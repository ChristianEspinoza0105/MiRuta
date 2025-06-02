package com.example.miruta.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.ui.theme.AppTypography

@Composable
fun RecoverPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isFieldFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = "Recover Password Icon",
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = "Recover your password",
                style = AppTypography.h1.copy(fontSize = 24.sp),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Enter your email address to receive a recovery code.",
                style = AppTypography.body1.copy(fontSize = 18.sp),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                BasicTextField(
                    value = email,
                    onValueChange = { newValue ->
                        email = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .onFocusChanged { isFieldFocused = it.isFocused },
                    textStyle = AppTypography.body1.copy(
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        autoCorrect = false
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (email.isEmpty() && !isFieldFocused) {
                                Text(
                                    text = "example@email.com",
                                    style = AppTypography.body1.copy(
                                        fontSize = 18.sp,
                                        color = Color.Gray
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Divider(
                    color = if (isFieldFocused) Color(0xFF00933B) else Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Recovery code sent to $email",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.popBackStack()
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00933B),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Send Verification Code",
                    style = AppTypography.h1.copy(fontSize = 18.sp)
                )
            }

            Text(
                text = "Back to login",
                style = AppTypography.body1.copy(fontSize = 16.sp),
                color = Color.Gray,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
        }
    }
}
