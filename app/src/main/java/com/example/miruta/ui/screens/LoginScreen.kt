package com.example.miruta.ui.screens

import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsState()
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        val (header, form, image) = createRefs()
        val guidelineTop = createGuidelineFromTop(0.3f)

        Box(
            modifier = Modifier
                .constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
                .height(175.dp)
                .background(Color(0xFF00933B))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp
                val screenHeight = configuration.screenHeightDp.dp

                val titleFontSize = (screenWidth.value * 0.10).sp
                val subtitleFontSize = (screenWidth.value * 0.04).sp
                Text(
                    text = "Welcome Back",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.h1.fontFamily,
                        fontWeight = AppTypography.h1.fontWeight,
                        fontSize = titleFontSize
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 40.dp, top = 30.dp)
                )
                Text(
                    text = "Always on route. Log in.",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.body1.fontFamily,
                        fontWeight = AppTypography.body1.fontWeight,
                        fontSize = subtitleFontSize
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 45.dp, top = 15.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .constrainAs(form) {
                    top.linkTo(guidelineTop)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight(0.92f)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Log In",
                    color = Color(0xFF00933B),
                    style = TextStyle(
                        fontFamily = AppTypography.h2.fontFamily,
                        fontWeight = AppTypography.h2.fontWeight,
                        fontSize = 48.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 45.dp, top = 20.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = "Correo electrónico",
                            style = TextStyle(
                                color = Color.DarkGray,
                                fontFamily = AppTypography.body1.fontFamily,
                                fontWeight = AppTypography.body1.fontWeight,
                                fontSize = 16.sp
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(top = 10.dp, start = 45.dp, end = 45.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF00933B),
                        unfocusedBorderColor = Color(0xFFE7E7E7),
                        backgroundColor = Color(0xFFE7E7E7)
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = "Contraseña",
                            style = TextStyle(
                                color = Color.DarkGray,
                                fontFamily = AppTypography.body1.fontFamily,
                                fontWeight = AppTypography.body1.fontWeight,
                                fontSize = 16.sp
                            )
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(top = 10.dp, start = 45.dp, end = 45.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF00933B),
                        unfocusedBorderColor = Color(0xFFE7E7E7),
                        backgroundColor = Color(0xFFE7E7E7)
                    )
                )

                Text(
                    text = "Forgot your password?",
                    color = Color.DarkGray,
                    style = TextStyle(
                        fontFamily = AppTypography.body1.fontFamily,
                        fontWeight = AppTypography.body1.fontWeight,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                )

                val loginState = remember { mutableStateOf<String?>(null) }

                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        val trimmedPassword = password.trim()

                        if (trimmedEmail.isEmpty()) {
                            Toast.makeText(context, "El correo electrónico no puede estar vacío.", Toast.LENGTH_SHORT).show()
                        } else if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                            Toast.makeText(context, "Correo electrónico no válido.", Toast.LENGTH_SHORT).show()
                        }

                        if (trimmedPassword.isEmpty()) {
                            Toast.makeText(context, "La contraseña no puede estar vacía.", Toast.LENGTH_SHORT).show()
                        }

                        if (trimmedEmail.isNotEmpty() && trimmedPassword.isNotEmpty() &&
                            trimmedEmail.contains("@") && trimmedEmail.contains(".") &&
                            trimmedPassword.isNotEmpty()) {
                            authViewModel.login(trimmedEmail, trimmedPassword)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 45.dp, end = 45.dp)
                ) {
                    Text(
                        text = "Log in",
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = AppTypography.button.fontFamily,
                            fontWeight = AppTypography.button.fontWeight,
                            fontSize = 24.sp
                        )
                    )
                    loginState.value?.let { state ->
                        Text(
                            text = state,
                            color = if (state == "Login exitoso") Color.Green else Color.Red,
                            modifier = Modifier.padding(top = 16.dp)
                        )

                        if (state == "Login exitoso") {
                            LaunchedEffect(state) {
                                navController.navigate("ProfileScreen") {
                                    popUpTo("LoginScreen") { inclusive = true }
                                }
                                loginState.value = null
                            }
                        } else if (state != null) {
                            LaunchedEffect(state) {
                                Toast.makeText(context, state, Toast.LENGTH_LONG).show()
                                loginState.value = null
                            }
                        }
                    }
                }

                Text(
                    text = "Don’t have an account? Sign in",
                    color = Color.DarkGray,
                    style = TextStyle(
                        fontFamily = AppTypography.body1.fontFamily,
                        fontWeight = AppTypography.body1.fontWeight,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 20.dp)
                        .clickable {
                            navController.navigate("RegisterScreen")
                        }
                )
            }
        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val imageWidth = screenWidth * 0.5f
        val imageHeight = screenHeight * 0.2f
        val guideline = createGuidelineFromTop(0.090f)
        val guidelineStart = createGuidelineFromStart(0.5f)
        Image(
            painter = painterResource(id = R.drawable.ic_routelogin),
            contentDescription = "Logo",
            modifier = Modifier
                .constrainAs(image) {
                    top.linkTo(guideline)
                    start.linkTo(guidelineStart)
                    end.linkTo(parent.end)
                }
                .width(imageWidth)
                .height(imageHeight)
                .zIndex(1f)
        )
    }
}
