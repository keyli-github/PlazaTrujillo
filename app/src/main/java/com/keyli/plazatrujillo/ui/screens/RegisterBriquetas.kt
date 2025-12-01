package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBriquetasScreen(navController: NavHostController) {
    // --- ESTADOS DEL FORMULARIO ---
    var cantidad by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    // Estado para el Dropdown (Menú desplegable)
    var estado by remember { mutableStateOf("Operativo") }
    var expandedEstado by remember { mutableStateOf(false) }
    val opcionesEstado = listOf("Operativo", "En Mantenimiento", "Fuera de Servicio")

    // Estado del scroll
    val scrollState = rememberScrollState()

    // Scaffold maneja la estructura básica y el fondo
    Scaffold(
        containerColor = LightBackground, // Fondo gris suave global
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Evita que el teclado tape el contenido
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // 1. ÁREA DESLIZABLE (HEADER + INPUTS)
            // Usamos weight(1f) para que ocupe todo el espacio disponible menos los botones
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState) // ¡ESTO EVITA QUE SE APLASTE!
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                // --- HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cambio de Briquetas",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Registre la cantidad y el estado operativo del equipo.",
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

                // --- FORMULARIO ---

                // 1. Cantidad
                ProfessionalBriquetasInput(
                    label = "Cantidad de Briquetas *",
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    placeholder = "Ej. 50",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Fecha
                ProfessionalBriquetasInput(
                    label = "Fecha *",
                    value = fecha,
                    onValueChange = { fecha = it },
                    placeholder = "dd/mm/aaaa",
                    trailingIcon = Icons.Default.DateRange
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Hora
                ProfessionalBriquetasInput(
                    label = "Hora *",
                    value = hora,
                    onValueChange = { hora = it },
                    placeholder = "00:00",
                    trailingIcon = Icons.Default.Schedule
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Dropdown de Estado (Lógica profesional)
                Text(
                    text = "Estado Operativo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBlack,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true, // No se puede escribir, solo seleccionar
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, "Desplegar", tint = TextGray)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedEstado = true }, // Click abre el menú
                        enabled = false, // Deshabilitado visualmente para que funcione el click del Box
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = TextBlack,
                            disabledBorderColor = TextGray.copy(alpha = 0.5f),
                            disabledContainerColor = LightSurface,
                            disabledPlaceholderColor = TextBlack
                        ),
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Medium)
                    )

                    // Caja transparente invisible para capturar el clic
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedEstado = true }
                    )

                    DropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false },
                        modifier = Modifier.background(LightSurface)
                    ) {
                        opcionesEstado.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion, color = TextBlack) },
                                onClick = {
                                    estado = opcion
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }

                // Espacio extra al final para scroll cómodo
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. ÁREA DE BOTONES (FIJA AL FONDO)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LightSurface,
                shadowElevation = 10.dp // Sombra suave superior
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
                        onClick = { /* Lógica Guardar */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary, // Naranja oficial
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Registrar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- COMPONENTE INPUT REUTILIZABLE CON ESTILO PROFESIONAL ---
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
            color = TextBlack,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray.copy(alpha = 0.6f)) },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), // Bordes más modernos (12dp)
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LightSurface,
                unfocusedContainerColor = LightSurface,
                focusedBorderColor = OrangePrimary, // Naranja al enfocar
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