package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloqHabitacionScreen(navController: NavHostController) {
    // --- ESTADOS ---
    var habitacion by remember { mutableStateOf("") }
    var razon by remember { mutableStateOf("") }
    var fechaLiberacion by remember { mutableStateOf("") }

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

    val usuarioLogueado = "Marco Gutierrez"
    val scrollState = rememberScrollState()

    // --- ESTRUCTURA PRINCIPAL ---
    Scaffold(
        containerColor = LightBackground, // Color de fondo global
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Ajuste automático con el teclado
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // 1. COLUMNA CON SCROLL (Para contenido)
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa todo el espacio menos los botones
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bloquear Habitación",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Complete la información para bloquear la habitación por mantenimiento.",
                            fontSize = 14.sp,
                            color = TextGray,
                            lineHeight = 20.sp
                        )
                    }
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextBlack)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // INPUT 1: SELECCIONAR HABITACIÓN (DROPDOWN)
                Text(
                    text = "Habitación *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBlack,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = habitacion,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Seleccione una habitación") }, // Placeholder pedido
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "Desplegar", tint = TextGray)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedHabitacion = true },
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = TextBlack,
                            disabledBorderColor = TextGray.copy(alpha = 0.5f),
                            disabledPlaceholderColor = TextGray,
                            disabledContainerColor = LightSurface
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
                            .background(LightSurface)
                            .heightIn(max = 250.dp) // Limita altura para que no tape todo
                    ) {
                        opcionesHabitacion.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion, color = TextBlack) },
                                onClick = {
                                    habitacion = opcion
                                    expandedHabitacion = false
                                }
                            )
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
                        color = TextBlack,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = usuarioLogueado,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = LightBackground,
                            unfocusedContainerColor = LightBackground,
                            focusedBorderColor = TextGray.copy(alpha = 0.2f),
                            unfocusedBorderColor = TextGray.copy(alpha = 0.2f),
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Se usa automáticamente el nombre del usuario logueado",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. BOTONES (FIJOS ABAJO)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LightSurface,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, TextGray.copy(alpha = 0.3f))
                    ) {
                        Text("Cancelar", color = TextBlack, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { /* Acción Guardar */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary, // Naranja Global
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Bloquear", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- COMPONENTE REUTILIZABLE PARA ESTA PANTALLA ---
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
            color = TextBlack,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray.copy(alpha = 0.6f)) },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LightSurface,
                unfocusedContainerColor = LightSurface,
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = TextGray.copy(alpha = 0.4f),
                cursorColor = OrangePrimary,
                focusedTextColor = TextBlack,
                unfocusedTextColor = TextBlack
            ),
            trailingIcon = if (trailingIcon != null) {
                { Icon(trailingIcon, contentDescription = null, tint = OrangePrimary) }
            } else null
        )
    }
}