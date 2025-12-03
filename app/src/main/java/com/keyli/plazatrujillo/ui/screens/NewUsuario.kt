package com.keyli.plazatrujillo.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keyli.plazatrujillo.data.model.CreateUserRequest
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// Función para generar contraseña temporal (igual que React)
private fun generateTempPassword(): String {
    val uppercase = "ABCDEFGHJKLMNPQRSTUVWXYZ"
    val lowercase = "abcdefghijkmnopqrstuvwxyz"
    val digits = "23456789"
    val symbols = "!@#\$%^&*?"
    val all = uppercase + lowercase + digits + symbols
    val length = 12
    
    val passwordChars = mutableListOf<Char>(
        uppercase[Random.nextInt(uppercase.length)],
        lowercase[Random.nextInt(lowercase.length)],
        digits[Random.nextInt(digits.length)],
        symbols[Random.nextInt(symbols.length)]
    )
    
    for (i in passwordChars.size until length) {
        passwordChars.add(all[Random.nextInt(all.length)])
    }
    
    return passwordChars.shuffled().joinToString("")
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
fun NewUsuario(navController: NavHostController) {
    // ViewModel
    val viewModel: UserViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val inputBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)

    // --- ESTADOS ---
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    var roleExpanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("") }
    val roles = listOf("Administrador", "Recepcionista", "Hotelero")

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateDisplay by remember { mutableStateOf("") }
    var selectedDateISO by remember { mutableStateOf<String?>(null) }
    val datePickerState = rememberDatePickerState()

    // Lógica Fecha
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val displayFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDateDisplay = displayFormatter.format(Date(millis))
            selectedDateISO = isoFormatter.format(Date(millis))
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

    // Manejar éxito en la creación con diálogo de contraseña
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(uiState.creationSuccess) {
        uiState.creationSuccess?.let {
            showSuccessDialog = true
        }
    }
    
    // Función para validar y crear usuario
    fun createUser() {
        if (name.isBlank() || email.isBlank() || selectedRole.isEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Por favor completa todos los campos requeridos",
                    duration = SnackbarDuration.Short
                )
            }
            return
        }
        
        val password = generateTempPassword()
        val request = CreateUserRequest(
            email = email.trim(),
            password = password,
            role = roleLabelToApi(selectedRole),
            displayName = name.trim().takeIf { it.isNotBlank() },
            salary = salary.takeIf { it.isNotBlank() },
            entryDate = selectedDateISO,
            attendance = null
        )
        
        viewModel.createUser(request)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor, // Fondo dinámico
        topBar = {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surfaceColor)
                    .statusBarsPadding()
                    .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = textColor)
                }
                Text(
                    text = "Crear Nuevo Usuario",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
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

            // Sección 1: Datos Principales
            SectionTitle("Información Personal", textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: NOMBRE
            DesignTextField(
                label = "Nombre Completo",
                value = name,
                onValueChange = { name = it },
                icon = Icons.Default.Person,
                placeholder = "Ej: Marco Antonio Castro",
                inputBgColor = inputBgColor,
                textColor = textColor,
                placeholderColor = textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPO: CORREO
            DesignTextField(
                label = "Correo Electrónico",
                value = email,
                onValueChange = { email = it },
                icon = Icons.Default.Email,
                placeholder = "usuario@plaza.com",
                keyboardType = KeyboardType.Email,
                inputBgColor = inputBgColor,
                textColor = textColor,
                placeholderColor = textSecondary
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Sección 2: Datos Laborales
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
                onClick = { createUser() },
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
                        text = "Guardar Usuario",
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
        
        // Diálogo de éxito con contraseña
        uiState.creationSuccess?.let { successData ->
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { 
                        showSuccessDialog = false
                        viewModel.clearCreationSuccess()
                        // Refrescar lista de usuarios y volver atrás
                        viewModel.refresh()
                        navController.popBackStack()
                    },
                    icon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            "Usuario Creado Exitosamente",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = textColor
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = OrangePrimary.copy(alpha = 0.1f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Credenciales de acceso:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = textColor
                                    )
                                    InfoRow("Nombre:", successData.name, textColor, textSecondary)
                                    InfoRow("Email:", successData.email, textColor, textSecondary)
                                    PasswordRow(
                                        "Contraseña:",
                                        successData.password,
                                        textColor,
                                        textSecondary,
                                        LocalContext.current
                                    )
                                    InfoRow("Rol:", successData.role, textColor, textSecondary)
                                }
                            }
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = bgColor.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        "Instrucciones de acceso",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = textColor
                                    )
                                    Text(
                                        "• Esta es la contraseña permanente del usuario.",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        "• El usuario puede iniciar sesión inmediatamente con estas credenciales.",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                    Text(
                                        "• Antes debe verificar su correo electrónico.",
                                        fontSize = 11.sp,
                                        color = textSecondary
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                viewModel.clearCreationSuccess()
                                viewModel.refresh()
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Cerrar", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, textColor: Color, secondaryColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = secondaryColor
        )
        Text(
            value,
            fontSize = 13.sp,
            color = textColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PasswordRow(
    label: String,
    password: String,
    textColor: Color,
    secondaryColor: Color,
    context: Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = secondaryColor
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                password,
                fontSize = 13.sp,
                color = textColor,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                modifier = Modifier.padding(end = 8.dp)
            )
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Contraseña", password)
                    clipboard.setPrimaryClip(clip)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "Copiar contraseña",
                    tint = OrangePrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------------
// COMPONENTES DE DISEÑO
// -------------------------------------------------------------------

@Composable
fun SectionTitle(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun DesignLabel(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = color,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun DesignTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    inputBgColor: Color,
    textColor: Color,
    placeholderColor: Color
) {
    Column {
        DesignLabel(label, placeholderColor)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = placeholderColor) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OrangePrimary
                )
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBgColor,
                unfocusedContainerColor = inputBgColor,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = OrangePrimary,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            )
        )
    }
}