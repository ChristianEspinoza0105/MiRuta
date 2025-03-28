package com.example.miruta.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.miruta.data.repository.AuthRepository
import androidx.compose.runtime.State
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = mutableStateOf<String?>(null)
    val loginState: State<String?> = _loginState

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _isUserLoggedIn.value = user != null
        Log.d("AuthViewModel", "Estado de usuario: ${if (_isUserLoggedIn.value) "Conectado" else "Desconectado"}")
    }

    init {
        // Configurar el observador del estado de autenticación
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    // Llamado para verificar si el usuario está autenticado
    private fun checkUserLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        _isUserLoggedIn.value = user != null
        Log.d("AuthViewModel", "Estado de usuario: ${if (_isUserLoggedIn.value) "Conectado" else "Desconectado"}")
    }

    // Login
    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Intentando iniciar sesión con correo: $email")
        authRepository.loginUser(email, password) { success, message ->
            if (success) {
                _loginState.value = "Login exitoso"
                Log.d("AuthViewModel", "Inicio de sesión exitoso para: $email")
                checkUserLoggedIn()
            } else {
                _loginState.value = message
                Log.e("AuthViewModel", "Error al iniciar sesión: $message")
            }
        }
    }

    // Logout
    fun logout() {
        Log.d("AuthViewModel", "Cerrando sesión del usuario actual")
        FirebaseAuth.getInstance().signOut()
        _isUserLoggedIn.value = false
    }

    // Limpiar el observador cuando el ViewModel es destruido
    override fun onCleared() {
        super.onCleared()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

    // Actualizar el estado de autenticación en la aplicación
    fun updateUserLoggedInStatus(isLoggedIn: Boolean) {
        _isUserLoggedIn.value = isLoggedIn
    }
}
