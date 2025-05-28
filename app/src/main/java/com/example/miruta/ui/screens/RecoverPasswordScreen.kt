package com.example.miruta.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.miruta.ui.theme.AppTypography
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RecoverPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00933B))
    ) {
        val (form) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(form) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .padding(32.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Recover Password",
                    color = Color(0xFF00933B),
                    style = TextStyle(
                        fontFamily = AppTypography.h2.fontFamily,
                        fontWeight = AppTypography.h2.fontWeight,
                        fontSize = 32.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Caja con sombra para el TextField
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(50))
                        .background(Color.White, RoundedCornerShape(50))
                ) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp), // relleno dentro del borde con sombra
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
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
                            Toast.makeText(context, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                        } else {
                            FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(trimmedEmail)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Correo de recuperación enviado",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error al enviar correo: ${task.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00933B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Send Recovery Email",
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = AppTypography.button.fontFamily,
                            fontWeight = AppTypography.button.fontWeight,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}
