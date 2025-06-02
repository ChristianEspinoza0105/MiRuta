package com.example.miruta.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import com.example.miruta.data.repository.LiveLocationSharing
import com.example.miruta.utils.getLocationFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    routeName: String,
    repository: AuthRepository,
    navController: NavHostController,
    onBackClick: () -> Unit,
    onCircleIconClick: () -> Unit,
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showLocationSheet = remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val statusBarHeightPx = WindowInsets.statusBars.getTop(density)
    val statusBarHeightDp = with(density) { statusBarHeightPx.toDp() }

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

    LaunchedEffect(routeName, isUserLoggedIn) {
        if (isUserLoggedIn) {
            viewModel.listenToMessages(routeName) { newMessages ->
                val updatedMessages = messages.toMutableList()

                for (newMsg in newMessages) {
                    val index = updatedMessages.indexOfFirst { it.id == newMsg.id }

                    if (index != -1) {
                        updatedMessages[index] = newMsg
                    } else {
                        updatedMessages.add(newMsg)
                    }
                }

                messages = updatedMessages
                isLoading = false
            }
        } else {
            messages = emptyList()
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
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            LoadingSpinner(isLoading = true)
        }
    } else {
        Scaffold(
            containerColor = Color.Transparent
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
                    if (isUserLoggedIn) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp + statusBarHeightDp)
                                .background(
                                    color = Color(0xFF00933B),
                                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                                )
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = statusBarHeightDp,
                                    bottom = 10.dp
                                )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                IconButton(
                                    onClick = onBackClick,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_back),
                                        contentDescription = "Regresar",
                                        modifier = Modifier.size(24.dp),
                                        colorFilter = ColorFilter.tint(Color.White)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .clickable { onCircleIconClick() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_chat),
                                            contentDescription = "Acción",
                                            modifier = Modifier.size(30.dp),
                                            colorFilter = ColorFilter.tint(Color(0xFF00933B))
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = routeName,
                                        color = Color.White,
                                        style = AppTypography.headlineMedium.copy(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 1.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
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
                                        if (msg.type == "location" || msg.type == "live_location") {
                                            val lat = msg.latitude ?: 0.0
                                            val lon = msg.longitude ?: 0.0

                                            val mapIntent = {
                                                if (msg.type == "location") {
                                                    val uri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                                    intent.setPackage("com.google.android.apps.maps")
                                                    context.startActivity(intent)
                                                } else {
                                                    navController.navigate("live_location_map/${msg.liveLocationDocId}/$routeName")
                                                }
                                            }

                                            Text(
                                                text = if (msg.type == "location") "📍 Ubicación" else "📍 Ubicación en vivo",
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
                                            style = AppTypography.bodyLarge.copy(
                                                fontSize = 10.sp,
                                                color = if (isOwnMessage) Color(0xFFE0E0E0) else Color.DarkGray
                                            ),
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
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
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(id = R.drawable.ic_share),
                                            contentDescription = "Ubicación"
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
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
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(32.dp)
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Authentication Required",
                                    tint = Color(0xFF00933B),
                                    modifier = Modifier.size(80.dp)
                                )

                                Text(
                                    text = "Your route. Your community.",
                                    style = AppTypography.headlineLarge.copy(fontSize = 24.sp),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Log in to access routes, chat with other passengers, and get real-time updates.",
                                    style = AppTypography.bodyLarge.copy(fontSize = 18.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                Button(
                                    onClick = {
                                        navController.navigate(BottomNavScreen.Auth(false).route) {
                                            popUpTo(BottomNavScreen.Auth(false).route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00933B),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .widthIn(min = 200.dp)
                                ) {
                                    Text(
                                        text = "Sign in",
                                        style = AppTypography.headlineLarge.copy(fontSize = 24.sp)
                                    )
                                }

                                Text(
                                    text = "Back to explore more!",
                                    style = AppTypography.bodyLarge.copy(fontSize = 16.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate(BottomNavScreen.Community.route) {
                                                popUpTo(BottomNavScreen.Community.route) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                )
                            }

                            val annotatedText = buildAnnotatedString {
                                append("Don't have an account yet? ")
                                pushStringAnnotation(tag = "CREATE_ACCOUNT", annotation = "create_account")
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF00933B),
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append("Create one now")
                                }
                                pop()
                                append(" and join the community!")
                            }

                            ClickableText(
                                text = annotatedText,
                                style = AppTypography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center
                                ),
                                onClick = { offset ->
                                    annotatedText.getStringAnnotations(tag = "CREATE_ACCOUNT", start = offset, end = offset)
                                        .firstOrNull()?.let {
                                            navController.navigate("RegisterScreen") {
                                                popUpTo("RegisterScreen") { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                            )
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
                        sheetState = sheetState,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        tonalElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .border(
                                            width = 4.dp,
                                            color = Color(0xFF00933B),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .size(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_share),
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Share location",
                                    style = AppTypography.headlineLarge,
                                    fontSize = 22.sp,
                                )

                                Spacer(modifier = Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (userLoc != null) {
                                GoogleMap(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                        .clip(RoundedCornerShape(16.dp)),
                                    cameraPositionState = cameraState
                                ) {
                                    Marker(
                                        state = MarkerState(position = userLoc!!),
                                        title = "Tu ubicación"
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

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
                                        .height(52.dp),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00933B),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        "Share your current location",
                                        style = AppTypography.headlineMedium,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                var liveSharing by remember { mutableStateOf<LiveLocationSharing?>(null) }

                                Button(
                                    onClick = {
                                        if (liveSharing == null) {
                                            val user = FirebaseAuth.getInstance().currentUser ?: return@Button
                                            val liveLocationSharing = LiveLocationSharing(routeName, senderName, user.uid)

                                            val locationFlow = getLocationFlow(context)

                                            liveLocationSharing.startSharing(context, locationFlow) { errorMsg ->
                                                errorMessage = errorMsg
                                            }
                                            liveSharing = liveLocationSharing
                                        } else {
                                            liveSharing?.stopSharing()
                                            liveSharing = null
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00933B),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text(
                                        "Share your live location",
                                        style = AppTypography.headlineMedium,
                                        fontSize = 18.sp
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    LoadingSpinner(isLoading = true)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}