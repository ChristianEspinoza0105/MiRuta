package com.example.miruta.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.miruta.R
import com.example.miruta.data.models.ChatMessage
import com.example.miruta.data.repository.AuthRepository
import com.example.miruta.ui.components.ErrorMessageCard
import com.example.miruta.ui.components.LoadingSpinner
import com.example.miruta.ui.navigation.BottomNavScreen
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel
import com.example.miruta.ui.viewmodel.AuthViewModelFactory
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.*
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    routeName: String,
    repository: AuthRepository,
    navController: NavHostController
) {
    val factory = AuthViewModelFactory(repository)
    val viewModel: AuthViewModel = viewModel(factory = factory)

    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val userData by viewModel.userData.collectAsState()
    val senderName = userData?.get("name")?.toString() ?: "Anónimo"

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var message by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showLocationSheet = remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(routeName) {
        viewModel.listenToMessages(routeName) { newMessages ->
            messages = newMessages
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            LoadingSpinner(isLoading = true)
        }
    } else {
        Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = Color.Transparent
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.background_chat),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF00933B))
                        .height(56.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = routeName,
                        color = Color.White,
                        style = AppTypography.h2.copy(fontSize = 34.sp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { msg ->
                        val formattedTime = msg.timestamp?.toDate()?.let {
                            java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(it)
                        } ?: ""

                        val isOwnMessage = msg.senderId == FirebaseAuth.getInstance().currentUser?.uid

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Column(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (isOwnMessage) Color(0xFF00933B) else Color(0xFFCFE9DA)
                                    )
                                    .padding(14.dp)
                            ) {
                                if (msg.type == "location") {
                                    val lat = msg.latitude ?: 0.0
                                    val lon = msg.longitude ?: 0.0

                                    val mapIntent = {
                                        val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        intent.setPackage("com.google.android.apps.maps")
                                        context.startActivity(intent)
                                    }

                                    Text(
                                        text = "📍 Ubicación ",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOwnMessage) Color.White else Color.Black,
                                        modifier = Modifier.clickable {
                                            mapIntent()
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    val staticMapUrl =
                                        "https://maps.googleapis.com/maps/api/staticmap?center=${lat},${lon}&zoom=15&size=600x300&markers=color:red%7C${lat},${lon}&key=AIzaSyBNbNDkpZPUO-jY3TzUUW_WqNmstyy3AuY"

                                    Image(
                                        painter = rememberAsyncImagePainter(staticMapUrl),
                                        contentDescription = "Mapa estático",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                mapIntent()
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = msg.text,
                                        color = if (isOwnMessage) Color.White else Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formattedTime,
                                    style = AppTypography.body1.copy(
                                        fontSize = 10.sp,
                                        color = if (isOwnMessage) Color(0xFFE0E0E0) else Color.DarkGray
                                    ),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                if (isUserLoggedIn) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = message,
                            onValueChange = { message = it },
                            placeholder = { Text("Message") },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(24.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    showLocationSheet.value = true
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_app),
                                        contentDescription = "Ubicación"
                                    )
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (message.isNotBlank()) {
                                    viewModel.sendMessage(
                                        routeName,
                                        message,
                                        senderName,
                                        context,
                                        onError = { errorMessage = it }
                                    )
                                    message = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color(0xFF00933B),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_send),
                                contentDescription = "Enviar",
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(BottomNavScreen.Auth(false).route) {
                                    popUpTo(BottomNavScreen.Auth(false).route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00933B))
                        ) {
                            Text(
                                text = "Log in to send messages",
                                color = Color.White,
                                style = AppTypography.h1.copy(fontSize = 20.sp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            errorMessage?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 60.dp, start = 12.dp, end = 12.dp)
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                ) {
                    ErrorMessageCard(
                        message = "Error al enviar mensaje",
                        reason = it,
                        onDismiss = { errorMessage = null }
                    )
                }
            }

            if (showLocationSheet.value) {
                var userLoc by remember { mutableStateOf<LatLng?>(null) }
                val cameraState = rememberCameraPositionState()
                LaunchedEffect(hasLocationPermission) {
                    if (hasLocationPermission) {
                        val fused = LocationServices.getFusedLocationProviderClient(context)
                        val location = fused.lastLocation.await()
                        location?.let {
                            val latLng = LatLng(it.latitude, it.longitude)
                            userLoc = latLng
                            cameraState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                        }
                    }
                }

                ModalBottomSheet(
                    onDismissRequest = { showLocationSheet.value = false },
                    sheetState = sheetState
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(20.dp)
                    ) {
                        Text("Send location", style = AppTypography.h1, fontSize = 30.sp)
                        Spacer(Modifier.height(8.dp))

                        if (userLoc != null) {
                            GoogleMap(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp)),
                                cameraPositionState = cameraState
                            ) {
                                Marker(
                                    state = MarkerState(position = userLoc!!),
                                    title = "Tu ubicación"
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    userLoc?.let { loc ->
                                        viewModel.sendMessage(
                                            routeName,
                                            messageText = null,
                                            senderName = senderName,
                                            context = context,
                                            location = loc,
                                            onError = { errorMessage = it }
                                        )
                                        showLocationSheet.value = false
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00933B))
                            ) {
                                Text("Send your curret location", color = Color.White, style = AppTypography.h2, fontSize = 20.sp)
                            }
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}