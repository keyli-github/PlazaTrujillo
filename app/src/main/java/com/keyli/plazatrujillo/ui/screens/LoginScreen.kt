package com.keyli.plazatrujillo.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.R
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // Estado: ¿La app ya cargó los recursos iniciales?
    var isAppReady by remember { mutableStateOf(false) }

    // Simulación de carga de configuración inicial (Splash)
    LaunchedEffect(Unit) {
        delay(2500) // 2.5 segundos de "Splash Screen"
        isAppReady = true
    }

    // Transición suave entre Splash y Login
    Crossfade(
        targetState = isAppReady,
        animationSpec = tween(durationMillis = 800), // Transición elegante de casi 1 segundo
        label = "SplashToLogin"
    ) { ready ->
        if (!ready) {
            ProfessionalSplashScreen()
        } else {
            LoginFormContent(onLoginSuccess)
        }
    }
}

// ==========================================
// 1. PANTALLA DE CARGA (SPLASH) PROFESIONAL
// ==========================================
@Composable
fun ProfessionalSplashScreen() {
    // Animación de escala para el logo (efecto "respiración" al entrar)
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = LinearOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightSurface), // Fondo blanco puro limpio
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo con animación de escala
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value)
                    .background(color = Color.Transparent)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Hotel Plaza Trujillo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Indicador de carga personalizado
            CircularProgressIndicator(
                color = OrangePrimary,
                trackColor = OrangeSecondary.copy(alpha = 0.3f), // Pista suave de fondo
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Iniciando sistema...",
                color = TextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }

        // Copyright elegante al pie
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "HOTEL PLAZA TRUJILLO",
                color = OrangePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp // Espaciado para toque premium
            )
        }
    }
}

// ==========================================
// 2. CONTENIDO DEL LOGIN (FORMULARIO)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginFormContent(onLoginSuccess: () -> Unit) {
    // Estados del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Estado de carga interna (cuando presionas el botón Login)
    var isLoggingIn by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground) // Usamos tu color Gris Suave
    ) {
        // --- FONDO SUPERIOR NARANJA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.40f) // Un poco más corto para estilizar
                .background(
                    color = OrangePrimary,
                    shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp) // Curva más pronunciada
                )
        )

        // --- CONTENIDO ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Logo Circular (Con sombra para efecto 3D)
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                color = LightSurface,
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo Hotel",
                        modifier = Modifier
                            .padding(20.dp)
                            .size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hotel Plaza Trujillo",
                color = TextWhite, // Tu color blanco definido
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TARJETA DE LOGIN ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(28.dp) // Más padding interno para "aire"
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bienvenido",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )

                    Text(
                        text = "Panel Administrativo",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // === CAMPO EMAIL ===
                    LoginTextFieldLabel(text = "Correo Electrónico")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        enabled = !isLoggingIn,
                        placeholder = { Text("admin@plazatrujillo.com", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // === CAMPO PASSWORD ===
                    LoginTextFieldLabel(text = "Contraseña")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        enabled = !isLoggingIn,
                        placeholder = { Text("••••••••", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextGray) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !isLoggingIn) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = if (passwordVisible) OrangePrimary else TextGray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color.LightGray,
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack
                        )
                    )

                    // === EXTRAS (Checkbox) ===
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                enabled = !isLoggingIn,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = OrangePrimary,
                                    uncheckedColor = TextGray
                                )
                            )
                            Text("Recordarme", fontSize = 13.sp, color = TextGray)
                        }
                        TextButton(onClick = { }, enabled = !isLoggingIn) {
                            Text(
                                "Olvidé mi clave",
                                fontSize = 13.sp,
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // === BOTÓN PRINCIPAL CON ESTADO DE CARGA ===
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            isLoggingIn = true
                            // Simular petición al servidor
                            scope.launch {
                                delay(2000)
                                isLoggingIn = false
                                onLoginSuccess()
                            }
                        },
                        enabled = !isLoggingIn,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp), // Botón más alto para fácil toque
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            disabledContainerColor = OrangeSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoggingIn) {
                            CircularProgressIndicator(
                                color = TextWhite,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("VALIDANDO...", color = TextWhite, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = "INICIAR SESIÓN",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Hotel Plaza Trujillo © 2025",
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "v1.0.0 Enterprise Edition",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Pequeño helper para etiquetas de texto uniformes
@Composable
fun LoginTextFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextBlack,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    PlazaTrujilloTheme(darkTheme = false) {
        LoginScreen(onLoginSuccess = {})
    }
}