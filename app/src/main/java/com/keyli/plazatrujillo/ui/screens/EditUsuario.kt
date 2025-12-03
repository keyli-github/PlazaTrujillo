package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keyli.plazatrujillo.data.model.UpdateUserRequest
import com.keyli.plazatrujillo.data.model.User
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Función para mapear rol de API a UI
private fun roleApiToLabel(role: String?): String {
    return when (role?.lowercase()) {
        "admin" -> "Administrador"
        "housekeeping" -> "Hotelero"
        "receptionist" -> "Recepcionista"
        else -> role ?: "Sin rol"
    }
}

// Función para mapear rol de UI a API
private fun roleLabelToApi(label: String): String {
    val l = label.lowercase()
    return when {
        l.startsWith("admin") -> "admin"
        l.startsWith("hotel") -> "housekeeping"
        else -> "receptionist"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUsuarioScreen(
    navController: NavHostController,
    uid: String
) {
    // ViewModel
    val viewModel: UserViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Cargar usuarios si no están cargados
    LaunchedEffect(Unit) {
        if (uiState.users.isEmpty()) {
            viewModel.loadUsers()
        }
    }

    // Buscar el usuario por UID
    val user = uiState.users.firstOrNull { it.uid == uid }

    if (user == null) {
        // Mostrar carga o error si no se encuentra el usuario
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = OrangePrimary)
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Usuario no encontrado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }
        return
    }

    EditUsuario(navController, user, viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditUsuario(
    navController: NavHostController,
    user: User,
    viewModel: UserViewModel
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()

    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val inputBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    // --- ESTADOS --- (Inicializados con los datos del usuario)
    val initialRole = roleApiToLabel(user.role)
    var selectedRole by remember { mutableStateOf(initialRole) }
    var salary by remember { mutableStateOf(user.salary ?: "") }

    var roleExpanded by remember { mutableStateOf(false) }
    val roles = listOf("Administrador", "Recepcionista", "Hotelero")

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateDisplay by remember { mutableStateOf("") }
    var selectedDateISO by remember { mutableStateOf<String?>(null) }
    var showSuccessOverlay by remember { mutableStateOf(false) }

    // Inicializar fecha si existe
    LaunchedEffect(Unit) {
        user.entryDate?.let { dateStr ->
            try {
                // Parsear formato ISO (YYYY-MM-DD) a formato de visualización
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateStr)
                if (date != null) {
                    selectedDateDisplay = outputFormat.format(date)
                    selectedDateISO = dateStr
                }
            } catch (e: Exception) {
                selectedDateDisplay = dateStr
                selectedDateISO = dateStr
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = user.entryDate?.let { dateStr ->
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                inputFormat.parse(dateStr)?.time
            } catch (e: Exception) {
                null
            }
        }
    )

    // Lógica Fecha Corregida
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            // Forzamos UTC para evitar que reste un día por la zona horaria
            val displayFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            displayFormatter.timeZone = TimeZone.getTimeZone("UTC")

            val isoFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")

            val date = Date(millis)
            selectedDateDisplay = displayFormatter.format(date)
            selectedDateISO = isoFormatter.format(date)
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
        uiState.successMessage?.let {
            showSuccessOverlay = true

            kotlinx.coroutines.delay(1200)

            showSuccessOverlay = false
            viewModel.clearSuccessMessage()
            navController.popBackStack()
        }
    }


    // Función para validar y actualizar usuario
    fun updateUser() {
        if (selectedRole.isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Por favor selecciona un rol",
                    duration = SnackbarDuration.Short
                )
            }
            return
        }

        val request = UpdateUserRequest(
            role = roleLabelToApi(selectedRole),
            displayName = user.displayName,
            salary = salary.takeIf { it.isNotBlank() },
            entryDate = selectedDateISO,
            attendance = null
        )

        user.uid?.let { uid ->
            viewModel.updateUser(uid, request)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Editar Usuario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
            if (showSuccessOverlay) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .size(180.dp)
                            .background(OrangePrimary, shape = RoundedCornerShape(90.dp))
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Editado",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 10.dp)
        ) {

            // Sección 1: Datos Principales (Solo lectura)
            SectionTitle("Información Personal", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: NOMBRE (Disabled)
            DesignLabel("Nombre Completo", textSecondary)
            OutlinedTextField(
                value = user.displayName ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, null, tint = OrangePrimary)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBgColor,
                    unfocusedContainerColor = inputBgColor,
                    disabledContainerColor = inputBgColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledTextColor = textSecondary,
                    disabledLabelColor = textSecondary
                )
            )
            Text(
                "El nombre no se puede editar",
                fontSize = 11.sp,
                color = textSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPO: CORREO (Disabled)
            DesignLabel("Correo Electrónico", textSecondary)
            OutlinedTextField(
                value = user.email ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Email, null, tint = OrangePrimary)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = inputBgColor,
                    unfocusedContainerColor = inputBgColor,
                    disabledContainerColor = inputBgColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledTextColor = textSecondary,
                    disabledLabelColor = textSecondary
                )
            )
            Text(
                "El email no se puede editar",
                fontSize = 11.sp,
                color = textSecondary,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Sección 2: Datos Laborales (Editables)
            SectionTitle("Detalles del Puesto", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: ROL (Dropdown)
            DesignLabel("Rol / Cargo", textSecondary)
            ExposedDropdownMenuBox(
                expanded = roleExpanded,
                onExpandedChange = { roleExpanded = !roleExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (selectedRole.isEmpty()) "Seleccionar Rol" else selectedRole,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(Icons.Default.VerifiedUser, null, tint = OrangePrimary)
                    },
                    trailingIcon = {
                        Icon(
                            if (roleExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            null, tint = textSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        disabledContainerColor = inputBgColor,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = OrangePrimary,
                        focusedTextColor = textColor,
                        unfocusedTextColor = if(selectedRole.isEmpty()) textSecondary else textColor
                    )
                )

                ExposedDropdownMenu(
                    expanded = roleExpanded,
                    onDismissRequest = { roleExpanded = false },
                    modifier = Modifier.background(surfaceColor)
                ) {
                    roles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol, color = textColor) },
                            onClick = {
                                selectedRole = rol
                                roleExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPO: SALARIO
            DesignTextField(
                label = "Salario Mensual",
                value = salary,
                onValueChange = { salary = it },
                icon = Icons.Default.ShoppingCart,
                placeholder = "S/ 0.00",
                keyboardType = KeyboardType.Number,
                inputBgColor = inputBgColor,
                textColor = textColor,
                placeholderColor = textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPO: FECHA
            DesignLabel("Fecha de Ingreso", textSecondary)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedDateDisplay,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Seleccionar fecha", color = textSecondary) },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, null, tint = OrangePrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBgColor,
                        unfocusedContainerColor = inputBgColor,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón Guardar
            Button(
                onClick = { updateUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                elevation = ButtonDefaults.buttonElevation(4.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Guardar Cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Dialogo Fecha
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Aceptar", color = OrangePrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar", color = textSecondary)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = surfaceColor,
                    titleContentColor = textColor,
                    headlineContentColor = textColor,
                    weekdayContentColor = textSecondary,
                    dayContentColor = textColor,
                    todayContentColor = OrangePrimary,
                    todayDateBorderColor = OrangePrimary,
                    selectedDayContainerColor = OrangePrimary,
                    selectedDayContentColor = Color.White
                )
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

