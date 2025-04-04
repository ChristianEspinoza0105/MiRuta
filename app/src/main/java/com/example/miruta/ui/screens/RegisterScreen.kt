package com.example.miruta.ui.screens

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
fun RegisterScreen(
navController: NavController,
authViewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
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
                        fontFamily = AppTypography.h1.fontFamily,
                        fontWeight = AppTypography.h1.fontWeight,
                        fontSize = titleFontSize
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 40.dp, top = 35.dp)
                )
                Text(
                    text = "Always on route. Sign in.",
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
                    text = "Sign in",
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
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(
                            text = "Name",
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
                    value = phone,
                    onValueChange = { phone = it },
                    label = {
                        Text(
                            text = "Phone number",
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
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = "Email",
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
                            text = "Password",
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

                Button(
                    onClick = {

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 45.dp, end = 45.dp)
                ) {
                    Text(
                        text = "Sign in",
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = AppTypography.button.fontFamily,
                            fontWeight = AppTypography.button.fontWeight,
                            fontSize = 24.sp
                        )
                    )
                }

                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.dp
                val subtitleFontSize = (screenWidth.value * 0.03).sp

                Text(
                    text = "Driver? Register here and track your routes in real-time!",
                    color = Color.DarkGray,
                    style = TextStyle(
                        fontFamily = AppTypography.body1.fontFamily,
                        fontWeight = AppTypography.body1.fontWeight,
                        fontSize = subtitleFontSize
                    ),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable {
                            navController.navigate("RegisterDriverScreen")
                        }
                )

            }
        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        val imageWidth = screenWidth * 0.4f
        val imageHeight = screenHeight * 0.2f
        val guideline = createGuidelineFromTop(0.090f)
        val guidelineStart = createGuidelineFromStart(0.5f)
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