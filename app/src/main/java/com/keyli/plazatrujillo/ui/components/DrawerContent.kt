//DrawerContent.kt
package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keyli.plazatrujillo.ui.theme.*

@Composable
fun UsuarioScreen(navController: NavController) {

    var search by remember { mutableStateOf("") }

    val sampleUsers = listOf(
        UserData("Juan Pérez", "juan@plaza.com", "Administrador", "S/ 3,500", "15/01/2024", "Activo"),
        UserData("Ana López", "ana@plaza.com", "Recepcionista", "S/ 2,200", "20/02/2024", "Activo"),
        UserData("Carlos Ruiz", "carlos@plaza.com", "Mantenimiento", "S/ 1,800", "10/03/2024", "Inactivo")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // ------------------ CARD PRINCIPAL ------------------
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(14.dp)) {

                Text(
                    "Gestión de Usuarios",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBlack,
                    letterSpacing = 0.sp
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar usuario, correo…", color = TextGray, letterSpacing = 0.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = LightBackground,
                        unfocusedContainerColor = LightBackground,
                        cursorColor = OrangePrimary,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = TextBlack
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate("new_usuario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Crear Usuario", color = Color.White, letterSpacing = 0.sp)
                }

                Spacer(Modifier.height(8.dp))

                AlertCardDemo()

                Spacer(Modifier.height(12.dp))

                UsersHorizontalTable(sampleUsers)
            }
        }

        Spacer(Modifier.height(18.dp))

        // ------------------ REGISTRO DE ASISTENCIA ------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(14.dp)) {

                Text(
                    "Registro de Asistencia del Personal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextBlack,
                    letterSpacing = 0.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    "Control mensual de asistencia",
                    fontSize = 14.sp,
                    color = TextGray,
                    letterSpacing = 0.sp
                )

                Text(
                    "(DS N° 004-011-2000-TR)",
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 0.sp
                )

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = { navController.navigate("new_register") },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Nuevo Registro", color = Color.White, letterSpacing = 0.sp)
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

data class UserData(
    val nombre: String,
    val correo: String,
    val rol: String,
    val salario: String,
    val fechaEntrada: String,
    val estado: String
)

@Composable
private fun AlertCardDemo() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F0)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, null, tint = StatusRed)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Modo demostración", fontWeight = FontWeight.SemiBold, color = StatusRed, letterSpacing = 0.sp)
                Text("Los usuarios mostrados son datos de ejemplo.", color = StatusRed.copy(alpha = 0.85f), letterSpacing = 0.sp)
            }
        }
    }
}

@Composable
private fun UsersHorizontalTable(users: List<UserData>) {

    val horizontalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF9F9F9))
            .horizontalScroll(horizontalScroll)
    ) {

        Row(
            modifier = Modifier
                .background(Color(0xFFF4F4F4))
                .padding(vertical = 10.dp)
                .width(980.dp)
                .padding(horizontal = 10.dp)
        ) {
            HeaderText("Usuario", 160)
            HeaderText("Correo", 220)
            HeaderText("Rol", 140)
            HeaderText("Salario", 110)
            HeaderText("F. Entrada", 140)
            HeaderText("Estado", 100)
            HeaderText("Acciones", 110)
        }

        Divider()

        users.forEachIndexed { index, user ->
            Row(
                modifier = Modifier
                    .width(980.dp)
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                CellText(user.nombre, 160)
                CellText(user.correo, 220)
                CellText(user.rol, 140)
                CellText(user.salario, 110)
                CellText(user.fechaEntrada, 140)

                Row(Modifier.width(100.dp), verticalAlignment = Alignment.CenterVertically) {
                    val color = if (user.estado == "Activo") StatusGreen else Color.Gray
                    Box(
                        Modifier
                            .size(9.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(user.estado, color = color, letterSpacing = 0.sp)
                }

                Row(
                    Modifier.width(110.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Edit, null, tint = OrangeSecondary)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Delete, null, tint = StatusRed)
                    }
                }
            }
            if (index < users.lastIndex) Divider()
        }
    }
}

@Composable
private fun HeaderText(text: String, width: Int) {
    Text(
        text,
        modifier = Modifier.width(width.dp),
        fontWeight = FontWeight.Medium,
        color = TextBlack,
        letterSpacing = 0.sp
    )
}

@Composable
private fun CellText(text: String, width: Int) {
    Text(
        text,
        modifier = Modifier.width(width.dp),
        color = TextBlack,
        letterSpacing = 0.sp
    )
}