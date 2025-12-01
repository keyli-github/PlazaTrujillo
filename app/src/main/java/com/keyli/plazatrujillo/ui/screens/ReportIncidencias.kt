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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIncidenciasScreen(navController: NavHostController) {
    // --- ESTADOS DEL FORMULARIO ---
    var habitacionArea by remember { mutableStateOf("") }
    var problema by remember { mutableStateOf("") }

    // Estado para el Dropdown de Prioridad
    var prioridad by remember { mutableStateOf("Media") } // Valor por defecto
    var expandedPrioridad by remember { mutableStateOf(false) }
    val opcionesPrioridad = listOf("Baja", "Media", "Alta")

    // Estado para el scroll
    val scrollState = rememberScrollState()

    // --- ESTRUCTURA PRINCIPAL (SCAFFOLD) ---
    Scaffold(
        containerColor = LightBackground, // Color de fondo global (Gris suave)
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Evita que el teclado tape los campos
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // 1. ÁREA DESLIZABLE (HEADER + INPUTS)
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa todo el espacio disponible menos los botones
                    .verticalScroll(scrollState) // ¡IMPORTANTE: Permite scroll!
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
                            text = "Reportar Incidencia",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Complete la información detallada sobre la incidencia o avería.",
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
                    modifier = Modifier.height(120.dp) // Más alto para escribir
                )

                Spacer(modifier = Modifier.height(20.dp))

                // INPUT 3: PRIORIDAD (DROPDOWN)
                Text(
                    text = "Prioridad *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBlack,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = prioridad,
                        onValueChange = {},
                        readOnly = true, // Solo lectura
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "Desplegar", tint = TextGray)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedPrioridad = true },
                        enabled = false, // Deshabilitado visualmente para usar el click del Box
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = TextBlack,
                            disabledBorderColor = TextGray.copy(alpha = 0.5f),
                            disabledPlaceholderColor = TextGray,
                            disabledContainerColor = LightSurface
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
                        modifier = Modifier.background(LightSurface)
                    ) {
                        opcionesPrioridad.forEach { opcion ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opcion,
                                        color = TextBlack,
                                        fontWeight = if(opcion == prioridad) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    prioridad = opcion
                                    expandedPrioridad = false
                                }
                            )
                        }
                    }
                }

                // Espacio final para scroll cómodo
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. BOTONES (FIJOS AL FINAL)
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
                        onClick = { /* Lógica de envío */ },
                        modifier = Modifier
                            .weight(1f) // Cambié 0.6f a 1f para que sean iguales (más estético)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangeSecondary, // Naranja secundario como pediste aquí
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text("Reportar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- COMPONENTE INPUT REUTILIZABLE ---
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
            )
        )
    }
}