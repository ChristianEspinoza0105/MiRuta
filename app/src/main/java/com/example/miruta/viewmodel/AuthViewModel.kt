package com.example.miruta.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.miruta.data.RutaClassifier
import com.example.miruta.data.models.ChatMessage
import com.example.miruta.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository,
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

    //Conexión de usuario

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

    //Registro

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

    //Login

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

    //Cerrar sesión

    fun logout() {
        authRepository.logoutUser()
        _isUserLoggedIn.value = false
    }

    //Carga de datos

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

    //Enviado de mensaje y guardado

    fun sendMessage(routeId: String, messageText: String, senderName: String, context: Context) {
        //Filtrado de mensajes (primera capa)
        if (!isMessageAllowed(messageText)) {
            println("Mensaje bloqueado por filtro rápido: $messageText")
            return
        }

        //Filtrado de mensajes (segunda capa)
        val clasificador = RutaClassifier(context)
        if (!clasificador.esMensajeRelacionado(messageText)) {
            println("❌ Mensaje no relacionado con rutas, descartado.")
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val message = mapOf(
            "text" to messageText,
            "senderId" to currentUser.uid,
            "senderName" to senderName,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("chats").document(routeId)
            .collection("messages").add(message)
    }

    //Filtrado de mensajes (primera capa)

    private fun isMessageAllowed(text: String): Boolean {
        val forbiddenWords = listOf(
            "idiota", "estupido", "imbecil", "pendejo", "pendeja", "tonto", "tonta", "gilipollas",
            "mierda", "puta", "puto", "puta madre", "coño", "joder", "cabron", "cabrón", "polla",
            "culo", "verga", "pinche", "chingar", "chingada", "chingón", "zorra", "maricón", "marica",
            "pedo", "mamón", "mamona", "culero", "culera", "cojones", "joto", "pajero", "pajera",
            "tarado", "tarada", "soplapollas", "puto amo", "cabrona", "culia", "culiado", "cagada",
            "cagón", "cagona", "mamonazo", "mamaguevo", "vergón", "verga", "chinga", "chinga tu madre",
            "puta que te pario", "puta madre", "perra", "perro", "maldito", "maldita", "mierdoso",
            "mierdosa", "estúpida", "pendejazo", "boludo", "boluda", "pelotudo", "pelotuda",
            "tarado mental", "idiotez", "imbecil mental", "lesbiana", "gay", "homosexual",
            "putamadre", "cabronazo", "chupapollas", "gonorrea", "culiarse", "gilipollas",
            "gilipollez", "mariconazo", "mamabicho", "bastardo", "sidoso", "sidosa",
            "chucha", "huevón", "huevona", "huevonazo", "chingado", "chingada madre",
            "pene", "vagina", "vulva", "chocho", "chochito", "mamada", "putazo", "putita", "pinche",
            "puteada", "chingue tu madre", "chinga tu madre",
            // Inglés
            "fuck", "shit", "bitch", "asshole", "dick", "pussy", "cock", "cunt", "bastard", "damn",
            "crap", "bollocks", "bugger", "bloody", "arsehole", "wanker", "prick", "twat", "fucker",
            "motherfucker", "nigger", "nigga", "slut", "whore", "douche", "douchebag", "retard",
            "dumbass", "shithead", "moron", "loser", "idiot", "stupid", "jerk", "asswipe",
            "cockface", "fuckface", "dickhead", "dickweed", "asshat", "shitbag", "fuckboy", "shitface",
            "twatface", "bitchass", "dipshit", "shitfuck", "twatwaffle", "clusterfuck", "shitstorm",
            "jackass", "cumdumpster", "assclown", "shitshow",
            // Violencia y discriminación
            "terrorista", "racista", "homofobo", "misogino", "machista",
            "asesino", "asesina", "matar", "muerte", "violador", "violacion", "violento",
            "genocida", "terrorismo", "asesinato", "golpear", "golpeador", "golpista",
            "racismo", "intolerancia", "discriminacion", "exterminio", "genocidio",
            "exclusion", "opresion", "dictador", "tortura", "secuestrar", "secuestrador",
            // Sexo explícito
            "porno", "pornografia", "sexo", "sexual", "masturbacion", "orgasmo", "follar",
            "penetracion", "coito", "masturbarse", "pechos", "tetas", "nalgas", "ejaculacion",
            "porn", "fetiche", "pajearse",
            // Spam
            "gratis", "dinero facil", "trabajo desde casa", "hazte rico", "oferta especial",
            "gana dinero", "inversion segura", "click aqui", "suscribete", "visita",
            "comprar ahora", "haz clic", "oferta", "promocion", "regalo", "premio",
            "ganar", "ganancias", "multiplica tu dinero", "facil dinero",
            "trabaja desde casa", "trabajo rapido", "oportunidad unica", "invierte ahora",
            "dinero rapido", "comprar", "descarga gratis", "envio gratis", "promo", "oferton"
        )


        val lowerText = text.lowercase().trim()
        var normalized = text.lowercase().normalizeToAscii().replace(Regex("[^a-z0-9]"), "")

        // Bloquea mensajes vacíos o muy cortos
        if (lowerText.isBlank() || lowerText.length < 3) return false

        // Bloquea mensajes con solo caracteres no alfanuméricos
        if (!lowerText.any { it.isLetterOrDigit() }) return false

        // Bloquea mensajes que contienen links
        if (lowerText.contains("http://") || lowerText.contains("https://") || lowerText.contains("www.")) return false

        // Bloquea si contiene alguna palabra prohibida
        for (word in forbiddenWords) {
            val cleanWord = word.lowercase().normalizeToAscii().replace(Regex("[^a-z0-9]"), "")
            if (cleanWord.isNotEmpty() && normalized.contains(cleanWord)) {
                return false
            }
        }

        // Bloquea mensajes con demasiadas letras repetidas consecutivas
        val maxRepeated = 5
        var count = 1
        for (i in 1 until lowerText.length) {
            if (lowerText[i] == lowerText[i - 1]) {
                count++
                if (count > maxRepeated) return false
            } else {
                count = 1
            }
        }

        // Bloquea abuso de mayúsculas
        if (lowerText.length > 5) {
            val uppercaseCount = text.count { it.isUpperCase() }
            val uppercaseRatio = uppercaseCount.toDouble() / text.length
            if (uppercaseRatio > 0.7) return false
        }

        return true
    }

    fun String.normalizeToAscii(): String {
        val normalized = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
        return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalized, "")
    }


    //Actualización de mensajes

    private var listenerRegistration: ListenerRegistration? = null

    fun listenToMessages(routeId: String, onMessagesChanged: (List<ChatMessage>) -> Unit) {
        listenerRegistration?.remove()
        listenerRegistration = firestore.collection("chats").document(routeId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) return@addSnapshotListener

                val msgs = snapshots.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                onMessagesChanged(msgs)
            }
    }
}


class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}