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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
enum class ReportStateIncidencia {
    IDLE, SAVING, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidenciasScreen(navController: NavHostController) {
    // --- ESTADOS DEL FORMULARIO ---
    var habitacionArea by remember { mutableStateOf("") }
    var problema by remember { mutableStateOf("") }

    // Estado para el Dropdown de Prioridad
    var prioridad by remember { mutableStateOf("Media") }
    var expandedPrioridad by remember { mutableStateOf(false) }
    val opcionesPrioridad = listOf("Baja", "Media", "Alta")

    // Estado para la animación de guardado
    var reportState by remember { mutableStateOf(ReportStateIncidencia.IDLE) }
    val scope = rememberCoroutineScope()

    // Estado para el scroll
    val scrollState = rememberScrollState()

    // --- ESTRUCTURA PRINCIPAL (SCAFFOLD) ---
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                ReportTopAppBar(navController)
            },
            bottomBar = {
                ReportBottomActionButtons(
                    onCancel = { navController.popBackStack() },
                    onReport = {
                        if (reportState == ReportStateIncidencia.IDLE) {
                            scope.launch {
                                // 1. Mostrar carga
                                reportState = ReportStateIncidencia.SAVING
                                delay(2000) // Simular proceso

                                // 2. Mostrar éxito
                                reportState = ReportStateIncidencia.SUCCESS
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
                    .imePadding() // Evita que el teclado tape los campos
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp) // Padding de contenido
            ) {
                // HEADER (Información)
                Text(
                    text = "Complete la información detallada sobre la avería.",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                    modifier = Modifier.padding(bottom = 24.dp, top = 10.dp)
                )

                // INPUT 1: HABITACIÓN / ÁREA
                ProfessionalReportInput(
                    label = "Habitación / Área *",
                    value = habitacionArea,
                    onValueChange = { habitacionArea = it },
                    placeholder = "Ej: Hab. 301, Pasillo piso 2..."
                )

                Spacer(modifier = Modifier.height(20.dp))

                // INPUT 2: PROBLEMA (Text Area)
                ProfessionalReportInput(
                    label = "Descripción del Problema *",
                    value = problema,
                    onValueChange = { problema = it },
                    placeholder = "Describa la avería en detalle...",
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // INPUT 3: PRIORIDAD (DROPDOWN)
                Text(
                    text = "Prioridad *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true,
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
                            disabledTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium)
                    )

                    // Capa invisible para detectar el clic
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedPrioridad = true }
                    )

                    DropdownMenu(
                        expanded = expandedPrioridad,
                        onDismissRequest = { expandedPrioridad = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .width(IntrinsicSize.Max)
                    ) {
                        opcionesPrioridad.forEach { opcion ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opcion,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if(opcion == prioridad) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    prioridad = opcion
                                    expandedPrioridad = false
                                }
                            )
                            if (opcion != opcionesPrioridad.last()) Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // --- OVERLAY DE ANIMACION (Con el efecto que te gusta) ---
        SuccessOverlayIncidencias(state = reportState)
    }
}

// ------------------------------------------------------------------------
// COMPONENTES REUTILIZABLES
// ------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTopAppBar(navController: NavHostController) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Text(
                "Reportar Incidencia",
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
private fun ReportBottomActionButtons(onCancel: () -> Unit, onReport: () -> Unit) {
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
                onClick = onReport, // CONECTADO AL ESTADO DE REPORTE
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Reportar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ProfessionalReportInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
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
            // AJUSTE: Usando MaterialTheme.colorScheme para modo oscuro/claro
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = OrangePrimary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

// --- OVERLAY ANIMADO (El efecto grande que te gusta) ---
@Composable
fun SuccessOverlayIncidencias(state: ReportStateIncidencia) {
    AnimatedVisibility(
        visible = state != ReportStateIncidencia.IDLE,
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
                        visible = state == ReportStateIncidencia.SAVING,
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            color = OrangePrimary,
                            strokeWidth = 6.dp
                        )
                    }

                    // CHECK
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state == ReportStateIncidencia.SUCCESS,
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

                Spacer(modifier = Modifier.height(32.dp))

                // TEXTO
                val titleText = if(state == ReportStateIncidencia.SAVING) "Reportando..." else "¡Incidencia Reportada!"
                val subText = if(state == ReportStateIncidencia.SAVING) "Procesando..." else "El informe fue enviado."

                androidx.compose.animation.AnimatedVisibility(
                    visible = state != ReportStateIncidencia.IDLE,
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