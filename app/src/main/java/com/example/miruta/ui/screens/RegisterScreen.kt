package com.example.miruta.ui.screens

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.ui.navigation.BottomNavScreen
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel
import androidx.compose.material3.TextFieldDefaults

@Composable
fun RegisterScreen(
navController: NavController,
authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val horizontalPadding = (screenWidth.value * 0.1).dp
    val fieldCornerRadius = (screenWidth.value * 0.05).dp
    val textFieldFontSize = (screenWidth.value * 0.04).sp

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
                    text = "Hello",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.headlineLarge.fontFamily,
                        fontWeight = AppTypography.headlineLarge.fontWeight,
                        fontSize = titleFontSize
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 40.dp, top = 35.dp)
                )
                Text(
                    text = "Always on route. Sign up.",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.bodyLarge.fontFamily,
                        fontWeight = AppTypography.bodyLarge.fontWeight,
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
                    text = "Sign up",
                    color = Color(0xFF00933B),
                    style = TextStyle(
                        fontFamily = AppTypography.headlineMedium.fontFamily,
                        fontWeight = AppTypography.headlineMedium.fontWeight,
                        fontSize = 48.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 45.dp, top = 20.dp)
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", fontSize = textFieldFontSize) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 8.dp)
                        .shadow(
                            elevation = 20.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(fieldCornerRadius)),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )

                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone number", fontSize = textFieldFontSize) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 8.dp)
                        .shadow(
                            elevation = 20.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(fieldCornerRadius)),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", fontSize = textFieldFontSize)  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 8.dp)
                        .shadow(
                            elevation = 20.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(fieldCornerRadius)),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", fontSize = textFieldFontSize)},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 8.dp)
                        .shadow(
                            elevation = 20.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(fieldCornerRadius)),
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.Black,
                        ),
                    shape = RoundedCornerShape(50),
                    singleLine = true
                )

                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        val trimmedPassword = password.trim()
                        val trimmedName = name.trim()
                        val trimmedPhone = phone.trim()

                        when {
                            trimmedName.isEmpty() -> context?.let {
                                Toast.makeText(it, "Nombre requerido", Toast.LENGTH_SHORT).show()
                            }
                            trimmedPhone.isEmpty() -> context?.let {
                                Toast.makeText(it, "Teléfono requerido", Toast.LENGTH_SHORT).show()
                            }
                            !isValidEmail(trimmedEmail) -> context?.let {
                                Toast.makeText(it, "Email inválido", Toast.LENGTH_SHORT).show()
                            }
                            trimmedPassword.length < 6 -> context?.let {
                                Toast.makeText(it, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                authViewModel.register(trimmedEmail, trimmedPassword, trimmedName, trimmedPhone)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 45.dp, end = 45.dp)
                ) {
                    Text(
                        text = "Sign up",
                        color = Color.White,
                        style = AppTypography.titleMedium.copy(fontSize = 24.sp)
                    )
                }

                val registerState by authViewModel.registerState.collectAsState()

                LaunchedEffect(registerState) {
                    Log.d("RegisterState", "Register state: $registerState")
                    if (registerState == "Registro exitoso") {
                        authViewModel.setUserLoggedIn(true)
                        navController.navigate(BottomNavScreen.Explore.route) {
                            popUpTo("RegisterScreen") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (!registerState.isNullOrBlank()) {
                        Toast.makeText(context, registerState, Toast.LENGTH_LONG).show()
                    }
                }

                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp
                val subtitleFontSize = (screenWidth.value * 0.03).sp

                Text(
                    text = "Driver? Register here and track your routes in real-time!",
                    color = Color.DarkGray,
                    style = TextStyle(
                        fontFamily = AppTypography.bodyLarge.fontFamily,
                        fontWeight = AppTypography.bodyLarge.fontWeight,
                        fontSize = subtitleFontSize
                    ),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable {
                            navController.navigate("RegisterDriverScreen")
                        }
                )

                Text(
                    text = "Already have an account? Login here!",
                    color = Color.DarkGray,
                    style = TextStyle(
                        fontFamily = AppTypography.bodyLarge.fontFamily,
                        fontWeight = AppTypography.bodyLarge.fontWeight,
                        fontSize = subtitleFontSize
                    ),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable {
                            navController.navigate("LoginScreen")
                        }
                )

            }
        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val imageWidth = screenWidth * 0.35f
        val imageHeight = screenHeight * 0.2f
        val guideline = createGuidelineFromTop(0.090f)
        val guidelineStart = createGuidelineFromStart(0.55f)
        Image(
            painter = painterResource(id = R.drawable.ic_bus),
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

private fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}