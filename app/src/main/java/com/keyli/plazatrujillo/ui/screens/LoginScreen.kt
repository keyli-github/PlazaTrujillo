package com.keyli.plazatrujillo.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.R
import com.keyli.plazatrujillo.data.AuthRepository
import com.keyli.plazatrujillo.ui.theme.LightBackground
import com.keyli.plazatrujillo.ui.theme.LightSurface
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.theme.TextGray
import com.keyli.plazatrujillo.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// =======================================================
// LOGIN SCREEN MANAGER
// =======================================================

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    var isAppReady by remember { mutableStateOf(false) }

    // Tiempo de carga inicial (Splash)
    LaunchedEffect(Unit) {
        delay(3000)
        isAppReady = true
    }

    // Transición fluida: El Login sube suavemente cubriendo el Splash
    AnimatedContent(
        targetState = isAppReady,
        transitionSpec = {
            if (targetState) {
                (slideInVertically { height -> height / 10 } + fadeIn(tween(800)))
                    .togetherWith(fadeOut(tween(500)))
            } else {
                fadeIn(tween(0)).togetherWith(fadeOut(tween(0)))
            }
        },
        label = "SplashToLogin"
    ) { ready ->
        if (!ready) {
            ElegantSplashScreen()
        } else {
            LoginFormContent(onLoginSuccess, onNavigateToRecovery)
        }
    }
}

// =======================================================
// SPLASH SCREEN "ELEGANTE"
// =======================================================

@Composable
fun ElegantSplashScreen() {
    // 1. Animación de "Latido" (Pulse) para el fondo del logo
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    // 2. Animación de los puntos suspensivos del texto
    var dots by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            dots = ""
            delay(300)
            dots = "."
            delay(300)
            dots = ".."
            delay(300)
            dots = "..."
            delay(500)
        }
    }

    // 3. Animación de entrada del Logo (Rebote/Pop)
    val logoScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(1.2f).getInterpolation(it) }
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Fondo con un degradado muy sutil vertical (Blanco -> Gris muy claro)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFF5F5F5))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- CONTENEDOR DEL LOGO ---
            Box(contentAlignment = Alignment.Center) {
                // Círculo de "Pulso" detrás
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .background(OrangePrimary.copy(alpha = 0.3f), CircleShape)
                )

                // Logo principal dentro de un círculo elevado
                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(logoScale.value), // Aplica el rebote
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 10.dp, // Sombra elegante
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)) // Borde sutil
                ) {
                    Box(
                        modifier = Modifier.padding(25.dp), // Espacio interno del logo
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo Hotel",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // --- INDICADOR Y TEXTO ---
            // Barra de carga pequeña y minimalista
            LinearProgressIndicator(
                modifier = Modifier
                    .width(120.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = OrangePrimary,
                trackColor = OrangePrimary.copy(alpha = 0.2f)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Cargando sistema$dots", // Texto fijo + puntos animados
                color = TextGray,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }

        // Copyright o versión abajo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "Hotel Plaza Trujillo",
                fontSize = 12.sp,
                color = TextGray.copy(alpha = 0.4f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// =======================================================
// LOGIN FORM
// =======================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginFormContent(
    onLoginSuccess: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    // --- Estados ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isNetworkError by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }

    // --- Animación de Entrada ---
    var isFormVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isFormVisible = true }

    val cardOffset by animateDpAsState(
        targetValue = if (isFormVisible) 0.dp else 40.dp,
        animationSpec = tween(800, easing = LinearOutSlowInEasing), label = "cardOffset"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (isFormVisible) 1f else 0f,
        animationSpec = tween(800), label = "cardAlpha"
    )

    Box(modifier = Modifier.fillMaxSize().background(LightBackground)) {
        // Fondo naranja superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(OrangePrimary, RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Logo pequeño superior
            AnimatedVisibility(visible = isFormVisible, enter = scaleIn(tween(500, delayMillis = 200)) + fadeIn()) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = LightSurface,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(visible = isFormVisible, enter = fadeIn(tween(600, delayMillis = 400))) {
                Text("Hotel Plaza Trujillo", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            // Card Formulario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .offset(y = cardOffset)
                    .alpha(cardAlpha),
                colors = CardDefaults.cardColors(LightSurface),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Bienvenido", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("Panel Administrativo", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 24.dp))

                    // Llamada a la función helper corregida
                    LoginTextFieldLabel("Correo Electrónico")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        enabled = !isLoggingIn,
                        placeholder = { Text("ejemplo@correo.com", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = OrangePrimary) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    // Llamada a la función helper corregida
                    LoginTextFieldLabel("Contraseña")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        enabled = !isLoggingIn,
                        placeholder = { Text("••••••••", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = OrangePrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null, tint = TextGray)
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                            )
                            Text("Siempre conectado", fontSize = 12.sp, color = TextGray)
                        }
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            color = OrangePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onNavigateToRecovery() }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Por favor completa todos los campos"
                                isNetworkError = false
                                return@Button
                            }
                            isLoggingIn = true
                            errorMessage = null
                            isNetworkError = false
                            scope.launch {
                                val result = repository.loginWithFirebase(email.trim(), password.trim())
                                if (result.isSuccess) {
                                    onLoginSuccess()
                                } else {
                                    isLoggingIn = false
                                    val error = result.exceptionOrNull()
                                    errorMessage = error?.message ?: "Error desconocido"
                                    // Detectar error de red para el mensaje específico
                                    if (errorMessage!!.contains("red", ignoreCase = true) || errorMessage!!.contains("internet", ignoreCase = true) || errorMessage!!.contains("network", ignoreCase = true)) {
                                        isNetworkError = true
                                    }
                                }
                            }
                        },
                        enabled = !isLoggingIn,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoggingIn) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Validando...", color = Color.White)
                        } else {
                            Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold)
                        }
                    }

                    // --- INICIO DEL CAMBIO SOLICITADO ---
                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let { msg ->
                            Spacer(Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp)).padding(8.dp).fillMaxWidth()
                            ) {
                                val displayMessage: String
                                val displayIcon = if (isNetworkError) {
                                    // Mensaje específico para cuando falla la conexión de red
                                    displayMessage = "Por favor, conéctate a una red Wi-Fi o verifica tu conexión a Internet."
                                    Icons.Default.WifiOff
                                } else {
                                    // Mensaje genérico para errores de credenciales
                                    displayMessage = msg
                                    Icons.Default.Lock
                                }

                                Icon(displayIcon, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(text = displayMessage, color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }
                    // --- FIN DEL CAMBIO SOLICITADO ---
                }
            }
            Spacer(Modifier.weight(1f))
            Text("v1.0.0 Enterprise Edition", color = TextGray.copy(alpha = 0.5f), fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
        }
    }
}
@Composable
fun LoginTextFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}