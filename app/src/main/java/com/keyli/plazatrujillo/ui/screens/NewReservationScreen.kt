package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

@Composable
fun NewReservationScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    // --- ESTADOS ---
    val docTypes = listOf("DNI", "RUC", "CE")
    val channelTypes = listOf("Booking.com", "WhatsApp", "Venta Directa")
    val roomTypes = listOf("Simple", "Doble", "Triple", "Matrimonial")

    var selectedDocType by remember { mutableStateOf(docTypes[0]) }
    var docNumber by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf(channelTypes[0]) }
    var guestName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var dep by remember { mutableStateOf("") }
    var prov by remember { mutableStateOf("") }
    var dist by remember { mutableStateOf("") }

    var roomNumber by remember { mutableStateOf("") }
    var selectedRoomType by remember { mutableStateOf(roomTypes[0]) }
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }

    var timeArrival by remember { mutableStateOf("") }
    var timeDeparture by remember { mutableStateOf("") }
    var adults by remember { mutableStateOf("1") }
    var kids by remember { mutableStateOf("0") }
    var totalPeople by remember { mutableStateOf("1") }

    var totalMoney by remember { mutableStateOf("0.00") }
    var isPaid by remember { mutableStateOf(false) }

    var showCompanions by remember { mutableStateOf(false) }

    // Fondo General Gris (Estilo Profile)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fondo gris suave
            .statusBarsPadding()
            .verticalScroll(scrollState)
    ) {
        // --- CABECERA (Fondo Blanco para resaltar como en Profile) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nueva Reserva",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    text = "Complete la información del huésped",
                    fontSize = 13.sp,
                    color = TextGray
                )
            }
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TARJETA DEL FORMULARIO PRINCIPAL ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp), // Bordes redondeados estilo Profile
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                // --- FILA 1: DOC + NUMERO ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(0.35f)) {
                        ReservationFormLabel("Tipo")
                        ReservationCustomDropdown(options = docTypes, selected = selectedDocType, onSelected = { selectedDocType = it })
                    }
                    Column(modifier = Modifier.weight(0.65f)) {
                        ReservationFormLabel("Número")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ReservationCustomTextField(
                                value = docNumber,
                                onValueChange = { docNumber = it },
                                placeholder = "",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Botón de búsqueda pequeño
                            Box(
                                modifier = Modifier
                                    .size(50.dp) // Cuadrado redondeado
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrangePrimary)
                                    .clickable { /* Lógica buscar */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ReservationFormLabel("Canal de Reserva")
                ReservationCustomDropdown(options = channelTypes, selected = selectedChannel, onSelected = { selectedChannel = it })

                Spacer(modifier = Modifier.height(16.dp))

                ReservationFormLabel("Huésped *")
                ReservationCustomTextField(value = guestName, onValueChange = { guestName = it }, placeholder = "Nombre completo", icon = Icons.Outlined.Person)

                Spacer(modifier = Modifier.height(16.dp))

                ReservationFormLabel("Dirección")
                ReservationCustomTextField(value = address, onValueChange = { address = it }, placeholder = "Dirección completa", icon = Icons.Outlined.LocationOn)

                Spacer(modifier = Modifier.height(16.dp))

                // Ubicación
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Dep.")
                        ReservationCustomTextField(value = dep, onValueChange = { dep = it })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Prov.")
                        ReservationCustomTextField(value = prov, onValueChange = { prov = it })
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Dist.")
                        ReservationCustomTextField(value = dist, onValueChange = { dist = it })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = Color(0xFFF5F5F5), thickness = 2.dp)
                Spacer(modifier = Modifier.height(20.dp))

                // Habitación
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(0.8f)) {
                        ReservationFormLabel("Habitación *")
                        ReservationCustomTextField(value = roomNumber, onValueChange = { roomNumber = it }, placeholder = "101")
                    }
                    Column(modifier = Modifier.weight(1.2f)) {
                        ReservationFormLabel("Tipo *")
                        ReservationCustomDropdown(options = roomTypes, selected = selectedRoomType, onSelected = { selectedRoomType = it })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fechas
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Check-in *")
                        ReservationCustomTextField(value = checkIn, onValueChange = { checkIn = it }, icon = Icons.Outlined.CalendarToday, placeholder = "dd/mm")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Check-out *")
                        ReservationCustomTextField(value = checkOut, onValueChange = { checkOut = it }, icon = Icons.Outlined.CalendarToday, placeholder = "dd/mm")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Horas
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Llegada")
                        ReservationCustomTextField(value = timeArrival, onValueChange = { timeArrival = it }, icon = Icons.Outlined.Schedule, placeholder = "--:--")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Salida")
                        ReservationCustomTextField(value = timeDeparture, onValueChange = { timeDeparture = it }, icon = Icons.Outlined.Schedule, placeholder = "--:--")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Personas
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Adultos")
                        ReservationCustomTextField(value = adults, onValueChange = { adults = it }, isNumber = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Niños")
                        ReservationCustomTextField(value = kids, onValueChange = { kids = it }, isNumber = true)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Total")
                        ReservationCustomTextField(value = totalPeople, onValueChange = { totalPeople = it }, isNumber = true, readOnly = true)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = Color(0xFFF5F5F5), thickness = 2.dp)
                Spacer(modifier = Modifier.height(20.dp))

                // Dinero
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
                    Column(modifier = Modifier.weight(1f)) {
                        ReservationFormLabel("Total (S/)")
                        ReservationCustomTextField(value = totalMoney, onValueChange = { totalMoney = it }, isNumber = true, icon = Icons.Outlined.CreditCard)
                    }
                    Column(modifier = Modifier.width(110.dp)) {
                        ReservationFormLabel("Pagado")
                        Row(
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isPaid) Color.White else Color.Transparent)
                                    .clickable { isPaid = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No", color = if (!isPaid) TextBlack else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isPaid) OrangePrimary else Color.Transparent)
                                    .clickable { isPaid = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sí", color = if (isPaid) Color.White else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SECCIÓN ACOMPAÑANTES ---
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Acompañantes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    if (!showCompanions) {
                        TextButton(onClick = { showCompanions = true }) {
                            Text("+ Agregar", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (showCompanions) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Formulario de acompañante simplificado
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(Modifier.weight(0.35f)) { ReservationCustomDropdown(options = docTypes, selected = "DNI", onSelected = {}) }
                        Box(Modifier.weight(0.65f)) { ReservationCustomTextField(value = "", onValueChange = {}, placeholder = "Número") }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    ReservationCustomTextField(value = "", onValueChange = {}, placeholder = "Nombre completo")
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { showCompanions = false },
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Eliminar", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- BOTONES ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text("Cancelar", color = TextGray, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Crear Reserva", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// --- COMPONENTES AUXILIARES ESTILIZADOS (Estilo Profile) ---

@Composable
private fun ReservationFormLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = TextGray,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

// ESTE ES EL CAMPO CLAVE QUE CAMBIA EL DISEÑO (Fondo gris, sin borde, icono naranja)
@Composable
private fun ReservationCustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isNumber: Boolean = false,
    readOnly: Boolean = false,
    icon: ImageVector? = null // Opción para icono
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        textStyle = TextStyle(fontSize = 14.sp, color = TextBlack),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier
                    .height(56.dp) // Un poco más alto
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)) // FONDO GRIS REDONDEADO
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = Color.LightGray, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun ReservationCustomDropdown(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)) // FONDO GRIS
            .clickable { expanded = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = selected, fontSize = 14.sp, color = TextBlack)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = OrangePrimary)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 14.sp) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}