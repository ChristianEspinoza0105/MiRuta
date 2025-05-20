package com.example.miruta.ui.screens

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.style.TextAlign


@Composable
fun RegisterDriverScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var route by remember { mutableStateOf("") }
    var plates by remember { mutableStateOf("") }
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
                    text = "Welcome",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.h1.fontFamily,
                        fontWeight = AppTypography.h1.fontWeight,
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
                .fillMaxHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = "Sign up",
                    color = Color(0xFF00933B),
                    style = TextStyle(
                        fontFamily = AppTypography.h2.fontFamily,
                        fontWeight = AppTypography.h2.fontWeight,
                        fontSize = 48.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 45.dp, top = 10.dp)
                )


                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(50)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(50)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(50)
                )



                OutlinedTextField(
                    value = route,
                    onValueChange = { route = it },
                    label = { Text("Route") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(50)
                )



                OutlinedTextField(
                    value = plates,
                    onValueChange = { plates = it },
                    label = { Text("Plates") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(50)
                )


                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 45.dp, vertical = 8.dp)
                        .shadow(
                            elevation = 10.600000381469727.dp,
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x40000000)
                        )
                        .background(Color.White, RoundedCornerShape(40)),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(50)
                )


            Button(
                onClick = {
                    val trimmedEmail = email.trim()
                    val trimmedPassword = password.trim()
                    val trimmedName = name.trim()
                    val trimmedPhone = phone.trim()
                    val trimmedRoute = phone.trim()
                    val trimmedPlates = phone.trim()

                    when {
                        trimmedName.isEmpty() -> context?.let {
                            Toast.makeText(it, "Nombre requerido", Toast.LENGTH_SHORT)
                                .show()
                        }

                        trimmedPhone.isEmpty() -> context?.let {
                            Toast.makeText(it, "Teléfono requerido", Toast.LENGTH_SHORT)
                                .show()
                        }

                        !isValidEmail(trimmedEmail) -> context?.let {
                            Toast.makeText(it, "Email inválido", Toast.LENGTH_SHORT).show()
                        }

                        trimmedPassword.length < 6 -> context?.let {
                            Toast.makeText(
                                it,
                                "La contraseña debe tener al menos 6 caracteres",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        trimmedRoute.isEmpty() -> context?.let {
                            Toast.makeText(it, "Teléfono requerido", Toast.LENGTH_SHORT)
                                .show()
                        }

                        trimmedPlates.isEmpty() -> context?.let {
                            Toast.makeText(it, "Teléfono requerido", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {
                            authViewModel.registerDriver(
                                trimmedEmail,
                                trimmedPassword,
                                trimmedName,
                                trimmedPhone,
                                trimmedRoute,
                                trimmedPlates
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, start = 45.dp, end = 45.dp)
            ) {
                Text(
                    text = "Sign up",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = AppTypography.button.fontFamily,
                        fontWeight = AppTypography.button.fontWeight,
                        fontSize = 24.sp
                    )
                )
            }



            Text(
                text = "Are you an User? Click here",
                color = Color.DarkGray,
                style = TextStyle(
                    fontFamily = AppTypography.body1.fontFamily,
                    fontWeight = AppTypography.body1.fontWeight,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable {
                        navController.navigate("RegisterScreen")
                    },
                textAlign = TextAlign.Center
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

        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val imageWidth = screenWidth * 0.4f
        val imageHeight = screenHeight * 0.2f
        val guideline = createGuidelineFromTop(0.060f)
        val guidelineStart = createGuidelineFromStart(0.5f)
        Image(
            painter = painterResource(id = R.drawable.ic_driver),
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

