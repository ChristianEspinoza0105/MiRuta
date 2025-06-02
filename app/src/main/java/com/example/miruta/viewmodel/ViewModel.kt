package com.example.miruta.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.miruta.data.ml.RutaClassifier
import com.example.miruta.data.models.ChatMessage
import com.example.miruta.data.repository.AuthRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.miruta.data.models.FavoriteLocation
import com.example.miruta.data.models.FavoriteRoute
import com.example.miruta.data.models.Routine
import com.example.miruta.data.models.RoutineStop

@HiltViewModel
class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel() {

    //Variables de la pestaña MyRoutes
    private val _favoriteLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favoriteLocations: StateFlow<List<FavoriteLocation>> = _favoriteLocations

    private  val _favoriteRoutes = MutableStateFlow<List<FavoriteRoute>>(emptyList())
    val favoriteRoute: StateFlow<List<FavoriteRoute>> = _favoriteRoutes

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines

    //Variables de Firebase
    internal val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _loginState = MutableStateFlow<String?>(null)
    val loginState: StateFlow<String?> = _loginState

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _registerState = MutableStateFlow<String?>(null)
    val registerState: StateFlow<String?> = _registerState

    private val _userData = MutableStateFlow<Map<String, Any>?>(null)
    val userData: StateFlow<Map<String, Any>?> = _userData

    var userName by mutableStateOf("")
        private set

    var userPhone by mutableStateOf("")
        private set

    var userEmail by mutableStateOf("")
        private set

    var plates by mutableStateOf("")
        private set

    var route by mutableStateOf("")
        private set

    var role by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(true)
        private set

    var photoIndex by mutableStateOf("0")
        private set

    init {
        getInitialData()
    }

    //Conexión de usuario

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _isUserLoggedIn.value = user != null
        Log.d(
            "AuthViewModel",
            "Estado de usuario: ${if (_isUserLoggedIn.value) "Conectado" else "Desconectado"}"
        )

        if (user != null && _userData.value == null) {
            fetchUserData(user.uid)
        } else if (user == null) {
            _userData.value = null
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchFavoriteLocations(currentUser.uid)
            fetchFavoriteRoutes(currentUser.uid)
            fetchRoutines(currentUser.uid)
        }
    }


    //Funciones de MyRoute
    fun addFavoriteLocation(location: FavoriteLocation) {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                authRepository.addFavoriteLocation(currentUser.uid, location) { success, message ->
                    if (success) {
                        // Actualizar estado local si es necesario
                    } else {
                        Log.e("AuthViewModel", "Error adding location: $message")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error adding location", e)
            }
        }
    }

    fun addFavoriteRoute(route: FavoriteRoute){
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        viewModelScope.launch {
            try {
                val routeRef = firestore.collection("users").document(userId)
                    .collection("favoriteRoutes").document()

                val routeWithId = route.copy(id = routeRef.id)
                routeRef.set(routeWithId)
                    .addOnSuccessListener {
                        fetchFavoriteRoutes(userId)
                    }
                }catch (e: Exception){
                    Log.e("AuthViewModel", "Error adding favorite route", e)
            }
        }
    }

    fun addRoutine(routine: Routine){
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        viewModelScope.launch {
            try{
                val routineRef = firestore.collection("users").document(userId)
                    .collection("routines").document()

                val routineWithId = routine.copy(id= routineRef.id, userId = userId)
                    routineRef.set(routineWithId)
                        .addOnSuccessListener {
                            fetchRoutines(userId)
                        }
            }catch (e: Exception){
                Log.e("AuthViewModel", "Error adding routine", e)
            }
        }
    }

    private fun fetchFavoriteLocations(userId: String) {
        firestore.collection("users").document(userId)
            .collection("favoriteLocations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AuthViewModel", "Error fetching favorite locations", error)
                    return@addSnapshotListener
                }

                val locations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FavoriteLocation::class.java)
                } ?: emptyList()

                _favoriteLocations.value = locations
            }
    }

    private fun fetchFavoriteRoutes(userId: String) {
        firestore.collection("users").document(userId)
            .collection("favoriteRoutes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AuthViewModel", "Error fetching favorite routes", error)
                    return@addSnapshotListener
                }

                val routes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FavoriteRoute::class.java)
                } ?: emptyList()

                _favoriteRoutes.value = routes
            }
    }

    private fun fetchRoutines(userId: String) {
        firestore.collection("users").document(userId)
            .collection("routines")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AuthViewModel", "Error fetching routines", error)
                    return@addSnapshotListener
                }

                val routines = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Routine::class.java)
                } ?: emptyList()

                _routines.value = routines
            }
    }
    fun addNewFavoriteLocation(location: FavoriteLocation) {
        val userId = auth.currentUser?.uid ?: return
        authRepository.addFavoriteLocation(userId, location) { success, message ->
            if (!success) {
                Log.e("AuthViewModel", "Error adding location: $message")
            }
        }
    }


    fun addNewRoute(routeShortName: String, routeLongName: String) {
        val currentUser = auth.currentUser ?: run {
            Log.e("AuthViewModel", "Usuario no autenticado")
            return
        }

        val favoriteRoute = FavoriteRoute(
            routeId = routeShortName,
            routeName = routeShortName,
            routeDescription = routeLongName,
            isFavorite = true
        )

        viewModelScope.launch {
            try {
                authRepository.addFavoriteRoute(currentUser.uid, favoriteRoute) { success, message ->
                    if (success) {

                        _favoriteRoutes.value = _favoriteRoutes.value + favoriteRoute
                        Log.d("AuthViewModel", "Ruta favorita añadida con éxito")
                    } else {
                        Log.e("AuthViewModel", "Error al añadir ruta favorita: $message")

                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al añadir ruta favorita", e)
            }
        }
    }

    fun addNewRoutine(
        routineName: String,
        stops: List<RoutineStop>
    ) {
        val currentUser = auth.currentUser ?: run {
            Log.e("AuthViewModel", "Usuario no autenticado")
            return
        }

        val routine = Routine(
            name = routineName,
            stops = stops,
            userId = currentUser.uid
        )

        viewModelScope.launch {
            try {
                authRepository.addRoutine(currentUser.uid, routine) { success, message ->
                    if (success) {
                        // Actualizar el estado local
                        _routines.value = _routines.value + routine
                        Log.d("AuthViewModel", "Rutina añadida con éxito")
                    } else {
                        Log.e("AuthViewModel", "Error al añadir rutina: $message")
                        // Puedes mostrar un mensaje al usuario aquí
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al añadir rutina", e)
            }
        }
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

    fun registerDriver(
        email: String,
        password: String,
        name: String,
        phone: String,
        route: String,
        plates: String
    ) {
        viewModelScope.launch {
            authRepository.registerDriver(
                email,
                password,
                name,
                phone,
                route,
                plates
            ) { success, message ->
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

    fun getInitialData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    userName = document.getString("name") ?: "User"
                    photoIndex = document.getString("photoIndex") ?: "0"
                }
            }
            .addOnFailureListener {
                userName = "User"
                photoIndex = "0"
            }
    }

    fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    userEmail = document.getString("email") ?: ""
                    userName = document.getString("name") ?: ""
                    userPhone = document.getString("phone") ?: ""
                    photoIndex = document.getString("photoIndex") ?: ""
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    fun loadDriverData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            firestore.collection("drivers").document(uid).get()
                .addOnSuccessListener { document ->
                    userEmail = document.getString("email") ?: ""
                    userName = document.getString("name") ?: ""
                    userPhone = document.getString("phone") ?: ""
                    plates = document.getString("plates") ?: ""
                    route = document.getString("route") ?: ""
                    photoIndex = document.getString("photoIndex") ?: ""
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    fun updateUserData(name: String, phone: String, email: String, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val updatedData = hashMapOf(
                "name" to name,
                "phone" to phone,
                "email" to email
            )

            firestore.collection("users").document(uid)
                .update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    onResult(true)
                }
                .addOnFailureListener {
                    onResult(false)
                }
        } else {
            onResult(false)
        }
    }

    fun updateDriverData(name: String, phone: String, email: String, route: String, plates: String, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val updatedData = hashMapOf(
                "name" to name,
                "phone" to phone,
                "email" to email,
                "plates" to plates,
                "route" to route
            )

            firestore.collection("drivers").document(uid)
                .update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    onResult(true)
                }
                .addOnFailureListener {
                    onResult(false)
                }
        } else {
            onResult(false)
        }
    }

    fun updateUserAvatar(avatarIndex: Int, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid

            val userRef = firestore.collection("users").document(uid)
            val driverRef = firestore.collection("drivers").document(uid)

            userRef.get()
                .addOnSuccessListener { userDoc ->
                    if (userDoc.exists()) {
                        // Usuario existe en 'users'
                        userRef.update("photoIndex", avatarIndex.toString())
                            .addOnSuccessListener {
                                photoIndex = avatarIndex.toString()
                                onResult(true)
                            }
                            .addOnFailureListener {
                                onResult(false)
                            }
                    } else {
                        // Si no está en 'users', intenta en 'drivers'
                        driverRef.update("photoIndex", avatarIndex.toString())
                            .addOnSuccessListener {
                                photoIndex = avatarIndex.toString()
                                onResult(true)
                            }
                            .addOnFailureListener {
                                onResult(false)
                            }
                    }
                }
                .addOnFailureListener {
                    onResult(false)
                }
        } else {
            onResult(false)
        }
    }

    fun refreshUserData(uid: String? = null) {
        val userId = uid ?: auth.currentUser?.uid ?: return

        val userDocRef = firestore.collection("users").document(userId)
        userDocRef.get().addOnSuccessListener { userDoc ->
            if (userDoc != null && userDoc.exists()) {
                userName = userDoc.getString("name") ?: "User"
                photoIndex = userDoc.getString("photoIndex") ?: "0"
                role = userDoc.getString("role") ?: "user"
            } else {
                // Si no está en 'users', intentar en 'drivers'
                val driverDocRef = firestore.collection("drivers").document(userId)
                driverDocRef.get().addOnSuccessListener { driverDoc ->
                    if (driverDoc != null && driverDoc.exists()) {
                        userName = driverDoc.getString("name") ?: "Driver"
                        photoIndex = driverDoc.getString("photoIndex") ?: "0"
                        role = driverDoc.getString("role") ?: "driver"
                    }
                }
            }
        }
    }

    //Cerrar sesión

    fun logout() {
        authRepository.logoutUser()

        _isUserLoggedIn.value = false
        _loginState.value = null
        _registerState.value = null
        _userData.value = null

        userName = ""
        userEmail = ""
        userPhone = ""
        photoIndex = "0"
        isLoading = true

        Log.d("AuthViewModel", "Sesión cerrada. Datos del usuario limpiados.")
    }

    fun resetRegisterState() {
        _registerState.value = null
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

    //Mensajes

    //Filtrado de mensajes
    private fun isMessageAllowed(text: String): MessageValidationResult {
        val forbiddenWords = listOf(
            "idiota",
            "estupido",
            "imbecil",
            "pendejo",
            "pendeja",
            "tonto",
            "tonta",
            "gilipollas",
            "mierda",
            "puta",
            "puto",
            "puta madre",
            "coño",
            "joder",
            "cabron",
            "cabrón",
            "polla",
            "culo",
            "verga",
            "pinche",
            "chingar",
            "chingada",
            "chingón",
            "zorra",
            "maricón",
            "marica",
            "pedo",
            "mamón",
            "mamona",
            "culero",
            "culera",
            "cojones",
            "joto",
            "pajero",
            "pajera",
            "tarado",
            "tarada",
            "soplapollas",
            "puto amo",
            "cabrona",
            "culia",
            "culiado",
            "cagada",
            "cagón",
            "cagona",
            "mamonazo",
            "mamaguevo",
            "vergón",
            "verga",
            "chinga",
            "chinga tu madre",
            "puta que te pario",
            "puta madre",
            "perra",
            "perro",
            "maldito",
            "maldita",
            "mierdoso",
            "mierdosa",
            "estúpida",
            "pendejazo",
            "boludo",
            "boluda",
            "pelotudo",
            "pelotuda",
            "tarado mental",
            "idiotez",
            "imbecil mental",
            "lesbiana",
            "gay",
            "homosexual",
            "putamadre",
            "cabronazo",
            "chupapollas",
            "gonorrea",
            "culiarse",
            "gilipollas",
            "gilipollez",
            "mariconazo",
            "mamabicho",
            "bastardo",
            "sidoso",
            "sidosa",
            "chucha",
            "huevón",
            "huevona",
            "huevonazo",
            "chingado",
            "chingada madre",
            "pene",
            "vagina",
            "vulva",
            "chocho",
            "chochito",
            "mamada",
            "putazo",
            "putita",
            "pinche",
            "puteada",
            "chingue tu madre",
            "chinga tu madre",
            "fuck",
            "shit",
            "bitch",
            "asshole",
            "dick",
            "pussy",
            "cock",
            "cunt",
            "bastard",
            "damn",
            "crap",
            "bollocks",
            "bugger",
            "bloody",
            "arsehole",
            "wanker",
            "prick",
            "twat",
            "fucker",
            "motherfucker",
            "nigger",
            "nigga",
            "slut",
            "whore",
            "douche",
            "douchebag",
            "retard",
            "dumbass",
            "shithead",
            "moron",
            "loser",
            "idiot",
            "stupid",
            "jerk",
            "asswipe",
            "cockface",
            "fuckface",
            "dickhead",
            "dickweed",
            "asshat",
            "shitbag",
            "fuckboy",
            "shitface",
            "twatface",
            "bitchass",
            "dipshit",
            "shitfuck",
            "twatwaffle",
            "clusterfuck",
            "shitstorm",
            "jackass",
            "cumdumpster",
            "assclown",
            "shitshow",
            "terrorista",
            "racista",
            "homofobo",
            "misogino",
            "machista",
            "asesino",
            "asesina",
            "matar",
            "muerte",
            "violador",
            "violacion",
            "violento",
            "genocida",
            "terrorismo",
            "asesinato",
            "golpear",
            "golpeador",
            "golpista",
            "racismo",
            "intolerancia",
            "discriminacion",
            "exterminio",
            "genocidio",
            "exclusion",
            "opresion",
            "dictador",
            "tortura",
            "secuestrar",
            "secuestrador",
            "porno",
            "pornografia",
            "sexo",
            "sexual",
            "masturbacion",
            "orgasmo",
            "follar",
            "penetracion",
            "coito",
            "masturbarse",
            "pechos",
            "tetas",
            "nalgas",
            "ejaculacion",
            "porn",
            "fetiche",
            "pajearse",
            "gratis",
            "dinero facil",
            "trabajo desde casa",
            "hazte rico",
            "oferta especial",
            "gana dinero",
            "inversion segura",
            "click aqui",
            "suscribete",
            "visita",
            "comprar ahora",
            "haz clic",
            "oferta",
            "promocion",
            "regalo",
            "premio",
            "ganar",
            "ganancias",
            "multiplica tu dinero",
            "facil dinero",
            "trabaja desde casa",
            "trabajo rapido",
            "oportunidad unica",
            "invierte ahora",
            "dinero rapido",
            "comprar",
            "descarga gratis",
            "envio gratis",
            "promo",
            "oferton"
        )

        val lowerText = text.lowercase().trim()
        val normalized = text.lowercase().normalizeToAscii().replace(Regex("[^a-z0-9]"), "")

        if (lowerText.isBlank() || lowerText.length < 3)
            return MessageValidationResult.Denied("El mensaje es muy corto o está vacío.")

        if (!lowerText.any { it.isLetterOrDigit() })
            return MessageValidationResult.Denied("El mensaje no contiene caracteres alfanuméricos.")

        if (lowerText.contains("http://") || lowerText.contains("https://") || lowerText.contains("www."))
            return MessageValidationResult.Denied("Los enlaces no están permitidos en el mensaje.")

        for (word in forbiddenWords) {
            val cleanWord = word.lowercase().normalizeToAscii().replace(Regex("[^a-z0-9]"), "")
            if (cleanWord.isNotEmpty() && normalized.contains(cleanWord)) {
                return MessageValidationResult.Denied("El mensaje contiene palabras no permitidas: \"$word\".")
            }
        }

        val maxRepeated = 5
        var count = 1
        for (i in 1 until lowerText.length) {
            if (lowerText[i] == lowerText[i - 1]) {
                count++
                if (count > maxRepeated) return MessageValidationResult.Denied("El mensaje contiene demasiadas letras repetidas consecutivas.")
            } else {
                count = 1
            }
        }

        if (lowerText.length > 5) {
            val uppercaseCount = text.count { it.isUpperCase() }
            val uppercaseRatio = uppercaseCount.toDouble() / text.length
            if (uppercaseRatio > 0.7) return MessageValidationResult.Denied("El mensaje contiene abuso de mayúsculas.")
        }

        val words = lowerText.split(Regex("\\s+"))
        val wordFrequency = mutableMapOf<String, Int>()
        val maxRepeatedWords = 5

        for (word in words) {
            val normalizedWord = word.normalizeToAscii().replace(Regex("[^a-z0-9]"), "")
            if (normalizedWord.isNotEmpty()) {
                wordFrequency[normalizedWord] = wordFrequency.getOrDefault(normalizedWord, 0) + 1
                if (wordFrequency[normalizedWord]!! > maxRepeatedWords)
                    return MessageValidationResult.Denied("El mensaje contiene palabras repetidas demasiadas veces: \"$word\".")
            }
        }

        return MessageValidationResult.Allowed
    }

    fun sendMessage(
        routeId: String,
        messageText: String?,
        senderName: String,
        context: Context,
        location: LatLng? = null,
        liveLocation: Boolean = false,
        onError: (String) -> Unit
    ) {
        if (messageText == null && location == null && !liveLocation) {
            onError("El mensaje o la ubicación deben estar presentes")
            return
        }

        if (messageText != null) {
            when (val validation = isMessageAllowed(messageText)) {
                is MessageValidationResult.Denied -> {
                    onError(validation.reason)
                    return
                }
                is MessageValidationResult.Allowed -> { }
                else -> {
                    onError("Error desconocido en la validación del mensaje.")
                    return
                }
            }

            val clasificador = RutaClassifier(context)
            if (!clasificador.esMensajeRelacionado(messageText)) {
                onError("Tu mensaje no está relacionado con rutas.")
                return
            }
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onError("Usuario no autenticado.")
            return
        }

        val message = when {
            liveLocation && location != null -> mapOf(
                "type" to "live_location",
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "senderId" to currentUser.uid,
                "senderName" to senderName,
                "timestamp" to FieldValue.serverTimestamp()
            )
            location != null -> mapOf(
                "type" to "location",
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "senderId" to currentUser.uid,
                "senderName" to senderName,
                "timestamp" to FieldValue.serverTimestamp()
            )
            else -> mapOf(
                "type" to "text",
                "text" to messageText,
                "senderId" to currentUser.uid,
                "senderName" to senderName,
                "timestamp" to FieldValue.serverTimestamp()
            )
        }


        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(routeId)
            .collection("messages")
            .add(message)
            .addOnFailureListener { e ->
                onError("Error al enviar el mensaje: ${e.message}")
            }
    }


    fun listenToMessages(routeId: String, onMessagesChanged: (List<ChatMessage>) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("chats")
            .document(routeId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener

                val messages = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                }

                onMessagesChanged(messages)
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

sealed class MessageValidationResult {
    object Allowed : MessageValidationResult()
    data class Denied(val reason: String) : MessageValidationResult()
}

fun String.normalizeToAscii(): String {
    val normalized = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
    return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalized, "")
}
