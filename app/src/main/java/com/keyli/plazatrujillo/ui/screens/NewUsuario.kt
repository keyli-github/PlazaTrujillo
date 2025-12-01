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
import com.keyli.plazatrujillo.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Color específico para el fondo de los inputs (Gris muy clarito)
val InputBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewUsuario(navController: NavHostController) {

    // --- Estados ---
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }

    var roleExpanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("") }
    val roles = listOf("Administrador", "Recepcionista", "Hotelero")

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateDisplay by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()

    // Lógica Fecha
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            selectedDateDisplay = formatter.format(Date(millis + 86400000))
        }
    }

    Scaffold(
        containerColor = Color.White, // Fondo blanco limpio
        topBar = {
            // Header corregido
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding() // <--- ESTO EVITA QUE SE VEA APLASTADO ARRIBA
                    .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextBlack)
                }
                Text(
                    text = "Crear Nuevo Usuario",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
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
                .padding(horizontal = 24.dp, vertical = 10.dp) // Márgenes laterales
        ) {

            // Sección 1: Datos Principales
            SectionTitle("Información Personal")

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: NOMBRE
            DesignTextField(
                label = "Nombre Completo",
                value = name,
                onValueChange = { name = it },
                icon = Icons.Default.Person,
                placeholder = "Ej: Marco Antonio Castro"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPO: CORREO
            DesignTextField(
                label = "Correo Electrónico",
                value = email,
                onValueChange = { email = it },
                icon = Icons.Default.Email,
                placeholder = "usuario@plaza.com",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Sección 2: Datos Laborales
            SectionTitle("Detalles del Puesto")

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: ROL (Dropdown)
            DesignLabel("Rol / Cargo")
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
                            null, tint = TextGray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        disabledContainerColor = InputBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = OrangePrimary,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = if(selectedRole.isEmpty()) TextGray else TextBlack
                    )
                )

                ExposedDropdownMenu(
                    expanded = roleExpanded,
                    onDismissRequest = { roleExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    roles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol) },
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
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CAMPO: FECHA
            DesignLabel("Fecha de Ingreso")
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedDateDisplay,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Seleccionar fecha", color = TextGray) },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, null, tint = OrangePrimary)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = TextBlack
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
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text = "Guardar Usuario",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
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
                        Text("Cancelar", color = TextGray)
                    }
                },
                colors = DatePickerDefaults.colors(containerColor = Color.White)
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = OrangePrimary,
                        todayDateBorderColor = OrangePrimary
                    )
                )
            }
        }
    }
}

// -------------------------------------------------------------------
// COMPONENTES DE DISEÑO
// -------------------------------------------------------------------

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextBlack,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun DesignLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = TextGray,
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
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        DesignLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color(0xFFB0B0B0)) },
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
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = OrangePrimary,
                focusedTextColor = TextBlack,
                unfocusedTextColor = TextBlack
            )
        )
    }
}