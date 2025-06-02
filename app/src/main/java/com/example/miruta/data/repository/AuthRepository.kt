package com.example.miruta.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "Usuario no registrado"
                        is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                        else -> task.exception?.message ?: "Error desconocido"
                    }
                    onResult(false, errorMessage)
                }
            }
    }

    fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: run {
                        onResult(false, "Error al obtener usuario registrado")
                        return@addOnCompleteListener
                    }

                    val userData = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "email" to email,
                        "role" to "user",
                        "createdAt" to FieldValue.serverTimestamp(),
                        "photoIndex" to "",
                        "favorites" to emptyList<String>()
                    )

                    firestore.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            auth.currentUser?.delete()
                            onResult(false, "Error al guardar datos adicionales: ${e.message}")
                        }
                } else {
                    onResult(false, task.exception?.message ?: "Error desconocido")
                }
            }
    }

    fun registerDriver(
        email: String,
        password: String,
        name: String,
        phone: String,
        route: String,
        plates: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: run {
                        onResult(false, "Error al obtener conductor registrado")
                        return@addOnCompleteListener
                    }

                    val driverData = hashMapOf(
                        "name" to name,
                        "phone" to phone,
                        "email" to email,
                        "route" to route,
                        "plates" to plates,
                        "role" to "driver",
                        "createdAt" to FieldValue.serverTimestamp(),
                        "photoIndex" to "",
                    )

                    firestore.collection("drivers").document(uid)
                        .set(driverData)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            auth.currentUser?.delete()
                            onResult(false, "Error al guardar datos adicionales: ${e.message}")
                        }
                } else {
                    onResult(false, task.exception?.message ?: "Error desconocido")
                }
            }
    }

    fun logoutUser() {
        auth.signOut()
    }
}
