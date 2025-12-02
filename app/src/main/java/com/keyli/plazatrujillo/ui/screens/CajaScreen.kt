package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.* // Asegúrate de importar tus colores del tema
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// --- CONFIGURACIÓN TABLA (Ajustada para eliminar espacios vacíos) ---
object TableConfig {
    val WidthTipo = 80.dp
    val WidthCliente = 140.dp
    val WidthMetodo = 90.dp
    val WidthMonto = 90.dp
    val WidthHora = 70.dp
    val WidthEstado = 100.dp

    // Padding lateral interno de la tabla
    val HorizontalPadding = 12.dp
}

data class Transaccion(
    val id: String,
    val fecha: String,
    val tipo: String,
    val cliente: String,
    val metodo: String,
    val monto: String,
    val hora: String,
    val estado: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CajaScreen(navController: NavHostController) {
    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val textMedium = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    // --- ESTADOS ---
    var fechaSeleccionada by remember { mutableStateOf("01/12/2025") }
    var mostrarCalendarioPrincipal by remember { mutableStateOf(false) }
    var showArqueoDialog by remember { mutableStateOf(false) }
    var showCobroDialog by remember { mutableStateOf(false) }

    // Datos
    val todasLasTransacciones = remember { getSampleTransactions() }
    val transaccionesFiltradas = remember(fechaSeleccionada) {
        todasLasTransacciones.filter { it.fecha == fechaSeleccionada }
    }

    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    // --- DIALOGOS ---
    if (showArqueoDialog) {
        ArqueoDeCajaDialog(onDismiss = { showArqueoDialog = false }, initialDate = fechaSeleccionada)
    }

    if (showCobroDialog) {
        RegistrarCobroDialog(onDismiss = { showCobroDialog = false })
    }

    // --- CALENDARIO POPUP ---
    if (mostrarCalendarioPrincipal) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioPrincipal = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        fechaSeleccionada = sdf.format(Date(selectedMillis))
                    }
                    mostrarCalendarioPrincipal = false
                }) { Text("Aceptar", color = primaryColor) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendarioPrincipal = false }) { Text("Cancelar", color = textMedium) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // --- CONTENIDO PRINCIPAL ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        HeaderSection(
            onArqueoClick = { showArqueoDialog = true },
            onNuevoCobroClick = { showCobroDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))
        SummaryCardsSection()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Movimientos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Column {
                // Barra Superior Tabla
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Transacciones del día", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Text(if (transaccionesFiltradas.isEmpty()) "Sin registros" else "${transaccionesFiltradas.size} operaciones", fontSize = 12.sp, color = textMedium)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp), color = bgColor, border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { mostrarCalendarioPrincipal = true }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EditCalendar, null, tint = primaryColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(fechaSeleccionada, fontSize = 14.sp, color = textColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                HorizontalDivider(color = borderColor)

                // Tabla
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                        // CABECERA
                        Row(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(vertical = 10.dp, horizontal = TableConfig.HorizontalPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableHeaderCell("TIPO", TableConfig.WidthTipo, TextAlign.Start)
                            TableHeaderCell("CLIENTE", TableConfig.WidthCliente, TextAlign.Start)
                            TableHeaderCell("MÉTODO", TableConfig.WidthMetodo, TextAlign.Start)
                            TableHeaderCell("MONTO", TableConfig.WidthMonto, TextAlign.End)
                            TableHeaderCell("HORA", TableConfig.WidthHora, TextAlign.Center)
                            TableHeaderCell("ESTADO", TableConfig.WidthEstado, TextAlign.Center)
                        }
                        HorizontalDivider(color = borderColor)

                        // FILAS
                        if (transaccionesFiltradas.isEmpty()) {
                            EmptyStateView()
                        } else {
                            transaccionesFiltradas.forEach { item ->
                                TransactionRow(item)
                                HorizontalDivider(color = borderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

// =================================================================
// 1. DIÁLOGO: ARQUEO DE CAJA
// =================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArqueoDeCajaDialog(onDismiss: () -> Unit, initialDate: String) {
    // Colores del diálogo
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    var fechaArqueo by remember { mutableStateOf(initialDate) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        fechaArqueo = sdf.format(Date(selectedMillis))
                    }
                    showDatePicker = false
                }) { Text("Seleccionar", color = primaryColor) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f).padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Arqueo de Caja", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, "Cerrar", tint = textColor)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Seleccionar fecha:", fontSize = 14.sp, color = textColor, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = fechaArqueo,
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.CalendarToday, null, tint = primaryColor) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = textColor,
                            disabledBorderColor = borderColor,
                            disabledContainerColor = surfaceColor,
                            disabledTrailingIconColor = primaryColor
                        ),
                        enabled = false
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
                }

                Spacer(modifier = Modifier.height(24.dp))
                ArqueoRow("Efectivo", "S/ 840.50")
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Tarjeta", "S/ 4,320.00")
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Yape", "S/ 1,250.00")

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = borderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total del Día", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Text("S/ 6,410.50", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Text("Cerrar", color = textColor)
                }
            }
        }
    }
}

@Composable
fun ArqueoRow(label: String, amount: String) {
    val bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val textLabel = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(color = bgColor, shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = textLabel, fontSize = 14.sp)
            Text(amount, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// =================================================================
// 2. DIÁLOGO: REGISTRAR COBRO
// =================================================================
@Composable
fun RegistrarCobroDialog(onDismiss: () -> Unit) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    var selectedTipo by remember { mutableStateOf("") }
    var selectedMetodo by remember { mutableStateOf("") }
    var selectedCliente by remember { mutableStateOf("") }
    var montoInput by remember { mutableStateOf("") }

    val tiposOpciones = listOf("Pago de Reserva", "Servicio Adicional")
    val metodosOpciones = listOf("Efectivo", "Yape", "Tarjeta", "Transferencia")
    val clientesOpciones = listOf("Cliente 1", "Cliente 2")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Registrar Cobro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, "Cerrar", tint = textColor)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Tipo")
                        DynamicDropdownField(tiposOpciones, "Seleccione", selectedTipo) { selectedTipo = it }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Método")
                        DynamicDropdownField(metodosOpciones, "Seleccione", selectedMetodo) { selectedMetodo = it }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Cliente")
                        DynamicDropdownField(clientesOpciones, "Cliente", selectedCliente) { selectedCliente = it }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Monto")
                        OutlinedTextField(
                            value = montoInput,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) montoInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            placeholder = { Text("0.00", color = textColor.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = borderColor,
                                focusedContainerColor = surfaceColor,
                                unfocusedContainerColor = surfaceColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.height(46.dp)
                    ) { Text("Cancelar", color = textColor) }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.height(46.dp)
                    ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicDropdownField(
    options: List<String>,
    placeholder: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val surfaceColor = MaterialTheme.colorScheme.surface

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption.ifEmpty { placeholder },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = borderColor,
                focusedContainerColor = surfaceColor,
                unfocusedContainerColor = surfaceColor,
                focusedTextColor = if (selectedOption.isEmpty()) textColor.copy(alpha = 0.5f) else textColor,
                unfocusedTextColor = if (selectedOption.isEmpty()) textColor.copy(alpha = 0.5f) else textColor
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(surfaceColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, fontSize = 14.sp, color = textColor) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FormLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 6.dp))
}

// =================================================================
// COMPONENTES DE UI GENERALES
// =================================================================

@Composable
fun HeaderSection(onArqueoClick: () -> Unit, onNuevoCobroClick: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val textMedium = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val buttonBg = MaterialTheme.colorScheme.surface

    Column {
        Text("Caja y Cobros", color = textColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
        Text("Resumen de operaciones diarias", color = textMedium, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onArqueoClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = buttonBg)
            ) {
                Icon(Icons.Outlined.Description, null, tint = textMedium, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Arqueo", color = textColor, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onNuevoCobroClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo Cobro", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SummaryCardsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard(Modifier.weight(1f), "Yape / Plin", "S/ 1,250.00", Icons.Default.Smartphone, StatusGreen)
            DashboardCard(Modifier.weight(1f), "Efectivo", "S/ 840.50", Icons.Default.Money, StatusBlue)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard(Modifier.weight(1f), "Tarjetas", "S/ 4,320.00", Icons.Default.CreditCard, StatusPurple)
            DashboardCard(Modifier.weight(1f), "Total Día", "S/ 6,410.50", Icons.Default.BarChart, MaterialTheme.colorScheme.primary, true)
        }
    }
}

@Composable
fun DashboardCard(modifier: Modifier, title: String, amount: String, icon: ImageVector, color: Color, isPrimary: Boolean = false) {
    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val textMedium = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isPrimary) BorderStroke(1.dp, color.copy(alpha = 0.3f)) else BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(Modifier.size(38.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, color = textMedium, fontWeight = FontWeight.Medium)
            Text(amount, fontSize = 18.sp, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

// --- CELDAS DE TABLA ALINEADAS ---
@Composable
fun TableHeaderCell(text: String, width: Dp, align: TextAlign) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = align,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun TransactionRow(item: Transaccion) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val textMedium = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .background(surfaceColor)
            .clickable { /* Detalle */ }
            .padding(vertical = 14.dp, horizontal = TableConfig.HorizontalPadding), // Padding lateral igual a la cabecera
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.tipo, modifier = Modifier.width(TableConfig.WidthTipo), fontSize = 13.sp, color = textColor, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)

        Column(modifier = Modifier.width(TableConfig.WidthCliente), verticalArrangement = Arrangement.Center) {
            Text(item.cliente, fontSize = 13.sp, color = textColor, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("ID: ${item.id}", fontSize = 11.sp, color = textMedium)
        }

        Box(modifier = Modifier.width(TableConfig.WidthMetodo), contentAlignment = Alignment.CenterStart) {
            StatusBadge(item.metodo, when(item.metodo) { "Yape" -> StatusPurple; "Efectivo" -> StatusBlue; else -> textColor })
        }

        Text(text = item.monto, modifier = Modifier.width(TableConfig.WidthMonto), fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)

        Text(text = item.hora, modifier = Modifier.width(TableConfig.WidthHora), fontSize = 12.sp, color = textMedium, textAlign = TextAlign.Center)

        Box(modifier = Modifier.width(TableConfig.WidthEstado), contentAlignment = Alignment.Center) {
            val (color, bg) = when(item.estado) {
                "Completado" -> StatusGreen to StatusGreen.copy(alpha = 0.1f)
                "Pendiente" -> OrangePrimary to OrangePrimary.copy(alpha = 0.1f)
                else -> StatusRed to StatusRed.copy(alpha = 0.1f)
            }
            Surface(color = bg, shape = RoundedCornerShape(50)) {
                Text(item.estado, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Surface(color = Color.Transparent, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, color.copy(alpha = 0.2f))) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(6.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}

@Composable
fun EmptyStateView() {
    val textMedium = MaterialTheme.colorScheme.onSurfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(80.dp).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.CalendarToday, null, tint = textMedium, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("No hay transacciones registradas hoy", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Selecciona otra fecha para ver más transacciones", fontSize = 13.sp, color = textMedium, textAlign = TextAlign.Center)
    }
}

fun getSampleTransactions(): List<Transaccion> {
    return listOf(
        Transaccion("101", "01/12/2025", "Venta", "Juan Pérez", "Yape", "S/ 125.50", "10:30", "Completado"),
        Transaccion("102", "01/12/2025", "Servicio", "María García", "Efectivo", "S/ 85.00", "11:15", "Completado"),
        Transaccion("103", "01/12/2025", "Producto", "Empresa SAC", "Tarjeta", "S/ 230.75", "14:45", "Pendiente"),
        Transaccion("104", "01/12/2025", "Venta", "Ana Martínez", "Yape", "S/ 150.00", "16:20", "Completado"),
        Transaccion("105", "30/11/2025", "Servicio", "Luis Rodríguez", "Efectivo", "S/ 95.50", "17:10", "Cancelado")
    )
}