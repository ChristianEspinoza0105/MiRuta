package com.example.miruta.data.repository

import com.google.firebase.auth.FirebaseAuth
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
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        fetchUserData(uid, onResult)
                    } else {
                        onResult(false, "No se encontró el usuario")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    private fun fetchUserData(uid: String, onResult: (Boolean, String?) -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onResult(true, null)
                } else {
                    onResult(false, "Usuario no encontrado en Firestore")
                }
            }
            .addOnFailureListener {
                onResult(false, it.message)
            }
    }
}
