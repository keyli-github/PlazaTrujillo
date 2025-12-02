package com.keyli.plazatrujillo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- ESTADO DE GUARDADO ---
enum class BloqueoState {
    IDLE, SAVING, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloqHabitacionScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // --- ESTADOS ---
    var habitacion by remember { mutableStateOf("") }
    var razon by remember { mutableStateOf("") }
    var fechaLiberacion by remember { mutableStateOf("") }
    var bloqueoState by remember { mutableStateOf(BloqueoState.IDLE) }

    // Estado para el Dropdown de Habitaciones
    var expandedHabitacion by remember { mutableStateOf(false) }

    // Lista exacta solicitada
    val opcionesHabitacion = listOf(
        "111 - Piso 1 (DE)", "112 - Piso 1 (DF)", "113 - Piso 1 (M)",
        "210 - Piso 2 (M)", "211 - Piso 2 (DF)", "212 - Piso 2 (DF)",
        "213 - Piso 2 (M)", "214 - Piso 2 (DF)", "215 - Piso 2 (M)",
        "310 - Piso 3 (M)", "311 - Piso 3 (DF)", "312 - Piso 3 (DF)",
        "313 - Piso 3 (M)", "314 - Piso 3 (DF)", "315 - Piso 3 (TF)"
    )

    val usuarioLogueado = "Marco Gutierrez" // Campo de solo lectura

    // --- ESTRUCTURA PRINCIPAL ---
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                BloqueoTopAppBar(navController)
            },
            bottomBar = {
                BloqueoBottomActionButtons(
                    onCancel = { navController.popBackStack() },
                    onBlock = {
                        if (bloqueoState == BloqueoState.IDLE) {
                            scope.launch {
                                // 1. Mostrar carga
                                bloqueoState = BloqueoState.SAVING
                                delay(2000) // Simular proceso

                                // 2. Mostrar éxito
                                bloqueoState = BloqueoState.SUCCESS
                                delay(1500)

                                // 3. Salir
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp) // Padding de contenido
            ) {
                // HEADER (Información)
                Text(
                    text = "Complete la información para bloquear la habitación por mantenimiento.",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                    modifier = Modifier.padding(bottom = 24.dp, top = 10.dp)
                )

                // INPUT 1: SELECCIONAR HABITACIÓN (DROPDOWN)
                Text(
                    text = "Habitación *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = habitacion,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Seleccione una habitación", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "Desplegar", tint = OrangePrimary)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = OrangePrimary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium)
                    )

                    // Capa invisible para detectar clic
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedHabitacion = true }
                    )

                    // Menú Desplegable con Scroll propio si la lista es larga
                    DropdownMenu(
                        expanded = expandedHabitacion,
                        onDismissRequest = { expandedHabitacion = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .heightIn(max = 250.dp)
                            .width(IntrinsicSize.Max)
                    ) {
                        opcionesHabitacion.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    habitacion = opcion
                                    expandedHabitacion = false
                                }
                            )
                            if (opcion != opcionesHabitacion.last()) Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // INPUT 2: RAZÓN (Área de texto)
                ProfessionalBloqueoInput(
                    label = "Razón de Bloqueo *",
                    value = razon,
                    onValueChange = { razon = it },
                    placeholder = "Describa el motivo del bloqueo...",
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // INPUT 3: FECHA ESTIMADA
                ProfessionalBloqueoInput(
                    label = "Fecha Estimada de Liberación *",
                    value = fechaLiberacion,
                    onValueChange = { fechaLiberacion = it },
                    placeholder = "dd/mm/aaaa",
                    trailingIcon = Icons.Default.DateRange
                )

                Spacer(modifier = Modifier.height(20.dp))

                // INPUT 4: USUARIO (SOLO LECTURA)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Bloqueado Por",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = usuarioLogueado,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        // Colores de un campo de solo lectura (ligeramente diferente)
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Se usa automáticamente el nombre del usuario logueado",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // --- OVERLAY DE ANIMACION ---
        SuccessOverlayBloqueo(state = bloqueoState)
    }
}

// ------------------------------------------------------------------------
// COMPONENTES REUTILIZABLES
// ------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BloqueoTopAppBar(navController: NavHostController) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                "Bloquear Habitación",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onSurface)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        windowInsets = TopAppBarDefaults.windowInsets.exclude(WindowInsets.statusBars).exclude(WindowInsets.navigationBars)
    )
}

@Composable
private fun BloqueoBottomActionButtons(onCancel: () -> Unit, onBlock: () -> Unit) {
    Surface(
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onBlock,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Bloquear", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ProfessionalBloqueoInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    singleLine: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = OrangePrimary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            trailingIcon = if (trailingIcon != null) {
                { Icon(trailingIcon, contentDescription = null, tint = OrangePrimary) }
            } else null
        )
    }
}

// --- OVERLAY ANIMADO
@Composable
fun SuccessOverlayBloqueo(state: BloqueoState) {
    AnimatedVisibility(
        visible = state != BloqueoState.IDLE,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = false) {}
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {

                    // LOADING
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state == BloqueoState.SAVING,
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(70.dp),
                            color = OrangePrimary,
                            strokeWidth = 6.dp
                        )
                    }

                    // CHECK
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state == BloqueoState.SUCCESS,
                        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Éxito",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // TEXTO
                val titleText = if(state == BloqueoState.SAVING) "Bloqueando Habitación..." else "¡Habitación Bloqueada!"
                val subText = if(state == BloqueoState.SAVING) "Procesando la solicitud..." else "La habitación ha sido bloqueada."

                androidx.compose.animation.AnimatedVisibility(
                    visible = state != BloqueoState.IDLE,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 200)),
                    exit = fadeOut(animationSpec = tween(200))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = subText,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}