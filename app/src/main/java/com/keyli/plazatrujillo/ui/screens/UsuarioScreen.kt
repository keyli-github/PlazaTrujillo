package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.ui.theme.*

@Composable
fun UsuarioScreen() {

    var search by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val sampleUsers = listOf(
        Triple("Juan Pérez", "juan.perez@plaza.com", "Administrador"),
        Triple("Ana López", "ana.lopez@plaza.com", "Recepcionista"),
        Triple("Carlos Ruiz", "carlos.ruiz@plaza.com", "Mantenimiento")
    )

    val sampleRoles = listOf(
        Pair("Administrador", "S/ 3,500"),
        Pair("Recepcionista", "S/ 2,200"),
        Pair("Mantenimiento", "S/ 1,800")
    )

    val sampleDates = listOf(
        Pair("15/01/2024", "Activo"),
        Pair("20/02/2024", "Activo"),
        Pair("10/03/2024", "Inactivo")
    )

    val sampleStatus = listOf(
        Pair("Activo", true),
        Pair("Activo", true),
        Pair("Inactivo", false)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        /* ---------- CARD PRINCIPAL ---------- */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {

                Text(
                    text = "Gestión de Usuarios",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar usuario, correo…", color = TextGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = OrangePrimary,
                        focusedContainerColor = Color(0xFFF7F7F7),
                        unfocusedContainerColor = Color(0xFFF7F7F7),
                        cursorColor = OrangePrimary
                    )
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { /* abrir dialogo */ },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear Usuario", color = Color.White)
                }

                Spacer(Modifier.height(12.dp))

                /* Modo Demo */
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Modo demostración", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                            Text("Los usuarios mostrados son datos de ejemplo.", color = Color(0xFF8A2D2D))
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                UsersTable(users = sampleUsers)
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ---------- ROLES ---------- */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Roles y Salarios", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                RolesTable(items = sampleRoles)
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ---------- FECHAS ---------- */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Registro de Ingreso", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                DatesTable(items = sampleDates)
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ---------- ESTADO Y ACCIONES ---------- */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Estado y Acciones", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                StatusActionsTable(items = sampleStatus)
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ---------- REGISTRO DE ASISTENCIA ---------- */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Registro de Asistencia del Personal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack)

                Spacer(Modifier.height(6.dp))

                Text(
                    "Control mensual de asistencia\n(DS N° 004-011-2000-TR)",
                    fontSize = 14.sp,
                    color = TextGray
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { /* abrir nuevo registro */ },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Nuevo Registro", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}


/* ===========================================================
   ===============   TABLAS PERSONALIZADAS   =================
   =========================================================== */

@Composable
private fun UsersTable(users: List<Triple<String, String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9F9F9))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Usuario", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = TextBlack)
            Text("Correo", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = TextBlack)
            Text("Rol", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = TextBlack)
        }

        Divider()

        users.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.first, modifier = Modifier.weight(1f), color = TextBlack)
                Text(item.second, modifier = Modifier.weight(1f), color = TextGray)
                Text(item.third, modifier = Modifier.weight(1f), color = OrangePrimary)
            }
            if (index < users.lastIndex) Divider()
        }
    }
}

@Composable
private fun RolesTable(items: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9F9F9))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(12.dp)
        ) {
            Text("Rol", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Salario", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        }

        Divider()

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(item.first, modifier = Modifier.weight(1f), color = OrangePrimary)
                Text(item.second, modifier = Modifier.weight(1f), color = TextBlack)
            }
            if (index < items.lastIndex) Divider()
        }
    }
}

@Composable
private fun DatesTable(items: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9F9F9))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(12.dp)
        ) {
            Text("F. Entrada", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Estado", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        }

        Divider()

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(item.first, modifier = Modifier.weight(1f), color = TextGray)

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val color = if (item.second == "Activo") StatusGreen else Color.Gray
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(item.second, color = color)
                }
            }
            if (index < items.lastIndex) Divider()
        }
    }
}

@Composable
private fun StatusActionsTable(items: List<Pair<String, Boolean>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9F9F9))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F1F1))
                .padding(12.dp)
        ) {
            Text("Estado", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Acciones", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        }

        Divider()

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    val color = if (item.second) StatusGreen else Color.Gray
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (item.second) "Activo" else "Inactivo", color = color)
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { /*editar*/ }) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFFFFA726))
                    }
                    IconButton(onClick = { /*eliminar*/ }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFF44336))
                    }
                }
            }
            if (index < items.lastIndex) Divider()
        }
    }
}
