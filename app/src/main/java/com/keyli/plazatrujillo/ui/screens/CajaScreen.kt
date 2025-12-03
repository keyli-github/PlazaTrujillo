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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.data.model.CajaTransaction
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.CajaViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// --- CONFIGURACIÓN TABLA ---
object TableConfig {
    val WidthTipo = 90.dp
    val WidthCliente = 140.dp
    val WidthMetodo = 95.dp
    val WidthMonto = 90.dp
    val WidthHora = 75.dp
    val WidthEstado = 100.dp
    val HorizontalPadding = 12.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CajaScreen(
    navController: NavHostController,
    viewModel: CajaViewModel = viewModel()
) {
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val textMedium = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    val uiState by viewModel.uiState.collectAsState()
    
    var mostrarCalendarioPrincipal by remember { mutableStateOf(false) }
    var showArqueoDialog by remember { mutableStateOf(false) }
    var showCobroDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
    
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            delay(1500)
            viewModel.clearSuccessMessage()
        }
    }

    // Diálogos
    if (showArqueoDialog) {
        ArqueoDeCajaDialog(
            onDismiss = { showArqueoDialog = false },
            totals = uiState.totals,
            displayDate = uiState.displayDate
        )
    }

    if (showCobroDialog) {
        RegistrarCobroDialog(
            onDismiss = { showCobroDialog = false },
            paidClients = uiState.paidClients,
            isCreating = uiState.isCreatingPayment,
            onCreatePayment = { type, guest, method, amount, reservationCode ->
                viewModel.createPayment(type, guest, method, amount, reservationCode)
            },
            onPaymentSuccess = { showCobroDialog = false }
        )
    }

    // Calendario
    if (mostrarCalendarioPrincipal) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendarioPrincipal = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        viewModel.setDate(selectedMillis)
                    }
                    mostrarCalendarioPrincipal = false
                }) { Text("Aceptar", color = primaryColor) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendarioPrincipal = false }) { 
                    Text("Cancelar", color = textMedium) 
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            CajaHeaderSection(
                onArqueoClick = { showArqueoDialog = true },
                onNuevoCobroClick = { showCobroDialog = true },
                onRefreshClick = { viewModel.refresh() },
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))
            
            // Tarjetas de Totales
            CajaSummaryCardsSection(
                totalYape = uiState.totalYape,
                totalEfectivo = uiState.totalEfectivo,
                totalTarjeta = uiState.totalTarjeta,
                totalDia = uiState.totalDia
            )
            
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Transacciones del día",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                if (uiState.transactions.isEmpty()) "Sin registros" 
                                else "${uiState.transactions.size} operaciones",
                                fontSize = 12.sp,
                                color = textMedium
                            )
                        }
                        
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = bgColor,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { mostrarCalendarioPrincipal = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.EditCalendar,
                                    null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    uiState.displayDate,
                                    fontSize = 14.sp,
                                    color = textColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = borderColor)

                    // Loading o Contenido
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
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
                                    CajaTableHeaderCell("TIPO", TableConfig.WidthTipo, TextAlign.Start)
                                    CajaTableHeaderCell("CLIENTE", TableConfig.WidthCliente, TextAlign.Start)
                                    CajaTableHeaderCell("MÉTODO", TableConfig.WidthMetodo, TextAlign.Start)
                                    CajaTableHeaderCell("MONTO", TableConfig.WidthMonto, TextAlign.End)
                                    CajaTableHeaderCell("HORA", TableConfig.WidthHora, TextAlign.Center)
                                    CajaTableHeaderCell("ESTADO", TableConfig.WidthEstado, TextAlign.Center)
                                }
                                HorizontalDivider(color = borderColor)

                                // FILAS
                                if (uiState.transactions.isEmpty()) {
                                    CajaEmptyStateView()
                                } else {
                                    uiState.transactions.forEach { item ->
                                        CajaTransactionRow(item)
                                        HorizontalDivider(
                                            color = borderColor.copy(alpha = 0.5f),
                                            thickness = 0.5.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// =================================================================
// 1. DIÁLOGO: ARQUEO DE CAJA
// =================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArqueoDeCajaDialog(
    onDismiss: () -> Unit,
    totals: com.keyli.plazatrujillo.data.model.CajaTotals?,
    displayDate: String
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
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

                Spacer(modifier = Modifier.height(12.dp))
                
                // Fecha actual
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Fecha: $displayDate",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                ArqueoRow("Efectivo", formatCurrency(totals?.methods?.efectivo ?: 0.0))
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Tarjeta", formatCurrency(totals?.methods?.tarjeta ?: 0.0))
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Yape", formatCurrency(totals?.methods?.yape ?: 0.0))
                Spacer(modifier = Modifier.height(10.dp))
                ArqueoRow("Transferencia", formatCurrency(totals?.methods?.transferencia ?: 0.0))

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = borderColor)
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total del Día",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            formatCurrency(totals?.total ?: 0.0),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(44.dp),
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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
fun RegistrarCobroDialog(
    onDismiss: () -> Unit,
    paidClients: List<com.keyli.plazatrujillo.data.model.PaidClient>,
    isCreating: Boolean,
    onCreatePayment: (type: String, guest: String, method: String, amount: Double, reservationCode: String?) -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    var selectedTipo by remember { mutableStateOf("Pago de Reserva") }
    var selectedMetodo by remember { mutableStateOf("Efectivo") }
    var selectedCliente by remember { mutableStateOf("") }
    var selectedReservationCode by remember { mutableStateOf<String?>(null) }
    var montoInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val tiposOpciones = listOf("Pago de Reserva", "Servicio Adicional")
    val metodosOpciones = listOf("Efectivo", "Yape", "Tarjeta", "Transferencia")
    val clientesOpciones = paidClients.map { it.guest ?: "Sin nombre" }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
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
                        CajaDynamicDropdownField(tiposOpciones, "Seleccione", selectedTipo) { 
                            selectedTipo = it 
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Método")
                        CajaDynamicDropdownField(metodosOpciones, "Seleccione", selectedMetodo) { 
                            selectedMetodo = it 
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Cliente")
                        CajaDynamicDropdownField(
                            options = clientesOpciones,
                            placeholder = if (clientesOpciones.isEmpty()) "Sin clientes" else "Seleccione",
                            selectedOption = selectedCliente
                        ) { selected ->
                            selectedCliente = selected
                            // Buscar código de reserva
                            val client = paidClients.find { it.guest == selected }
                            selectedReservationCode = client?.reservationCode
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Monto")
                        OutlinedTextField(
                            value = montoInput,
                            onValueChange = { 
                                if (it.all { char -> char.isDigit() || char == '.' }) {
                                    montoInput = it
                                    errorMessage = null
                                }
                            },
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

                // Error message
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.height(46.dp),
                        enabled = !isCreating
                    ) { Text("Cancelar", color = textColor) }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            // Validaciones
                            if (selectedCliente.isEmpty()) {
                                errorMessage = "Seleccione un cliente"
                                return@Button
                            }
                            val amount = montoInput.toDoubleOrNull()
                            if (amount == null || amount <= 0) {
                                errorMessage = "Ingrese un monto válido"
                                return@Button
                            }
                            onCreatePayment(
                                selectedTipo,
                                selectedCliente,
                                selectedMetodo,
                                amount,
                                selectedReservationCode
                            )
                            onPaymentSuccess()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier.height(46.dp),
                        enabled = !isCreating
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CajaDynamicDropdownField(
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
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
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
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

// =================================================================
// COMPONENTES DE UI GENERALES
// =================================================================

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
    return format.format(amount)
}

@Composable
fun CajaHeaderSection(
    onArqueoClick: () -> Unit,
    onNuevoCobroClick: () -> Unit,
    onRefreshClick: () -> Unit,
    isLoading: Boolean
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val textMedium = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val buttonBg = MaterialTheme.colorScheme.surface

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Caja y Cobros",
                    color = textColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    "Resumen de operaciones diarias",
                    color = textMedium,
                    fontSize = 14.sp
                )
            }
            IconButton(
                onClick = onRefreshClick,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refrescar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onArqueoClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = buttonBg)
            ) {
                Icon(
                    Icons.Outlined.Description,
                    null,
                    tint = textMedium,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Arqueo", color = textColor, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onNuevoCobroClick,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
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
fun CajaSummaryCardsSection(
    totalYape: Double,
    totalEfectivo: Double,
    totalTarjeta: Double,
    totalDia: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CajaDashboardCard(
                Modifier.weight(1f),
                "Yape / Plin",
                formatCurrency(totalYape),
                Icons.Default.Smartphone,
                StatusGreen
            )
            CajaDashboardCard(
                Modifier.weight(1f),
                "Efectivo",
                formatCurrency(totalEfectivo),
                Icons.Default.Money,
                StatusBlue
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CajaDashboardCard(
                Modifier.weight(1f),
                "Tarjetas",
                formatCurrency(totalTarjeta),
                Icons.Default.CreditCard,
                StatusPurple
            )
            CajaDashboardCard(
                Modifier.weight(1f),
                "Total Día",
                formatCurrency(totalDia),
                Icons.Default.BarChart,
                MaterialTheme.colorScheme.primary,
                true
            )
        }
    }
}

@Composable
fun CajaDashboardCard(
    modifier: Modifier,
    title: String,
    amount: String,
    icon: ImageVector,
    color: Color,
    isPrimary: Boolean = false
) {
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
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 13.sp, color = textMedium, fontWeight = FontWeight.Medium)
            Text(amount, fontSize = 18.sp, color = textColor, fontWeight = FontWeight.Bold)
        }
    }
}

// --- CELDAS DE TABLA ---
@Composable
fun CajaTableHeaderCell(text: String, width: Dp, align: TextAlign) {
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
fun CajaTransactionRow(item: CajaTransaction) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val textMedium = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .background(surfaceColor)
            .clickable { /* Detalle */ }
            .padding(vertical = 14.dp, horizontal = TableConfig.HorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.type ?: "—",
            modifier = Modifier.width(TableConfig.WidthTipo),
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Column(
            modifier = Modifier.width(TableConfig.WidthCliente),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                item.guest ?: "—",
                fontSize = 13.sp,
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "ID: ${item.transactionId ?: item.id}",
                fontSize = 11.sp,
                color = textMedium
            )
        }

        Box(
            modifier = Modifier.width(TableConfig.WidthMetodo),
            contentAlignment = Alignment.CenterStart
        ) {
            val methodColor = when (item.method) {
                "Yape" -> StatusGreen
                "Efectivo" -> StatusBlue
                "Tarjeta" -> StatusPurple
                "Transferencia" -> OrangePrimary
                else -> textColor
            }
            CajaStatusBadge(item.method ?: "—", methodColor)
        }

        Text(
            text = formatCurrency(item.amount ?: 0.0),
            modifier = Modifier.width(TableConfig.WidthMonto),
            fontSize = 14.sp,
            color = textColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )

        Text(
            text = item.time ?: "—",
            modifier = Modifier.width(TableConfig.WidthHora),
            fontSize = 12.sp,
            color = textMedium,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier.width(TableConfig.WidthEstado),
            contentAlignment = Alignment.Center
        ) {
            val (color, bg) = when (item.status) {
                "Completado" -> StatusGreen to StatusGreen.copy(alpha = 0.1f)
                "Pendiente" -> OrangePrimary to OrangePrimary.copy(alpha = 0.1f)
                else -> StatusRed to StatusRed.copy(alpha = 0.1f)
            }
            Surface(color = bg, shape = RoundedCornerShape(50)) {
                Text(
                    item.status ?: "—",
                    Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
fun CajaStatusBadge(text: String, color: Color) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(6.dp))
            Text(text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}

@Composable
fun CajaEmptyStateView() {
    val textMedium = MaterialTheme.colorScheme.onSurfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(80.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CalendarToday,
                null,
                tint = textMedium,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No hay transacciones registradas",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = textColor
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Selecciona otra fecha o registra un nuevo cobro",
            fontSize = 13.sp,
            color = textMedium,
            textAlign = TextAlign.Center
        )
    }
}