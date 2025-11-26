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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.R
import com.keyli.plazatrujillo.ui.navigation.drawerOptions
import com.keyli.plazatrujillo.ui.theme.OrangePrimary

@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    // Obtenemos los colores actuales del tema (ya sea claro u oscuro)
    val backgroundColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    val selectedBgColor = OrangePrimary.copy(alpha = 0.15f) // Un poco más visible en oscuro

    ModalDrawerSheet(
        drawerContainerColor = backgroundColor, // Ahora usa el color del tema
        drawerContentColor = contentColor,
        modifier = Modifier.width(300.dp)
    ) {
        // Cabecera del Drawer (Logo)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Si tu logo es una imagen con fondo transparente, se verá bien.
            // Si es negra, en modo oscuro no se verá.
            // Aquí asumimos que el logo funciona o usamos un tinte si es necesario.
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
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Gris adaptable
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
                        // Si no está seleccionado, usa el color del texto del tema (blanco en dark mode)
                        tint = if (isSelected) OrangePrimary else contentColor.copy(alpha = 0.6f)
                    )
                },
                selected = isSelected,
                onClick = {
                    onNavigate(item.route)
                    onCloseDrawer()
                },
                // Colores dinámicos
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = selectedBgColor,
                    selectedTextColor = OrangePrimary,
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = contentColor // Se adapta a blanco/negro
                ),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tarjeta Inferior (Footer)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            // Usamos un color ligeramente diferente al fondo para que resalte
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hotel Plaza Trujillo",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sistema de Gestión",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onNavigate("dashboard"); onCloseDrawer() },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir al Dashboard", color = Color.White)
                }
            }
        }
    }
}