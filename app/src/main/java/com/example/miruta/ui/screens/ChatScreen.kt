package com.example.miruta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miruta.R
import com.example.miruta.data.models.ChatMessage
import com.example.miruta.data.repository.AuthRepository
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel
import com.example.miruta.ui.viewmodel.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(routeName: String, repository: AuthRepository) {

    val insets = WindowInsets.systemBars.asPaddingValues()

    val factory = AuthViewModelFactory(repository)
    val authViewModel: AuthViewModel = viewModel(factory = factory)

    val context = LocalContext.current
    val viewModel = viewModel<AuthViewModel>()

    val userData by viewModel.userData.collectAsState()
    val senderName = userData?.get("name")?.toString() ?: "Anónimo"

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(routeName) {
        viewModel.listenToMessages(routeName) { newMessages ->
            messages = newMessages
        }
    }

    Scaffold(
        backgroundColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = routeName,
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = AppTypography.h2.fontFamily,
                                fontWeight = AppTypography.h2.fontWeight,
                                fontSize = 34.sp
                            ),
                        )
                    }
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
                                        style = TextStyle(
                                            fontFamily = AppTypography.body1.fontFamily,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Text(
                                    text = msg.text,
                                    style = TextStyle(
                                        fontFamily = AppTypography.body1.fontFamily,
                                        fontWeight = AppTypography.body1.fontWeight,
                                        fontSize = 16.sp,
                                        color = if (isOwnMessage) Color(0xFFFFFFFF) else Color.Black
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formattedTime,
                                    color = if (isOwnMessage) Color(0xFFE0E0E0) else Color.DarkGray,
                                    style = TextStyle(
                                        fontFamily = AppTypography.body1.fontFamily,
                                        fontWeight = AppTypography.body1.fontWeight,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = {
                            Text(
                                "Message",
                                style = TextStyle(
                                    fontFamily = AppTypography.body1.fontFamily,
                                    fontWeight = AppTypography.body1.fontWeight,
                                    fontSize = 16.sp
                                )
                            )
                        },
                        textStyle = TextStyle(
                            fontFamily = AppTypography.body1.fontFamily,
                            fontWeight = AppTypography.body1.fontWeight,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0xFFFFFFFF),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (message.isNotBlank()) {
                                viewModel.sendMessage(routeName, message, senderName)
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

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
