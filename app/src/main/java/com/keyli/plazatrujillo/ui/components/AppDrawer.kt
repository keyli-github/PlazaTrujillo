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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.R // Asegúrate de importar tu R
import com.keyli.plazatrujillo.ui.navigation.drawerOptions
import com.keyli.plazatrujillo.ui.theme.OrangePrimary

@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White, // Fondo blanco
        drawerContentColor = Color.Black,
        modifier = Modifier.width(300.dp) // Ancho fijo para que se vea elegante
    ) {
        // Cabecera del Drawer (Logo)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Usa tu logo aquí
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Plaza Trujillo",
                modifier = Modifier.size(120.dp)
            )
        }

        Text(
            text = "MENU",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
        )

        // Lista de Opciones
        drawerOptions.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) OrangePrimary else Color.Gray
                    )
                },
                selected = isSelected,
                onClick = {
                    onNavigate(item.route)
                    onCloseDrawer()
                },
                // Colores personalizados para el efecto naranja
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = OrangePrimary.copy(alpha = 0.1f), // Fondo naranja suave
                    selectedTextColor = OrangePrimary,
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp), // Borde redondeado a la derecha
                modifier = Modifier.padding(end = 16.dp) // Espacio a la derecha
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja el footer hacia abajo

        // Tarjeta Inferior (Footer)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hotel Plaza Trujillo", fontWeight = FontWeight.Bold)
                Text("Sistema de Gestión", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onNavigate("dashboard"); onCloseDrawer() },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir al Dashboard")
                }
            }
        }
    }
}