package com.example.miruta.ui.screens

import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.miruta.R
import com.example.miruta.ui.components.ErrorMessageCard
import com.example.miruta.ui.navigation.BottomNavScreen
import com.example.miruta.ui.theme.AppTypography
import com.example.miruta.ui.viewmodel.AuthViewModel
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import com.example.miruta.data.gtfs.parseRoutesFromGTFS
import com.example.miruta.data.models.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDriverScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var plates by remember { mutableStateOf("") }
    var route by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val horizontalPadding = (screenWidth.value * 0.1).dp
    val fieldCornerRadius = (screenWidth.value * 0.05).dp
    val textFieldFontSize = (screenWidth.value * 0.04).sp

    LaunchedEffect(Unit) {
        val list = withContext(Dispatchers.IO) {
            context.assets.open("rutas_gtfs.zip").use { parseRoutesFromGTFS(it) }
        }
        routes = list
    }

    val routeOptions = remember(routes) {
        routes.map { route ->
            "${route.routeShortName} - ${route.routeLongName}"
        }
    }

    LaunchedEffect(Unit) {
        authViewModel.resetRegisterState()
    }

    errorMessage?.let { message ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .zIndex(2f)
        ) {
            ErrorMessageCard(
                message = message,
                reason = "Validation Error",
                onDismiss = { errorMessage = null }
            )
        }
    }

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
                .fillMaxHeight(1f)
                .background(Color(0xFF00933B))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                .fillMaxHeight(1f)
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
                    singleLine = true,
                )

                TextField(
                    value = phone,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() } && it.length <= 10) {
                            phone = it
                        }
                    },
                    label = { Text("Phone", fontSize = textFieldFontSize) },
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
                    label = { Text("Email", fontSize = textFieldFontSize) },
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

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 8.dp)
                ) {
                    TextField(
                        value = route,
                        onValueChange = {},
                        label = { Text("Route", fontSize = textFieldFontSize) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
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
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White)
                    ) {
                        routeOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = selectionOption,
                                        fontSize = textFieldFontSize,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    route = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = plates,
                    onValueChange = {
                        if (it.length <= 7) {
                            plates = it.uppercase()
                        }
                    },
                    label = { Text("Plates", fontSize = textFieldFontSize) },
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
                    label = { Text("Password", fontSize = textFieldFontSize) },
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
                        val trimmedRoute = route.trim()
                        val trimmedPlates = plates.trim()

                        when {
                            trimmedName.isEmpty() -> {
                                errorMessage = "Name is required"
                            }
                            trimmedName.length < 2 -> {
                                errorMessage = "Name must be at least 2 characters"
                            }
                            trimmedPhone.isEmpty() -> {
                                errorMessage = "Phone number is required"
                            }
                            trimmedPhone.length != 10 -> {
                                errorMessage = "Phone number must be 10 digits"
                            }
                            !isValidEmail(trimmedEmail) -> {
                                errorMessage = "Invalid email format"
                            }
                            trimmedRoute.isEmpty() -> {
                                errorMessage = "Route selection is required"
                            }
                            trimmedPlates.isEmpty() -> {
                                errorMessage = "License plate is required"
                            }
                            trimmedPlates.length < 6 -> {
                                errorMessage = "License plate must be at least 6 characters"
                            }
                            !trimmedPlates.matches(Regex("^[A-Z0-9-]+$")) -> {
                                errorMessage = "License plate can only contain letters, numbers, and hyphens"
                            }
                            trimmedPassword.length < 8 -> {
                                errorMessage = "Password must be at least 8 characters"
                            }
                            !trimmedPassword.matches(Regex("^(?=.*[A-Z])(?=.*[0-9]).+$")) -> {
                                errorMessage = "Password must contain at least one uppercase letter and one number"
                            }
                            else -> {
                                errorMessage = null
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
                    text = "Not a driver? Click here to continue as a user",
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
                    errorMessage = registerState
                }
            }
        }

        val imageWidth = screenWidth * 0.35f
        val imageHeight = screenHeight * 0.2f
        val guideline = createGuidelineFromTop(0.030f)
        val guidelineStart = createGuidelineFromStart(0.55f)
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