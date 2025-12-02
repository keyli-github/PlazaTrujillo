package com.keyli.plazatrujillo.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecupContraseña(
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val repository = remember { AuthRepository() }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // Fondo decorativo superior mejorado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            OrangePrimary,
                            OrangePrimary.copy(alpha = 0.9f)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header mejorado con mejor alineación
            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de regreso mejorado
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        onClick = onNavigateToLogin
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    // Título bien alineado
                    Text(
                        text = "Recuperar Acceso",
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Card principal con mejor posicionamiento
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(tween(500))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(LightSurface),
                    elevation = CardDefaults.cardElevation(12.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    AnimatedContent(
                        targetState = isSuccess,
                        transitionSpec = {
                            (fadeIn(tween(700, easing = EaseInOut)) +
                                    scaleIn(
                                        initialScale = 0.85f,
                                        animationSpec = tween(700, easing = EaseInOut)
                                    )).togetherWith(
                                fadeOut(tween(400)) +
                                        scaleOut(
                                            targetScale = 0.85f,
                                            animationSpec = tween(400)
                                        )
                            )
                        },
                        label = "FormToSuccess"
                    ) { success ->
                        if (success) {
                            SuccessView(onBackToLogin = onNavigateToLogin)
                        } else {
                            RecoveryFormView(
                                email = email,
                                onEmailChange = {
                                    email = it
                                    errorMessage = null
                                },
                                isLoading = isLoading,
                                errorMessage = errorMessage,
                                onSubmit = {
                                    keyboardController?.hide()
                                    if (email.isBlank()) {
                                        errorMessage = "Ingresa tu correo electrónico"
                                        return@RecoveryFormView
                                    }

                                    isLoading = true
                                    scope.launch {
                                        delay(1200)
                                        val result = repository.sendPasswordRecovery(email.trim())
                                        isLoading = false

                                        if (result.isSuccess) {
                                            isSuccess = true
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.message
                                                ?: "Error al enviar correo"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Footer informativo mejorado
            if (!isSuccess) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(800, delayMillis = 400))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "¿Necesitas ayuda?",
                            color = TextGray.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Contacta a soporte",
                            color = OrangePrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { /* Acción de soporte */ }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun RecoveryFormView(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo animado desde drawable
        LogoHeader()

        Spacer(Modifier.height(24.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Ingresa tu correo electrónico y te enviaremos las instrucciones para restablecer tu contraseña.",
            fontSize = 14.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(36.dp))

        // Input mejorado
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Correo Electrónico",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = {
                    Text(
                        "ejemplo@correo.com",
                        color = TextGray.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = if (email.isNotEmpty()) OrangePrimary else TextGray.copy(alpha = 0.4f)
                    )
                },
                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
            )

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFE53935),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Botón mejorado con animación
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangePrimary,
                disabledContainerColor = OrangePrimary.copy(alpha = 0.6f)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            ),
            enabled = !isLoading
        ) {
            AnimatedContent(
                targetState = isLoading,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "buttonContent"
            ) { loading ->
                if (loading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Enviando...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Text(
                        "ENVIAR INSTRUCCIONES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessView(onBackToLogin: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono de éxito animado
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(OrangePrimary.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600, delayMillis = 200)) +
                    slideInVertically(tween(600, delayMillis = 200))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "¡Correo Enviado!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Hemos enviado un enlace de recuperación a tu correo electrónico.",
                    fontSize = 15.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Por favor, revisa tu bandeja de entrada y carpeta de spam.",
                    fontSize = 13.sp,
                    color = TextGray.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(Modifier.height(36.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(600, delayMillis = 400)) +
                    slideInVertically(tween(600, delayMillis = 400))
        ) {
            Button(
                onClick = onBackToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary
                ),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Text(
                    "VOLVER A INICIAR SESIÓN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun LogoHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
            .alpha(alpha)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logorecupp),
            contentDescription = "Logo Recuperación",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}