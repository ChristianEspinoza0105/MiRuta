package com.example.miruta.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
    var message by remember { mutableStateOf("") } // <- ESTO FALTABA

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
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .height(56.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = routeName,
                            color = Color(0xFF00933B),
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
                        .padding(horizontal = 12.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { msg ->
                        val isOwnMessage = msg.senderId == FirebaseAuth.getInstance().currentUser?.uid
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isOwnMessage) Color(0xFFDCF8C6) else Color.White)
                                    .padding(12.dp)
                            ) {
                                if (!isOwnMessage) {
                                    Text(text = msg.senderName, fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(text = msg.text, fontSize = 16.sp, color = Color.Black)
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
                        placeholder = { Text("Escribe un mensaje...") },
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
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Send, contentDescription = "Enviar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
