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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.launch

// --- CONFIGURACIÓN DE ANCHOS DE TABLA (AJUSTADOS) ---
object UserTableConfig {
    val WidthName = 200.dp   // Antes 240
    val WidthRole = 160.dp   // Antes 200
    val WidthDate = 100.dp   // Antes 130
    val WidthStatus = 90.dp  // Antes 110
    val WidthActions = 130.dp // Antes 100 (Aumentado para que quepan 3 iconos)
}

// Data Class
data class PersonalUI(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val salary: String,
    val entryDate: String,
    val isActive: Boolean
)

@Composable
fun UsuarioScreen(navController: NavHostController) {
    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    // --- ESTADOS Y LÓGICA ---
    var search by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Usamos mutableStateListOf para que la UI se actualice al cambiar el estado
    val usersList = remember {
        mutableStateListOf(
            PersonalUI(1,"Marco Gutierrez","marco.gutierrez@plaza.com","Administrador","S/ 3,500","15/01/24",true),
            PersonalUI(2,"Frank Castro","frank.castro@plaza.com","Recepcionista","S/ 2,200","20/02/24",true),
            PersonalUI(3,"Keyli Roncal","keyli.roncal@plaza.com","Mantenimiento","S/ 1,800","10/03/24",true),
            PersonalUI(4,"Karina Guerrero","karina.guerrero@plaza.com","Administrador","S/ 3,500","05/04/24",true),
            PersonalUI(5,"Cristian Zavaleta","cristian.zavaleta@plaza.com","Seguridad","S/ 1,900","12/04/24",false),
            PersonalUI(6,"Luis Alonso","luis.alonso@plaza.com","Logística","S/ 2,500","01/05/24",true)
        )
    }

    // Filtro de búsqueda
    val filteredUsers = usersList.filter {
        it.name.contains(search, true) || it.email.contains(search, true)
    }

    // Lógica para cambiar estado (Candado)
    fun toggleUserStatus(user: PersonalUI) {
        val index = usersList.indexOfFirst { it.id == user.id }
        if (index != -1) {
            val newState = !user.isActive
            usersList[index] = user.copy(isActive = newState)

            // Mostrar mensaje
            val action = if (newState) "activado" else "inhabilitado"
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Usuario ${user.name} $action exitosamente",
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
                .padding(paddingValues)
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

                    Button(
                        onClick = { navController.navigate("new_usuario") },
                        modifier = Modifier
                            .fillMaxWidth()
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

                    UnifiedUserTable(
                        users = filteredUsers,
                        onToggleStatus = { toggleUserStatus(it) }
                    )
                }
            }

            Spacer(Modifier.height(80.dp)) // Espacio final
        }
    }
}

@Composable
private fun UnifiedUserTable(
    users: List<PersonalUI>,
    onToggleStatus: (PersonalUI) -> Unit
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
                Text("No se encontraron resultados", color = emptyTextColor)
            }
        } else {
            users.forEach { user ->
                UserRowItem(user, onToggleStatus)
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
    user: PersonalUI,
    onToggleStatus: (PersonalUI) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 12.dp), // Padding más ajustado
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna: Nombre y Email
        Column(Modifier.width(UserTableConfig.WidthName).padding(end = 8.dp)) {
            Text(user.name, fontWeight = FontWeight.SemiBold, color = textColor, fontSize = 14.sp)
            Text(user.email, fontSize = 12.sp, color = textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        // Columna: Rol y Salario
        Column(Modifier.width(UserTableConfig.WidthRole).padding(end = 8.dp)) {
            Text(user.role, color = OrangePrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text(user.salary, fontSize = 12.sp, color = textColor)
        }

        // Columna: Fecha
        Text(
            text = user.entryDate,
            modifier = Modifier.width(UserTableConfig.WidthDate),
            fontSize = 13.sp,
            color = textColor
        )

        // Columna: Estado
        Box(Modifier.width(UserTableConfig.WidthStatus)) {
            StatusChip(user.isActive)
        }

        // Columna: Acciones (Editar, Candado, Eliminar)
        Row(Modifier.width(UserTableConfig.WidthActions)) {
            // Editar
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = textSecondary, modifier = Modifier.size(20.dp))
            }

            // Candado (Activar/Inactivar)
            IconButton(
                onClick = { onToggleStatus(user) },
                modifier = Modifier.size(32.dp)
            ) {
                // Si está activo, mostramos el candado abierto (listo para cerrarse/bloquearse) o viceversa.
                // Generalmente: Icono representa la ACCIÓN o el ESTADO.
                // Aquí: Si está activo -> Icono Candado Abierto (es seguro). Al clickear -> Se bloquea.
                // Si está inactivo -> Icono Candado Cerrado (está bloqueado). Al clickear -> Se desbloquea.
                val icon = if (user.isActive) Icons.Default.LockOpen else Icons.Default.Lock
                val tint = if (user.isActive) StatusGreen else StatusRed // Verde si está libre, Rojo si está bloqueado

                Icon(icon, contentDescription = "Cambiar Estado", tint = tint, modifier = Modifier.size(20.dp))
            }

            // Eliminar
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusRed, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun StatusChip(isActive: Boolean) {
    val baseColor = if (isActive) StatusGreen else StatusRed
    val bgColor = baseColor.copy(alpha = 0.15f) // Fondo un poco más visible
    val txt = if (isActive) "Activo" else "Inactivo"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp)) // Forma rectangular redondeada más compacta
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(txt, color = baseColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}