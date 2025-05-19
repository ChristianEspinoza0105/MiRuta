package com.example.miruta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.miruta.R
import com.example.miruta.data.models.ChatMessage
import com.example.miruta.data.repository.AuthRepository
import com.example.miruta.ui.components.ErrorMessageCard
import com.example.miruta.ui.navigation.BottomNavScreen
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel
import com.example.miruta.ui.viewmodel.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

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

    LaunchedEffect(routeName) {
        viewModel.listenToMessages(routeName) { newMessages ->
            messages = newMessages
        }
    }

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
                                if (!isOwnMessage) {
                                    Text(
                                        text = msg.senderName,
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        style = AppTypography.body1,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Text(
                                    text = msg.text,
                                    style = AppTypography.body1.copy(
                                        color = if (isOwnMessage) Color.White else Color.Black
                                    )
                                )
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
                ModalBottomSheet(
                    onDismissRequest = { showLocationSheet.value = false },
                    sheetState = sheetState
                ) {
                    Text(
                        text = "Ubicación próximamente",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = AppTypography.body1
                    )
                }
            }
        }
    }
}