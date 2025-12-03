package com.keyli.plazatrujillo.ui.screens

import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keyli.plazatrujillo.data.model.User
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// --- CONFIGURACIÓN DE ANCHOS DE TABLA (AJUSTADOS) ---
object UserTableConfig {
    val WidthName = 200.dp   // Antes 240
    val WidthRole = 160.dp   // Antes 200
    val WidthDate = 100.dp   // Antes 130
    val WidthStatus = 90.dp  // Antes 110
    val WidthActions = 130.dp // Antes 100 (Aumentado para que quepan 3 iconos)
}

// Helper functions
private fun formatRole(role: String?): String {
    return when (role?.lowercase()) {
        "admin" -> "Administrador"
        "receptionist" -> "Recepcionista"
        "housekeeping" -> "Hotelero"
        else -> role ?: "Sin rol"
    }
}

private fun formatSalary(salary: String?): String {
    return if (salary.isNullOrBlank()) {
        "S/ 0.00"
    } else {
        try {
            val amount = salary.toDouble()
            "S/ ${String.format("%.2f", amount)}"
        } catch (e: Exception) {
            salary
        }
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "--/--/----"
    return try {
        // Intentar parsear formato ISO (YYYY-MM-DD)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        if (date != null) {
            outputFormat.format(date)
        } else {
            dateString
        }
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun UsuarioScreen(navController: NavHostController) {
    // ViewModel
    val viewModel: UserViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    // --- ESTADOS Y LÓGICA ---
    var search by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<User?>(null) }
    var showEditDialog by remember { mutableStateOf<User?>(null) }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Cargar usuarios al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }
    
    // Recargar cuando regrese de crear/editar usuario
    // Usando un DisposableEffect para escuchar cambios en el back stack
    DisposableEffect(navController) {
        val listener = androidx.navigation.NavController.OnDestinationChangedListener { controller, destination, arguments ->
            // Si estamos en la pantalla de usuarios, refrescar la lista
            if (destination.route == "usuarios") {
                viewModel.loadUsers()
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    // Manejar errores y mensajes de éxito
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearSuccessMessage()
            }
        }
    }

    // Filtro de búsqueda
    val filteredUsers = uiState.users.filter {
        (it.displayName?.contains(search, true) == true) || 
        (it.email?.contains(search, true) == true)
    }

    // Contar cuántos administradores hay en la lista (usar lista completa, no filtrada)
    val adminCount = remember(uiState.users) {
        uiState.users.count { it.role?.lowercase() == "admin" }
    }

    // Verificar si un usuario es el único administrador
    fun isOnlyAdmin(user: User): Boolean {
        return user.role?.lowercase() == "admin" && adminCount == 1
    }

    // Lógica para cambiar estado (Candado)
    fun toggleUserStatus(user: User) {
        user.uid?.let { uid ->
            viewModel.toggleUserStatus(uid)
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Estado actualizado",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // Usamos Scaffold para poder mostrar el Snackbar correctamente
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = bgColor
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                //.padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            Text(
                text = "Gestión de Personal",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // --- TARJETA DE BUSQUEDA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar por nombre o correo...", color = textSecondary) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = OrangePrimary)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = borderColor,
                            focusedContainerColor = bgColor,
                            unfocusedContainerColor = bgColor,
                            cursorColor = OrangePrimary,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier
                                .weight(0.3f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = textSecondary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                        }
                        
                        Button(
                            onClick = { navController.navigate("new_usuario") },
                            modifier = Modifier
                                .weight(0.7f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Crear Nuevo Usuario", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- TARJETA DE TABLA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column {
                    Text(
                        text = "Colaboradores Registrados",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor,
                        modifier = Modifier.padding(20.dp)
                    )

                    HorizontalDivider(color = borderColor)

                    if (uiState.isLoading && uiState.users.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = OrangePrimary)
                        }
                    } else {
                        UnifiedUserTable(
                            users = filteredUsers,
                            onToggleStatus = { toggleUserStatus(it) },
                            onEdit = { showEditDialog = it },
                            onDelete = { showDeleteDialog = it },
                            isOnlyAdmin = { isOnlyAdmin(it) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(80.dp)) // Espacio final
        }
        
        // Diálogo de confirmación para eliminar (solo si no es el único admin)
        showDeleteDialog?.let { user ->
            if (isOnlyAdmin(user)) {
                // Si intenta eliminar el único administrador, mostrar mensaje y cerrar diálogo
                LaunchedEffect(user.uid) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "No se puede eliminar el único administrador",
                            duration = SnackbarDuration.Long
                        )
                    }
                    showDeleteDialog = null
                }
            } else {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = null },
                    title = { Text("Eliminar Usuario", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("¿Estás seguro de que deseas eliminar a ${user.displayName ?: user.email}? Esta acción no se puede deshacer.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                user.uid?.let { uid ->
                                    viewModel.deleteUser(uid)
                                }
                                showDeleteDialog = null
                                // La lista se refrescará automáticamente después de eliminar
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                        ) {
                            Text("Eliminar", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = null }) {
                            Text("Cancelar", color = textSecondary)
                        }
                    }
                )
            }
        }
        
        // Navegar a pantalla de edición cuando se selecciona un usuario (solo si no es el único admin)
        LaunchedEffect(showEditDialog) {
            showEditDialog?.let { user ->
                if (!isOnlyAdmin(user)) {
                    user.uid?.let { uid ->
                        navController.navigate("edit_usuario/$uid")
                    }
                } else {
                    // Mostrar mensaje si intenta editar el único administrador
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "No se puede editar el único administrador",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                showEditDialog = null
            }
        }
    }
}

@Composable
private fun UnifiedUserTable(
    users: List<User>,
    onToggleStatus: (User) -> Unit,
    onEdit: (User) -> Unit,
    onDelete: (User) -> Unit,
    isOnlyAdmin: (User) -> Boolean
) {
    val hScroll = rememberScrollState()
    val headerBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val emptyTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(hScroll)
    ) {
        // Cabecera (Compacta)
        Row(
            modifier = Modifier
                .background(headerBg)
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeader("Usuario / Email", UserTableConfig.WidthName)
            TableHeader("Rol / Salario", UserTableConfig.WidthRole)
            TableHeader("Ingreso", UserTableConfig.WidthDate)
            TableHeader("Estado", UserTableConfig.WidthStatus)
            TableHeader("Acciones", UserTableConfig.WidthActions)
        }

        HorizontalDivider(color = borderColor)

        if (users.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                Text("No se encontraron usuarios", color = emptyTextColor)
            }
        } else {
            users.forEach { user ->
                UserRowItem(user, onToggleStatus, onEdit, onDelete, isOnlyAdmin)
                HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun TableHeader(text: String, width: Dp) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp // Texto un poco más pequeño para caber mejor
    )
}

@Composable
private fun UserRowItem(
    user: User,
    onToggleStatus: (User) -> Unit,
    onEdit: (User) -> Unit,
    onDelete: (User) -> Unit,
    isOnlyAdmin: (User) -> Boolean
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val userStatus = getUserStatus(user)
    val isDisabled = user.disabled == true
    val userRole = user.role?.lowercase() ?: ""
    val isAdmin = userRole == "admin"
    val canToggle = userRole == "receptionist" || userRole == "housekeeping"
    val onlyAdmin = isOnlyAdmin(user)

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna: Nombre y Email
        Column(Modifier.width(UserTableConfig.WidthName).padding(end = 8.dp)) {
            Text(
                user.displayName ?: "Sin nombre", 
                fontWeight = FontWeight.SemiBold, 
                color = textColor, 
                fontSize = 14.sp
            )
            Text(
                user.email ?: "", 
                fontSize = 12.sp, 
                color = textSecondary, 
                maxLines = 1, 
                overflow = TextOverflow.Ellipsis
            )
        }

        // Columna: Rol y Salario
        Column(Modifier.width(UserTableConfig.WidthRole).padding(end = 8.dp)) {
            Text(
                formatRole(user.role), 
                color = OrangePrimary, 
                fontWeight = FontWeight.Medium, 
                fontSize = 13.sp
            )
            Text(
                formatSalary(user.salary), 
                fontSize = 12.sp, 
                color = textColor
            )
        }

        // Columna: Fecha
        Text(
            text = formatDate(user.entryDate),
            modifier = Modifier.width(UserTableConfig.WidthDate),
            fontSize = 13.sp,
            color = textColor
        )

        // Columna: Estado
        Box(Modifier.width(UserTableConfig.WidthStatus)) {
            StatusChip(userStatus)
        }

        // Columna: Acciones (Editar, Candado, Eliminar)
        Row(Modifier.width(UserTableConfig.WidthActions)) {
            // Editar - deshabilitado si es el único administrador
            IconButton(
                onClick = { onEdit(user) },
                enabled = !onlyAdmin,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = if (onlyAdmin) "No se puede editar el único administrador" else "Editar",
                    tint = if (onlyAdmin) textSecondary.copy(alpha = 0.4f) else textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Candado (Activar/Inactivar) - solo para recepcionistas y hoteleros
            if (canToggle) {
                IconButton(
                    onClick = { onToggleStatus(user) },
                    modifier = Modifier.size(32.dp)
                ) {
                    val icon = if (isDisabled) Icons.Default.Lock else Icons.Default.LockOpen
                    val tint = if (isDisabled) StatusRed else StatusGreen

                    Icon(
                        icon,
                        contentDescription = if (isDisabled) "Habilitar usuario" else "Inhabilitar usuario",
                        tint = tint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Eliminar - deshabilitado si es el único administrador
            IconButton(
                onClick = { onDelete(user) },
                enabled = !onlyAdmin,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = if (onlyAdmin) "No se puede eliminar el único administrador" else "Eliminar",
                    tint = if (onlyAdmin) StatusRed.copy(alpha = 0.4f) else StatusRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Función para determinar el estado del usuario (igual que React)
private fun getUserStatus(user: User): String {
    val isAdmin = user.role?.lowercase() == "admin"
    
    return when {
        user.disabled == true -> "Inhabilitado"
        !isAdmin && user.emailVerified == false -> "Sin confirmar"
        else -> "Activo"
    }
}

@Composable
fun StatusChip(status: String) {
    val (baseColor, txt) = when (status) {
        "Activo" -> StatusGreen to "Activo"
        "Inhabilitado" -> StatusRed to "Inhabilitado"
        "Sin confirmar" -> Color(0xFFFF9800) to "Sin confirmar"  // Naranja/Amarillo
        else -> Color.Gray to status
    }
    
    val bgColor = baseColor.copy(alpha = 0.15f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(txt, color = baseColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}