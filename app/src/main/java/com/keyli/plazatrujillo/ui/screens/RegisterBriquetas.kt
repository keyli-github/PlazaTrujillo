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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Estados de guardado
enum class SaveStateBriquetas {
    IDLE, SAVING, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBriquetasScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Estados de UI
    var saveState by remember { mutableStateOf(SaveStateBriquetas.IDLE) }

    // Campos
    var cantidad by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    // Dropdown
    var estado by remember { mutableStateOf("Operativo") }
    var expandedEstado by remember { mutableStateOf(false) }
    val opcionesEstado = listOf("Operativo", "En Mantenimiento", "Fuera de Servicio")

    // --- CONTENEDOR PRINCIPAL ---
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            // Eliminamos .imePadding() de aquí y lo ponemos en el Modifier principal del Box si es necesario
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // AJUSTE: TopAppBar estándar para un mejor manejo de la barra de estado
                CenterAlignedTopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            "Registro de Briquetas",
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
            },
            bottomBar = {
                // AJUSTE: Botonera con padding de barra de navegación
                BottomActionButtonsBriquetas(
                    onCancel = { navController.popBackStack() },
                    onSave = {
                        if (saveState == SaveStateBriquetas.IDLE) {
                            scope.launch {
                                saveState = SaveStateBriquetas.SAVING
                                delay(2000) // Simular proceso

                                saveState = SaveStateBriquetas.SUCCESS
                                delay(1500)

                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->

            Column(
                // AJUSTE: El paddingValues ya contiene el espacio para las barras
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    // Usamos .imePadding() para que el teclado empuje el contenido
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp) // Reducido el padding horizontal para más espacio
            ) {

                // --- HEADER (simplificado ya que usamos TopAppBar) ---
                Text(
                    text = "Datos de Consumo",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                    modifier = Modifier.padding(bottom = 16.dp, top = 10.dp)
                )

                // --- FORMULARIO ---
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    ProfessionalBriquetasInput(
                        label = "Cantidad de Briquetas *",
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        placeholder = "Ej. 50",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    ProfessionalBriquetasInput(
                        label = "Fecha *",
                        value = fecha,
                        onValueChange = { fecha = it },
                        placeholder = "dd/mm/aaaa",
                        trailingIcon = Icons.Default.DateRange
                    )

                    ProfessionalBriquetasInput(
                        label = "Hora *",
                        value = hora,
                        onValueChange = { hora = it },
                        placeholder = "00:00",
                        trailingIcon = Icons.Default.Schedule
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // DROPDOWN DE ESTADO
                Text(
                    text = "Estado Operativo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Desplegar", tint = OrangePrimary) },
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

                    // Invisible click handler
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedEstado = true }
                    )

                    DropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .width(IntrinsicSize.Max) // Ajuste para que el menú se ajuste al ancho del TextField
                    ) {
                        opcionesEstado.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    estado = opcion
                                    expandedEstado = false
                                }
                            )
                            if (opcion != opcionesEstado.last()) Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // --- OVERLAY DE ANIMACION ---
        SuccessOverlayBriquetas(state = saveState)
    }
}

// ------------------------------------------------------------------------
// UI AUXILIAR
// ------------------------------------------------------------------------

@Composable
private fun BottomActionButtonsBriquetas(onCancel: () -> Unit, onSave: () -> Unit) {
    Surface(
        // AJUSTE: Sombra para efecto flotante y padding para la barra de navegación
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
                onClick = onSave,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Registrar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// --- INPUT PERSONALIZADO (MANTENIENDO TU DISEÑO) ---
@Composable
fun ProfessionalBriquetasInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
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
            keyboardOptions = keyboardOptions,
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

// --- OVERLAY ANIMADO (AJUSTADO PARA SER UN DIÁLOGO CENTRAL) ---
// --- OVERLAY ANIMADO (RESTAURADO AL EFECTO GRANDE Y PROMINENTE) ---
@Composable
fun SuccessOverlayBriquetas(state: SaveStateBriquetas) {
    AnimatedVisibility(
        visible = state != SaveStateBriquetas.IDLE,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        // AJUSTE: Usamos .systemBarsPadding() aquí para asegurar que el overlay cubra todo, incluyendo las barras
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)) // Fondo más oscuro para enfoque
                .clickable(enabled = false) {}
                .systemBarsPadding(), // Asegura que cubra la barra de estado y navegación
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(contentAlignment = Alignment.Center) {

                    // LOADING
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state == SaveStateBriquetas.SAVING,
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp), // Un poco más grande
                            color = OrangePrimary,
                            strokeWidth = 6.dp
                        )
                    }

                    // CHECK (EL EFECTO GRANDE QUE TE GUSTA)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state == SaveStateBriquetas.SUCCESS,
                        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn()
                    ) {
                        // Usamos un Box más grande para el círculo de éxito
                        Box(
                            modifier = Modifier
                                .size(140.dp) // Círculo de éxito mucho más grande
                                .clip(CircleShape)
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Éxito",
                                tint = Color.White,
                                modifier = Modifier.size(70.dp) // Icono de check enorme
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // TEXTO ANIMADO
                val titleText = if(state == SaveStateBriquetas.SAVING) "Registrando..." else "¡Registro Exitoso!"
                val subText = if(state == SaveStateBriquetas.SAVING) "Cargando..." else "Datos guardados."

                androidx.compose.animation.AnimatedVisibility(
                    visible = state == SaveStateBriquetas.SUCCESS,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) + scaleIn(initialScale = 0.8f)
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
                // Texto de carga que aparece si no hay éxito
                androidx.compose.animation.AnimatedVisibility(
                    visible = state == SaveStateBriquetas.SAVING,
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