package com.example.miruta.data.repository

import com.example.miruta.data.models.FavoriteLocation
import com.example.miruta.data.models.FavoriteRoute
import com.example.miruta.data.models.Routine
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

    fun addFavoriteLocation(
        userId: String,
        location: FavoriteLocation,
        onResult: (Boolean, String?) -> Unit
    ) {
        firestore.collection("users").document(userId)
            .collection("favoriteLocations")
            .document() // Auto-ID
            .set(location)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    // Para rutas favoritas
    fun addFavoriteRoute(
        userId: String,
        route: FavoriteRoute,
        onResult: (Boolean, String?) -> Unit
    ) {
        firestore.collection("users").document(userId)
            .collection("favoriteRoutes")
            .document() // Auto-ID
            .set(route)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message ?: "Error desconocido")
                }
            }
    }

    // Para rutinas
    fun addRoutine(
        userId: String,
        routine: Routine,
        onResult: (Boolean, String?) -> Unit
    ) {
        firestore.collection("users").document(userId)
            .collection("routines")
            .document() // Auto-ID
            .set(routine)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message ?: "Error desconocido")
                }
            }
    }

    // Funciones para obtener datos
    fun getFavoriteLocations(
        userId: String,
        onResult: (List<FavoriteLocation>?, String?) -> Unit
    ) {
        firestore.collection("users").document(userId)
            .collection("favoriteLocations")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val locations = task.result?.toObjects(FavoriteLocation::class.java)
                    onResult(locations, null)
                } else {
                    onResult(null, task.exception?.message ?: "Error ")
                }
            }
    }

    fun getFavoriteRoutes(
        userId: String,
        onResult: (List<FavoriteRoute>?, String?) -> Unit
    ){
        firestore.collection("users").document(userId)
            .collection("favoriteRoutes")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val routes = task.result?.toObjects(FavoriteRoute::class.java)
                    onResult(routes, null)
                }else{
                    onResult(null, task.exception?.message ?: "Error")
                }
            }
    }

    fun getRountines(
        userId: String,
        onResult: (List<Routine>?, String?) -> Unit
    ){
        firestore.collection("users").document(userId)
            .collection("Routines")
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val routines = task.result?.toObjects(Routine::class.java)
                    onResult(routines, null)
                }else{
                    onResult(null, task.exception?.message ?: "Error")
                }
            }
    }

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
