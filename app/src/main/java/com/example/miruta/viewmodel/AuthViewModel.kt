package com.example.miruta.ui.viewmodel

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

    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        _isUserLoggedIn.value = user != null
    }

    fun login(email: String, password: String) {
        authRepository.loginUser(email, password) { success, message ->
            if (success) {
                _loginState.value = "Login exitoso"
                checkUserLoggedIn()
            } else {
                _loginState.value = message
            }
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _isUserLoggedIn.value = false
    }
}


