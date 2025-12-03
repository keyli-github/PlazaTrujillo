package com.keyli.plazatrujillo.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.data.model.BlockedRoom
import com.keyli.plazatrujillo.data.model.BriquetteRecord
import com.keyli.plazatrujillo.data.model.MaintenanceIssue
import com.keyli.plazatrujillo.data.model.Room
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.theme.StatusGreen
import com.keyli.plazatrujillo.ui.theme.StatusRed
import com.keyli.plazatrujillo.ui.theme.StatusYellow
import com.keyli.plazatrujillo.ui.viewmodel.MantenimientoViewModel
import java.util.*

@Composable
fun MantenimientoScreen(navController: NavHostController) {
    val viewModel: MantenimientoViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Estados de diálogos
    var showBriquetteDialog by remember { mutableStateOf(false) }
    var showBlockRoomDialog by remember { mutableStateOf(false) }
    var showReportIssueDialog by remember { mutableStateOf(false) }
    var showDeleteIssueDialog by remember { mutableStateOf(false) }
    var showUnblockRoomDialog by remember { mutableStateOf(false) }
    var issueToDelete by remember { mutableStateOf<MaintenanceIssue?>(null) }
    var roomToUnblock by remember { mutableStateOf<BlockedRoom?>(null) }
    
    // Colores
    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sistema Agua Caliente", "Historial Briquetas", "Incidencias", "Hab. Bloqueadas")
    
    // Mostrar mensajes
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }
    
    Surface(modifier = Modifier.fillMaxSize(), color = bgColor) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 8.dp)) {
                Text(
                    text = "Mantenimiento Técnico",
                    color = textColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Gestión del sistema de agua caliente y mantenimiento",
                    color = subTextColor,
                    fontSize = 14.sp
                )
            }
            
            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = bgColor,
                contentColor = OrangePrimary,
                edgePadding = 16.dp,
                divider = { HorizontalDivider(color = dividerColor) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) OrangePrimary else subTextColor,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
            
            // Loading
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    when (selectedTabIndex) {
                        0 -> TabAguaCaliente(
                            uiState = uiState,
                            onRegisterBriquette = { showBriquetteDialog = true },
                            onBlockRoom = { showBlockRoomDialog = true },
                            onReportIssue = { showReportIssueDialog = true }
                        )
                        1 -> TabHistorialBriquetas(uiState.briquetteHistory)
                        2 -> TabIncidencias(
                            issues = uiState.issues,
                            onDeleteIssue = { issue ->
                                issueToDelete = issue
                                showDeleteIssueDialog = true
                            }
                        )
                        3 -> TabHabitacionesBloqueadas(
                            rooms = uiState.blockedRooms,
                            onUnblockRoom = { room ->
                                roomToUnblock = room
                                showUnblockRoomDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Diálogos
    if (showBriquetteDialog) {
        RegisterBriquetteDialog(
            isSaving = uiState.isSaving,
            onDismiss = { showBriquetteDialog = false },
            onConfirm = { quantity, date, time, status ->
                viewModel.registerBriquetteChange(quantity, date, time, status)
                showBriquetteDialog = false
            }
        )
    }
    
    if (showBlockRoomDialog) {
        BlockRoomDialog(
            rooms = uiState.allRooms,
            isSaving = uiState.isSaving,
            onLoadRooms = { viewModel.loadAllRooms() },
            onDismiss = { showBlockRoomDialog = false },
            onConfirm = { room, reason, blockedUntil, blockedBy ->
                viewModel.blockRoom(room, reason, blockedUntil, blockedBy)
                showBlockRoomDialog = false
            }
        )
    }
    
    if (showReportIssueDialog) {
        ReportIssueDialog(
            rooms = uiState.allRooms,
            isSaving = uiState.isSaving,
            onLoadRooms = { viewModel.loadAllRooms() },
            onDismiss = { showReportIssueDialog = false },
            onConfirm = { room, problem, priority, technician ->
                viewModel.reportIssue(room, problem, priority, technician)
                showReportIssueDialog = false
            }
        )
    }
    
    if (showDeleteIssueDialog && issueToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteIssueDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de eliminar la incidencia de ${issueToDelete?.room}?") },
            confirmButton = {
                TextButton(onClick = {
                    issueToDelete?.id?.let { viewModel.deleteIssue(it) }
                    showDeleteIssueDialog = false
                    issueToDelete = null
                }) {
                    Text("Eliminar", color = StatusRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteIssueDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    if (showUnblockRoomDialog && roomToUnblock != null) {
        AlertDialog(
            onDismissRequest = { showUnblockRoomDialog = false },
            title = { Text("Liberar habitación") },
            text = { Text("¿Estás seguro de liberar la habitación ${roomToUnblock?.room}?") },
            confirmButton = {
                TextButton(onClick = {
                    roomToUnblock?.id?.let { viewModel.unblockRoom(it) }
                    showUnblockRoomDialog = false
                    roomToUnblock = null
                }) {
                    Text("Liberar", color = StatusGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUnblockRoomDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ==================== TAB 1: SISTEMA AGUA CALIENTE ====================
@Composable
fun TabAguaCaliente(
    uiState: com.keyli.plazatrujillo.ui.viewmodel.MantenimientoUiState,
    onRegisterBriquette: () -> Unit,
    onBlockRoom: () -> Unit,
    onReportIssue: () -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onBackground
    val system = uiState.waterHeatingSystem
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Fila 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CheckCircle,
                iconBgColor = when (system.operationalStatus) {
                    "Operativo" -> Color(0xFFDCFCE7)
                    "En Mantenimiento" -> Color(0xFFFEF3C7)
                    else -> Color(0xFFFEE2E2)
                },
                iconColor = when (system.operationalStatus) {
                    "Operativo" -> Color(0xFF16A34A)
                    "En Mantenimiento" -> Color(0xFFD97706)
                    else -> Color(0xFFDC2626)
                },
                title = "Estado Operativo",
                value = system.operationalStatus
            )
            
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DateRange,
                iconBgColor = Color(0xFFFFEDD5),
                iconColor = Color(0xFFEA580C),
                title = "Briquetas Este Mes",
                value = "${system.briquettesThisMonth} Unid."
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Fila 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AccessTime,
                iconBgColor = Color(0xFFDBEAFE),
                iconColor = Color(0xFF2563EB),
                title = "Último Cambio",
                value = if (system.lastMaintenanceDate != null) {
                    "${system.lastMaintenanceDate}\n${system.lastMaintenanceTime ?: ""}"
                } else "No registrado"
            )
            
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                iconBgColor = Color(0xFFF3E8FF),
                iconColor = Color(0xFF9333EA),
                title = "Próximo Cambio",
                value = if (system.nextMaintenanceDate != null) {
                    "${system.nextMaintenanceDate}\n${system.nextMaintenanceTime ?: ""}"
                } else "No programado"
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Acciones
        Text("Acciones Rápidas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onRegisterBriquette,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Registrar Cambio Briquetas", color = Color.White)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = onBlockRoom,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Lock, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Bloquear Hab.", fontSize = 13.sp, color = Color.White)
            }
            
            Button(
                onClick = onReportIssue,
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Reportar Incidencia", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

// ==================== TAB 2: HISTORIAL BRIQUETAS ====================
@Composable
fun TabHistorialBriquetas(history: List<BriquetteRecord>) {
    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(listOf("Fecha" to 0.4f, "Hora" to 0.3f, "Cantidad" to 0.3f))
        
        if (history.isEmpty()) {
            EmptyStateMessage("No hay historial de cambios de briquetas")
        } else {
            LazyColumn {
                items(history) { record ->
                    BriquetteRow(record)
                }
            }
        }
    }
}

@Composable
fun BriquetteRow(record: BriquetteRecord) {
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = record.date ?: "-",
            modifier = Modifier.weight(0.4f),
            color = textColor,
            fontSize = 13.sp
        )
        Text(
            text = record.time ?: "-",
            modifier = Modifier.weight(0.3f),
            color = textColor,
            fontSize = 13.sp
        )
        Text(
            text = "${record.quantity ?: 0} Unid.",
            modifier = Modifier.weight(0.3f),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

// ==================== TAB 3: INCIDENCIAS ====================
@Composable
fun TabIncidencias(
    issues: List<MaintenanceIssue>,
    onDeleteIssue: (MaintenanceIssue) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(listOf("Hab." to 0.12f, "Problema" to 0.35f, "Prio." to 0.18f, "Fecha" to 0.2f, "" to 0.15f))
        
        if (issues.isEmpty()) {
            EmptyStateMessage("No hay incidencias registradas")
        } else {
            LazyColumn {
                items(issues) { issue ->
                    IssueRow(issue, onDelete = { onDeleteIssue(issue) })
                }
            }
        }
    }
}

@Composable
fun IssueRow(issue: MaintenanceIssue, onDelete: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = issue.room ?: "-",
            modifier = Modifier.weight(0.12f),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = issue.problem ?: "-",
            modifier = Modifier.weight(0.35f),
            color = textColor,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        PriorityBadge(
            priority = issue.priority ?: "Media",
            modifier = Modifier.weight(0.18f)
        )
        Text(
            text = issue.reportedDate ?: "-",
            modifier = Modifier.weight(0.2f),
            color = textColor,
            fontSize = 11.sp
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.weight(0.15f).size(32.dp)
        ) {
            Icon(Icons.Default.Delete, null, tint = StatusRed, modifier = Modifier.size(18.dp))
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (priority) {
        "Alta" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        "Media" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        else -> Color(0xFFDCFCE7) to Color(0xFF16A34A)
    }
    
    Surface(
        modifier = modifier.padding(end = 4.dp),
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = priority,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==================== TAB 4: HABITACIONES BLOQUEADAS ====================
@Composable
fun TabHabitacionesBloqueadas(
    rooms: List<BlockedRoom>,
    onUnblockRoom: (BlockedRoom) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(listOf("Hab." to 0.12f, "Razón" to 0.33f, "Hasta" to 0.2f, "Por" to 0.2f, "" to 0.15f))
        
        if (rooms.isEmpty()) {
            EmptyStateMessage("No hay habitaciones bloqueadas")
        } else {
            LazyColumn {
                items(rooms) { room ->
                    BlockedRoomRow(room, onUnblock = { onUnblockRoom(room) })
                }
            }
        }
    }
}

@Composable
fun BlockedRoomRow(room: BlockedRoom, onUnblock: () -> Unit) {
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = room.room ?: "-",
            modifier = Modifier.weight(0.12f),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = room.reason ?: "-",
            modifier = Modifier.weight(0.33f),
            color = textColor,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = room.blockedUntil ?: "-",
            modifier = Modifier.weight(0.2f),
            color = textColor,
            fontSize = 11.sp
        )
        Text(
            text = room.blockedBy ?: "-",
            modifier = Modifier.weight(0.2f),
            color = textColor,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = onUnblock,
            modifier = Modifier.weight(0.15f).size(32.dp)
        ) {
            Icon(Icons.Default.LockOpen, null, tint = StatusGreen, modifier = Modifier.size(18.dp))
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

// ==================== DIÁLOGOS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterBriquetteDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Int, date: String, time: String, status: String) -> Unit
) {
    val context = LocalContext.current
    var quantity by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Operativo") }
    val statuses = listOf("Operativo", "En Mantenimiento", "Fuera de Servicio")
    
    val calendar = Calendar.getInstance()
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Registrar Cambio de Briquetas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                    label = { Text("Cantidad *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    label = { Text("Fecha *") },
                    modifier = Modifier.fillMaxWidth().clickable {
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> date = String.format("%04d-%02d-%02d", y, m + 1, d) },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    enabled = false,
                    trailingIcon = { Icon(Icons.Default.DateRange, null) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    label = { Text("Hora *") },
                    modifier = Modifier.fillMaxWidth().clickable {
                        TimePickerDialog(
                            context,
                            { _, h, m -> time = String.format("%02d:%02d", h, m) },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    enabled = false,
                    trailingIcon = { Icon(Icons.Default.Schedule, null) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Estado del Sistema", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statuses.forEach { status ->
                        FilterChip(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            label = { Text(status, fontSize = 11.sp) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val qty = quantity.toIntOrNull() ?: 0
                            if (qty > 0 && date.isNotBlank() && time.isNotBlank()) {
                                onConfirm(qty, date, time, selectedStatus)
                            }
                        },
                        enabled = !isSaving && quantity.isNotBlank() && date.isNotBlank() && time.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Registrar")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockRoomDialog(
    rooms: List<Room>,
    isSaving: Boolean,
    onLoadRooms: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (room: String, reason: String, blockedUntil: String, blockedBy: String?) -> Unit
) {
    val context = LocalContext.current
    var selectedRoom by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var blockedUntil by remember { mutableStateOf("") }
    var blockedBy by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    val calendar = Calendar.getInstance()
    
    LaunchedEffect(Unit) { onLoadRooms() }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Bloquear Habitación", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Selector de habitación
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedRoom,
                        onValueChange = {},
                        label = { Text("Habitación *") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        rooms.forEach { room ->
                            DropdownMenuItem(
                                text = { Text("${room.code} - ${room.type}") },
                                onClick = {
                                    selectedRoom = room.code ?: ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Razón de Bloqueo *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = blockedUntil,
                    onValueChange = {},
                    label = { Text("Bloqueada Hasta *") },
                    modifier = Modifier.fillMaxWidth().clickable {
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> blockedUntil = String.format("%04d-%02d-%02d", y, m + 1, d) },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    enabled = false,
                    trailingIcon = { Icon(Icons.Default.DateRange, null) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = blockedBy,
                    onValueChange = { blockedBy = it },
                    label = { Text("Bloqueada Por") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedRoom.isNotBlank() && reason.isNotBlank() && blockedUntil.isNotBlank()) {
                                onConfirm(selectedRoom, reason, blockedUntil, blockedBy.ifBlank { null })
                            }
                        },
                        enabled = !isSaving && selectedRoom.isNotBlank() && reason.isNotBlank() && blockedUntil.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Bloquear")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueDialog(
    rooms: List<Room>,
    isSaving: Boolean,
    onLoadRooms: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (room: String, problem: String, priority: String, technician: String?) -> Unit
) {
    var selectedRoom by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Media") }
    var technician by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("Baja", "Media", "Alta")
    
    LaunchedEffect(Unit) { onLoadRooms() }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Reportar Incidencia", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Selector de habitación
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedRoom,
                        onValueChange = { selectedRoom = it },
                        label = { Text("Habitación/Área *") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        rooms.forEach { room ->
                            DropdownMenuItem(
                                text = { Text("${room.code} - ${room.type}") },
                                onClick = {
                                    selectedRoom = room.code ?: ""
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = problem,
                    onValueChange = { problem = it },
                    label = { Text("Descripción del Problema *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Prioridad", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorities.forEach { priority ->
                        val color = when (priority) {
                            "Alta" -> StatusRed
                            "Media" -> StatusYellow
                            else -> StatusGreen
                        }
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = { Text(priority) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = technician,
                    onValueChange = { technician = it },
                    label = { Text("Técnico Asignado (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (selectedRoom.isNotBlank() && problem.isNotBlank()) {
                                onConfirm(selectedRoom, problem, selectedPriority, technician.ifBlank { null })
                            }
                        },
                        enabled = !isSaving && selectedRoom.isNotBlank() && problem.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Reportar")
                        }
                    }
                }
            }
        }
    }
}

// ==================== COMPONENTES AUXILIARES ====================

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    value: String
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    
    Card(
        modifier = modifier.height(145.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(42.dp).background(iconBgColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(title, fontSize = 13.sp, color = subTextColor, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
fun TableHeader(headers: List<Pair<String, Float>>) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    
    Row(
        modifier = Modifier.fillMaxWidth().background(surfaceColor).padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        headers.forEach { (title, weight) ->
            Text(
                text = title,
                modifier = Modifier.weight(weight),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    HorizontalDivider(color = dividerColor)
}

@Composable
fun EmptyStateMessage(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}