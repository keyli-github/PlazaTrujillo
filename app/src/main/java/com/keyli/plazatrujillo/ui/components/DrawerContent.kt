package com.keyli.plazatrujillo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.R // Asegúrate que importe tu R
import com.keyli.plazatrujillo.ui.theme.OrangePrimary

// Modelo de datos para las opciones
data class DrawerMenuItem(
    val id: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun DrawerContent(
    items: List<DrawerMenuItem>,
    currentRoute: String,
    onItemClick: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Fondo blanco como en la foto
            .padding(16.dp)
    ) {
        // 1. Título "MENU" pequeño
        Text(
            text = "MENU",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )

        // 2. Lista de Opciones
        items.forEach { item ->
            val isSelected = currentRoute == item.id

            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        // El icono naranja si está seleccionado, gris si no
                        tint = if (isSelected) OrangePrimary else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = {
                    onItemClick(item.id)
                    onCloseDrawer()
                },
                // Colores personalizados para igualar tu foto
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFFFFF3E0), // Naranja muy clarito de fondo
                    selectedTextColor = OrangePrimary,
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp), // Efecto redondeado a la derecha
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja lo siguiente hacia abajo

        // 3. Tarjeta Inferior "Hotel Plaza Trujillo"
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hotel Plaza Trujillo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sistema de administración para la gestión integral del hotel.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onItemClick("dashboard")
                        onCloseDrawer()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir al Dashboard", color = Color.White)
                }
            }
        }
    }
}