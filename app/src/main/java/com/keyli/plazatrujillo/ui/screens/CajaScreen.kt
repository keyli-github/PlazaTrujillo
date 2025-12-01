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
import androidx.compose.material.icons.filled.ArrowDropDown
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// --- COLORES ---
val BgLight = Color(0xFFF4F6F8)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1A1C1E)
val TextMedium = Color(0xFF6C7278)
val PrimaryOrange = Color(0xFFFF6B00)
val GreenSuccess = Color(0xFF22C55E)
val BlueInfo = Color(0xFF3B82F6)
val PurpleAccent = Color(0xFF8B5CF6)
val RedError = Color(0xFFEF4444)
val BorderColor = Color(0xFFE5E7EB)

// --- CONFIGURACIÓN TABLA ---
object TableConfig {
    val WidthTipo = 90.dp
    val WidthCliente = 160.dp
    val WidthMetodo = 110.dp
    val WidthMonto = 100.dp
    val WidthHora = 80.dp
    val WidthEstado = 110.dp
    val RowHorizontalPadding = 16.dp
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
    // --- ESTADOS ---
    var fechaSeleccionada by remember { mutableStateOf("01/12/2025") }
    var mostrarCalendarioPrincipal by remember { mutableStateOf(false) }

    // Estados para los DIÁLOGOS
    var showArqueoDialog by remember { mutableStateOf(false) }
    var showCobroDialog by remember { mutableStateOf(false) }

    // Datos
    val todasLasTransacciones = remember { getSampleTransactions() }
    val transaccionesFiltradas = remember(fechaSeleccionada) {
        todasLasTransacciones.filter { it.fecha == fechaSeleccionada }
    }

    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    // --- DIALOGOS MODALES ---

    if (showArqueoDialog) {
        ArqueoDeCajaDialog(
            onDismiss = { showArqueoDialog = false },
            initialDate = fechaSeleccionada
        )
    }

    if (showCobroDialog) {
        RegistrarCobroDialog(
            onDismiss = { showCobroDialog = false }
        )
    }

    // --- POPUP CALENDARIO PRINCIPAL (Filtro de Tabla) ---
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
                }) { Text("Aceptar", color = PrimaryOrange) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendarioPrincipal = false }) { Text("Cancelar", color = TextMedium) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // --- CONTENIDO PRINCIPAL ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgLight)
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
            color = TextDark,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column {
                // Barra Superior Tabla
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Transacciones del día", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(if (transaccionesFiltradas.isEmpty()) "Sin registros" else "${transaccionesFiltradas.size} operaciones", fontSize = 12.sp, color = TextMedium)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp), color = BgLight, border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { mostrarCalendarioPrincipal = true }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EditCalendar, null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(fechaSeleccionada, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Divider(color = BorderColor)

                // Tabla
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                        Row(
                            modifier = Modifier
                                .background(BgLight.copy(alpha = 0.5f))
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(TableConfig.RowHorizontalPadding))
                            TableHeaderCell("TIPO", TableConfig.WidthTipo, TextAlign.Start)
                            TableHeaderCell("CLIENTE", TableConfig.WidthCliente, TextAlign.Start)
                            TableHeaderCell("MÉTODO", TableConfig.WidthMetodo, TextAlign.Start)
                            TableHeaderCell("MONTO", TableConfig.WidthMonto, TextAlign.End)
                            TableHeaderCell("HORA", TableConfig.WidthHora, TextAlign.Center)
                            TableHeaderCell("ESTADO", TableConfig.WidthEstado, TextAlign.Center)
                            Spacer(modifier = Modifier.width(TableConfig.RowHorizontalPadding))
                        }
                        Divider(color = BorderColor)
                        if (transaccionesFiltradas.isEmpty()) {
                            EmptyStateView()
                        } else {
                            transaccionesFiltradas.forEach { item ->
                                TransactionRow(item)
                                Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
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
// 1. DIÁLOGO: ARQUEO DE CAJA (CORREGIDO)
// =================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArqueoDeCajaDialog(onDismiss: () -> Unit, initialDate: String) {
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
                }) {
                    Text("Seleccionar", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar", color = TextMedium) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Arqueo de Caja", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextMedium)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CAMPO DE FECHA INTERACTIVO (CORREGIDO: ENVUELTO EN BOX)
                Text("Seleccionar fecha:", fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                // --- AQUÍ ESTABA EL ERROR: AHORA USAMOS UN BOX CONTENEDOR ---
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = fechaArqueo,
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false, // Deshabilitado visualmente para que no salga el cursor
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryOrange)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = TextDark,
                            disabledBorderColor = BorderColor,
                            disabledContainerColor = SurfaceWhite,
                            disabledTrailingIconColor = PrimaryOrange
                        )
                    )
                    // Caja transparente superpuesta que captura el click
                    // matchParentSize AHORA SÍ funciona porque el padre directo es un Box
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }
                // ------------------------------------------------------------

                Spacer(modifier = Modifier.height(24.dp))

                // Filas de Detalles
                ArqueoRow("Efectivo", "S/ 840.50")
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Tarjeta", "S/ 4,320.00")
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Yape", "S/ 1,250.00")
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Transferencia", "S/ 0.00")

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = BorderColor)
                Spacer(modifier = Modifier.height(16.dp))

                // Total
                Surface(color = BgLight, shape = RoundedCornerShape(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total del Día", fontWeight = FontWeight.Bold, color = TextDark)
                        Text("S/ 6,410.50", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Cerrar
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Text("Cerrar", color = TextDark)
                }
            }
        }
    }
}

@Composable
fun ArqueoRow(label: String, amount: String) {
    Surface(color = BgLight.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextMedium, fontSize = 14.sp)
            Text(amount, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// =================================================================
// 2. DIÁLOGO: REGISTRAR COBRO
// =================================================================
@Composable
fun RegistrarCobroDialog(onDismiss: () -> Unit) {
    var selectedTipo by remember { mutableStateOf("") }
    var selectedMetodo by remember { mutableStateOf("") }
    var selectedCliente by remember { mutableStateOf("") }
    var montoInput by remember { mutableStateOf("") }

    val tiposOpciones = listOf("Pago de Reserva", "Servicio Adicional")
    val metodosOpciones = listOf("Efectivo", "Yape", "Tarjeta", "Transferencia")
    val clientesOpciones = listOf("Seleccione cliente")

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Registrar Cobro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextMedium)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // FILA 1
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Tipo")
                        DynamicDropdownField(
                            options = tiposOpciones,
                            placeholder = "Seleccione",
                            selectedOption = selectedTipo,
                            onOptionSelected = { selectedTipo = it }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Método")
                        DynamicDropdownField(
                            options = metodosOpciones,
                            placeholder = "Seleccione",
                            selectedOption = selectedMetodo,
                            onOptionSelected = { selectedMetodo = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FILA 2
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Cliente")
                        DynamicDropdownField(
                            options = clientesOpciones,
                            placeholder = "Cliente",
                            selectedOption = selectedCliente,
                            onOptionSelected = { selectedCliente = it }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Monto")
                        OutlinedTextField(
                            value = montoInput,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) montoInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            placeholder = { Text("0.00", color = TextMedium.copy(alpha = 0.5f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = SurfaceWhite,
                                unfocusedContainerColor = SurfaceWhite
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                FormLabel("Clientes y montos del día")
                Surface(color = BgLight, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total general", color = TextDark)
                        Text("S/ 0.00", fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.height(46.dp)
                    ) { Text("Cancelar", color = TextMedium) }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        modifier = Modifier.height(46.dp)
                    ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

// --- COMPONENTE DROPDOWN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicDropdownField(
    options: List<String>,
    placeholder: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption.ifEmpty { placeholder },
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryOrange,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedTextColor = if (selectedOption.isEmpty()) TextMedium else TextDark,
                unfocusedTextColor = if (selectedOption.isEmpty()) TextMedium else TextDark
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceWhite)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, fontSize = 14.sp, color = TextDark) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

// Componentes Auxiliares
@Composable
fun FormLabel(text: String) {
    Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(bottom = 6.dp))
}

// =================================================================
// COMPONENTES DE UI GENERALES
// =================================================================

@Composable
fun HeaderSection(onArqueoClick: () -> Unit, onNuevoCobroClick: () -> Unit) {
    Column {
        Text("Caja y Cobros", color = TextDark, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
        Text("Resumen de operaciones diarias", color = TextMedium, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onArqueoClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = SurfaceWhite)
            ) {
                Icon(Icons.Outlined.Description, null, tint = TextMedium, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Arqueo", color = TextDark, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onNuevoCobroClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
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
            DashboardCard(Modifier.weight(1f), "Yape / Plin", "S/ 1,250.00", Icons.Default.Smartphone, GreenSuccess)
            DashboardCard(Modifier.weight(1f), "Efectivo", "S/ 840.50", Icons.Default.Money, BlueInfo)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DashboardCard(Modifier.weight(1f), "Tarjetas", "S/ 4,320.00", Icons.Default.CreditCard, PurpleAccent)
            DashboardCard(Modifier.weight(1f), "Total Día", "S/ 6,410.50", Icons.Default.BarChart, PrimaryOrange, true)
        }
    }
}

@Composable
fun DashboardCard(modifier: Modifier, title: String, amount: String, icon: ImageVector, color: Color, isPrimary: Boolean = false) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isPrimary) BorderStroke(1.dp, color.copy(alpha = 0.3f)) else BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(Modifier.size(38.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, color = TextMedium, fontWeight = FontWeight.Medium)
            Text(amount, fontSize = 18.sp, color = TextDark, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TableHeaderCell(text: String, width: Dp, align: TextAlign) {
    Text(text = text, modifier = Modifier.width(width), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMedium, textAlign = align, letterSpacing = 0.5.sp)
}

@Composable
fun TransactionRow(item: Transaccion) {
    Row(
        modifier = Modifier
            .background(SurfaceWhite)
            .clickable { /* Detalle */ }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(TableConfig.RowHorizontalPadding))
        Text(text = item.tipo, modifier = Modifier.width(TableConfig.WidthTipo), fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start)
        Column(modifier = Modifier.width(TableConfig.WidthCliente), verticalArrangement = Arrangement.Center) {
            Text(item.cliente, fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("ID: ${item.id}", fontSize = 11.sp, color = TextMedium)
        }
        Box(modifier = Modifier.width(TableConfig.WidthMetodo), contentAlignment = Alignment.CenterStart) {
            StatusBadge(item.metodo, when(item.metodo) { "Yape" -> PurpleAccent; "Efectivo" -> BlueInfo; else -> TextDark })
        }
        Text(text = item.monto, modifier = Modifier.width(TableConfig.WidthMonto), fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
        Text(text = item.hora, modifier = Modifier.width(TableConfig.WidthHora), fontSize = 12.sp, color = TextMedium, textAlign = TextAlign.Center)
        Box(modifier = Modifier.width(TableConfig.WidthEstado), contentAlignment = Alignment.Center) {
            val (color, bg) = when(item.estado) {
                "Completado" -> GreenSuccess to GreenSuccess.copy(alpha = 0.1f)
                "Pendiente" -> PrimaryOrange to PrimaryOrange.copy(alpha = 0.1f)
                else -> RedError to RedError.copy(alpha = 0.1f)
            }
            Surface(color = bg, shape = RoundedCornerShape(50)) {
                Text(item.estado, Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
            }
        }
        Spacer(modifier = Modifier.width(TableConfig.RowHorizontalPadding))
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(color = Color.Transparent, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, color.copy(alpha = 0.2f))) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(Modifier.width(6.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(80.dp).background(BgLight, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.CalendarToday, null, tint = TextMedium, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("No hay transacciones registradas hoy", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Selecciona otra fecha para ver más transacciones", fontSize = 13.sp, color = TextMedium, textAlign = TextAlign.Center)
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