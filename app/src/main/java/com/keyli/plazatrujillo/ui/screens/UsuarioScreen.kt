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

// Data Class (Mismo modelo)
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

    var search by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val usersList = listOf(
        PersonalUI(1,"Marco Gutierrez","marco.gutierrez@plaza.com","Administrador","S/ 3,500","15/01/2024",true),
        PersonalUI(2,"Frank Castro","frank.castro@plaza.com","Recepcionista","S/ 2,200","20/02/2024",true),
        PersonalUI(3,"Keyli Roncal","keyli.roncal@plaza.com","Mantenimiento","S/ 1,800","10/03/2024",true),
        PersonalUI(4,"Karina Guerrero","karina.guerrero@plaza.com","Administrador","S/ 3,500","05/04/2024",true),
        PersonalUI(5,"Cristian Zavaleta","cristian.zavaleta@plaza.com","Seguridad","S/ 1,900","12/04/2024",false),
        PersonalUI(6,"Luis Alonso","luis.alonso@plaza.com","Logística","S/ 2,500","01/05/2024",true)
    )

    val filteredUsers = usersList.filter {
        it.name.contains(search, true) || it.email.contains(search, true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground) // Color Global
            .verticalScroll(scrollState)
            .padding(20.dp) // Aumentado padding general
    ) {

        Text(
            text = "Gestión de Personal",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // --- TARJETA DE BUSQUEDA ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp) // Elevación sutil
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por nombre o correo...", color = TextGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = OrangePrimary)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = Color(0xFFEEEEEE),
                        focusedContainerColor = LightBackground,
                        unfocusedContainerColor = LightBackground,
                        cursorColor = OrangePrimary,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = TextBlack
                    ),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("new_usuario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp), // Botón más alto
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
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {

            Column {

                Text(
                    text = "Colaboradores Registrados",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextBlack,
                    modifier = Modifier.padding(20.dp)
                )

                Divider(color = Color(0xFFF0F0F0))

                UnifiedUserTable(filteredUsers)
            }
        }

        Spacer(Modifier.height(50.dp)) // Espacio final extra para scroll
    }
}

@Composable
private fun UnifiedUserTable(users: List<PersonalUI>) {

    val hScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(hScroll)
    ) {
        // Cabecera con fondo gris suave del tema
        Row(
            modifier = Modifier
                .background(LightBackground)
                .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeader("Usuario / Email", 240.dp) // Ancho aumentado
            TableHeader("Rol / Salario", 200.dp)
            TableHeader("F. Ingreso", 130.dp)
            TableHeader("Estado", 110.dp)
            TableHeader("Acciones", 100.dp)
        }

        Divider(color = Color(0xFFE0E0E0))

        if (users.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(30.dp), contentAlignment = Alignment.Center) {
                Text("No se encontraron resultados", color = TextGray)
            }
        } else {
            users.forEach {
                UserRowItem(it)
                Divider(color = Color(0xFFF5F5F5))
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
        color = TextGray,
        fontSize = 13.sp
    )
}

@Composable
private fun UserRowItem(user: PersonalUI) {

    Row(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp), // Padding vertical aumentado para evitar lo "aplastado"
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(Modifier.width(240.dp).padding(end = 8.dp)) {
            Text(user.name, fontWeight = FontWeight.SemiBold, color = TextBlack, fontSize = 15.sp)
            Text(user.email, fontSize = 13.sp, color = TextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }

        Column(Modifier.width(200.dp).padding(end = 8.dp)) {
            Text(user.role, color = OrangePrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(user.salary, fontSize = 13.sp, color = TextBlack)
        }

        Text(
            text = user.entryDate,
            modifier = Modifier.width(130.dp),
            fontSize = 14.sp,
            color = TextBlack
        )

        Box(Modifier.width(110.dp)) {
            StatusChip(user.isActive)
        }

        Row(Modifier.width(100.dp)) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextGray)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusRed)
            }
        }
    }
}

@Composable
fun StatusChip(isActive: Boolean) {
    // Usamos los colores globales StatusGreen y StatusRed
    val baseColor = if (isActive) StatusGreen else StatusRed
    val bgColor = baseColor.copy(alpha = 0.1f) // 10% de opacidad para el fondo
    val txt = if (isActive) "Activo" else "Inactivo"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50)) // Píldora redondeada
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(txt, color = baseColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}