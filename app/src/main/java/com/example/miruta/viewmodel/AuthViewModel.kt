package com.example.miruta.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruta.data.models.ChatMessage
import com.example.miruta.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _loginState = MutableStateFlow<String?>(null)
    val loginState: StateFlow<String?> = _loginState

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState: StateFlow<String?> = _registerState

    private val _userData = MutableStateFlow<Map<String, Any>?>(null)
    val userData: StateFlow<Map<String, Any>?> = _userData

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _isUserLoggedIn.value = user != null
        Log.d("AuthViewModel", "Estado de usuario: ${if (_isUserLoggedIn.value) "Conectado" else "Desconectado"}")

        if (user != null && _userData.value == null) {
            fetchUserData(user.uid)
        } else if (user == null) {
            _userData.value = null
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun register(email: String, password: String, name: String, phone: String) {
        viewModelScope.launch {
            authRepository.registerUser(email, password, name, phone) { success, message ->
                if (success) {
                    _registerState.value = "Registro exitoso"
                    fetchUserData(auth.currentUser?.uid ?: "")
                } else {
                    _registerState.value = message
                }
            }
        }
    }
    fun registerDriver(email: String, password: String, name: String, phone: String, route: String, plates: String) {
        viewModelScope.launch {
            authRepository.registerDriver(email, password, name, phone, route, plates) { success, message ->
                if (success) {
                    _registerState.value = "Registro exitoso"
                    fetchUserData(auth.currentUser?.uid ?: "")
                } else {
                    _registerState.value = message
                }
            }
        }
    }

    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Intentando iniciar sesión con correo: $email")
        authRepository.loginUser(email, password) { success, message ->
            if (success) {
                _loginState.value = "Login exitoso"
                Log.d("AuthViewModel", "Inicio de sesión exitoso para: $email")
            } else {
                _loginState.value = message
                Log.e("AuthViewModel", "Error al iniciar sesión: $message")
            }
        }
    }

    fun logout() {
        authRepository.logoutUser()
        _isUserLoggedIn.value = false
    }

    private fun fetchUserData(userId: String) {
        if (_userData.value != null) return
        viewModelScope.launch {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        _userData.value = document.data
                        Log.d("AuthViewModel", "Datos del usuario cargados: ${document.data}")
                    } else {
                        Log.d("AuthViewModel", "No se encontraron datos del usuario.")
                        _userData.value = null
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("AuthViewModel", "Error obteniendo datos del usuario", exception)
                }
        }
    }

    fun setUserLoggedIn(isLoggedIn: Boolean) {
        _isUserLoggedIn.value = isLoggedIn
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun createGroup(
        name: String,
        description: String,
        memberIds: List<String>,
        onResult: (Boolean, String?) -> Unit
    ) {
        val groupData = hashMapOf(
            "name" to name,
            "description" to description,
            "members" to memberIds,
            "createdAt" to FieldValue.serverTimestamp(),
            "createdBy" to FirebaseAuth.getInstance().currentUser?.uid
        )

        FirebaseFirestore.getInstance().collection("groups")
            .add(groupData)
            .addOnSuccessListener {
                onResult(true, it.id) // Retorna el ID del grupo creado
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun sendMessage(chatId: String, message: ChatMessage) {
        firestore.collection("groups")
            .document(message.groupId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("Chat", "Mensaje enviado")
            }
            .addOnFailureListener {
                Log.e("Chat", "Error al enviar mensaje", it)
            }
    }

    fun listenForGroupMessages(groupId: String, onMessageReceived: (ChatMessage) -> Unit) {
        firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Chat", "Error escuchando mensajes de grupo", e)
                    return@addSnapshotListener
                }

                for (doc in snapshots!!.documentChanges) {
                    if (doc.type.name == "ADDED") {
                        val message = doc.document.toObject(ChatMessage::class.java)
                        onMessageReceived(message)
                    }
                }
            }
    }

    fun getUserGroups(userId: String, onResult: (List<String>) -> Unit) {
        firestore.collection("groups")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { result ->
                val groupIds = result.documents.map { it.id }
                onResult(groupIds)
            }
            .addOnFailureListener {
                Log.e("Groups", "Error al obtener grupos", it)
                onResult(emptyList())
            }
    }

}
