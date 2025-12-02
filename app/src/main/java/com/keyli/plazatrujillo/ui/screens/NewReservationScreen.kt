package com.keyli.plazatrujillo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Estados del proceso de guardado
enum class SaveState {
    IDLE,       // Esperando input
    LOADING,    // Círculo cargando
    SUCCESS     // Check naranja y mensaje
}

@Composable
fun NewReservationScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Controlamos el estado de la animación aquí
    var saveState by remember { mutableStateOf(SaveState.IDLE) }

    // --- DATOS MAESTROS ---
    val docTypes = listOf("DNI", "RUC", "CE")
    val channelTypes = listOf("Booking.com", "WhatsApp", "Venta Directa")
    val roomTypes = listOf("Simple", "Doble", "Triple", "Matrimonial")

    val roomList = listOf(
        "111 - Piso 1", "112 - Piso 1", "113 - Piso 1",
        "210 - Piso 2", "211 - Piso 2", "212 - Piso 2", "213 - Piso 2", "214 - Piso 2", "215 - Piso 2",
        "310 - Piso 3", "311 - Piso 3", "312 - Piso 3", "313 - Piso 3", "314 - Piso 3", "315 - Piso 3"
    )

    // --- ESTADOS FORMULARIO ---
    var selectedDocType by remember { mutableStateOf(docTypes[0]) }
    var docNumber by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf(channelTypes[0]) }
    var guestName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var dep by remember { mutableStateOf("") }
    var prov by remember { mutableStateOf("") }
    var dist by remember { mutableStateOf("") }

    // Fechas (dd/mm/aaaa)
    var checkIn by remember { mutableStateOf("") }
    var checkOut by remember { mutableStateOf("") }

    val areDatesSelected = checkIn.length >= 10 && checkOut.length >= 10

    var selectedRoomNumber by remember { mutableStateOf("") }
    var selectedRoomType by remember { mutableStateOf(roomTypes[0]) }

    var timeArrival by remember { mutableStateOf("") }
    var timeDeparture by remember { mutableStateOf("") }

    var adults by remember { mutableStateOf("1") }
    var kids by remember { mutableStateOf("0") }
    val totalPeople = (adults.toIntOrNull() ?: 0) + (kids.toIntOrNull() ?: 0)

    var totalMoney by remember { mutableStateOf("") }
    var isPaid by remember { mutableStateOf(false) }

    // Acompañante
    var showCompanions by remember { mutableStateOf(false) }
    var compDocType by remember { mutableStateOf(docTypes[0]) }
    var compDocNum by remember { mutableStateOf("") }
    var compName by remember { mutableStateOf("") }
    var compAddress by remember { mutableStateOf("") }
    var compDep by remember { mutableStateOf("") }
    var compProv by remember { mutableStateOf("") }
    var compDist by remember { mutableStateOf("") }


    // --- UI PRINCIPAL (Usamos un Box para poder superponer la animación) ---
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. CONTENIDO DEL FORMULARIO
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .verticalScroll(scrollState)
        ) {
            // --- CABECERA ---
            Surface(shadowElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Nueva Reserva", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp), color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Complete la información del huésped", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(MaterialTheme.colorScheme.background.copy(alpha=0.1f), RoundedCornerShape(50))) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TARJETA PRINCIPAL ---
            SectionCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionTitle("Datos del Titular")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(0.35f)) {
                            ReservationFormLabel("Tipo")
                            ReservationCustomDropdown(options = docTypes, selected = selectedDocType, onSelected = { selectedDocType = it })
                        }
                        Column(modifier = Modifier.weight(0.65f)) {
                            ReservationFormLabel("Número")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ReservationCustomTextField(value = docNumber, onValueChange = { docNumber = it }, modifier = Modifier.weight(1f), isNumber = true, placeholder = "Documento")
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(OrangePrimary).clickable { }, contentAlignment = Alignment.Center) {
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) { ReservationFormLabel("Dep."); ReservationCustomTextField(value = dep, onValueChange = { dep = it }) }
                        Column(Modifier.weight(1f)) { ReservationFormLabel("Prov."); ReservationCustomTextField(value = prov, onValueChange = { prov = it }) }
                        Column(Modifier.weight(1f)) { ReservationFormLabel("Dist."); ReservationCustomTextField(value = dist, onValueChange = { dist = it }) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- TARJETA ESTADÍA ---
            SectionCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionTitle("Detalles de Estadía")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            ReservationFormLabel("Check-in *")
                            ReservationCustomTextField(value = checkIn, onValueChange = { checkIn = formatDateInput(it) }, icon = Icons.Outlined.CalendarToday, placeholder = "dd/mm/aaaa", isNumber = true)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            ReservationFormLabel("Check-out *")
                            ReservationCustomTextField(value = checkOut, onValueChange = { checkOut = formatDateInput(it) }, icon = Icons.Outlined.CalendarToday, placeholder = "dd/mm/aaaa", isNumber = true)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.background)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1.3f)) {
                            ReservationFormLabel("Habitación *")
                            ReservationCustomDropdown(options = roomList, selected = if(selectedRoomNumber.isEmpty()) "Seleccionar" else selectedRoomNumber, onSelected = { selectedRoomNumber = it }, enabled = areDatesSelected)
                        }
                        Column(modifier = Modifier.weight(0.7f)) {
                            ReservationFormLabel("Tipo")
                            ReservationCustomDropdown(options = roomTypes, selected = selectedRoomType, onSelected = { selectedRoomType = it }, enabled = areDatesSelected)
                        }
                    }
                    if(!areDatesSelected) {
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = StatusBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Complete las fechas (dd/mm/aaaa) para desbloquear", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            ReservationFormLabel("Llegada")
                            ReservationCustomTextField(value = timeArrival, onValueChange = { timeArrival = it }, icon = Icons.Outlined.Schedule, placeholder = "--:--", enabled = areDatesSelected)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            ReservationFormLabel("Salida")
                            ReservationCustomTextField(value = timeDeparture, onValueChange = { timeDeparture = it }, icon = Icons.Outlined.Schedule, placeholder = "--:--", enabled = areDatesSelected)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SectionCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    SectionTitle("Pago y Ocupantes")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) { ReservationFormLabel("Adultos"); ReservationCustomTextField(value = adults, onValueChange = { adults = it }, isNumber = true, centered = true) }
                        Column(modifier = Modifier.weight(1f)) { ReservationFormLabel("Niños"); ReservationCustomTextField(value = kids, onValueChange = { kids = it }, isNumber = true, centered = true) }
                        Column(modifier = Modifier.weight(1f)) { ReservationFormLabel("Total"); ReservationCustomTextField(value = totalPeople.toString(), onValueChange = {}, isNumber = true, readOnly = true, centered = true) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Bottom) {
                        Column(modifier = Modifier.weight(1f)) {
                            ReservationFormLabel("Monto Total (S/)")
                            ReservationCustomTextField(value = totalMoney, onValueChange = { totalMoney = it }, isNumber = true, icon = Icons.Outlined.CreditCard, placeholder = "0.00")
                        }
                        Column(modifier = Modifier.width(130.dp)) {
                            ReservationFormLabel("Estado Pago")
                            PaymentSwitch(isPaid = isPaid, onToggle = { isPaid = it })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SectionCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        SectionTitle("Acompañantes", modifier = Modifier.padding(bottom = 0.dp))
                        if (!showCompanions) {
                            TextButton(onClick = { showCompanions = true }) {
                                Text("+ Agregar", color = OrangePrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (showCompanions) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(0.35f)) { ReservationCustomDropdown(options = docTypes, selected = compDocType, onSelected = { compDocType = it }) }
                            Box(Modifier.weight(0.65f)) { ReservationCustomTextField(value = compDocNum, onValueChange = { compDocNum = it }, placeholder = "Número", isNumber = true) }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ReservationCustomTextField(value = compName, onValueChange = { compName = it }, placeholder = "Nombre completo")
                        Spacer(modifier = Modifier.height(12.dp))
                        ReservationCustomTextField(value = compAddress, onValueChange = { compAddress = it }, placeholder = "Dirección", icon = Icons.Outlined.LocationOn)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(Modifier.weight(1f)) { ReservationCustomTextField(value = compDep, onValueChange = { compDep = it }, placeholder = "Dep.") }
                            Column(Modifier.weight(1f)) { ReservationCustomTextField(value = compProv, onValueChange = { compProv = it }, placeholder = "Prov.") }
                            Column(Modifier.weight(1f)) { ReservationCustomTextField(value = compDist, onValueChange = { compDist = it }, placeholder = "Dist.") }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth().clickable { showCompanions = false }, horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Delete, contentDescription = null, tint = StatusRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Eliminar Acompañante", color = StatusRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        // LOGICA DEL BOTON
                        if (saveState == SaveState.IDLE) {
                            scope.launch {
                                // 1. Mostrar carga
                                saveState = SaveState.LOADING
                                delay(2000) // Simular guardado en BD

                                // 2. Mostrar éxito
                                saveState = SaveState.SUCCESS
                                delay(1500) // Dejar que se vea la animación bonita

                                // 3. Salir
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text("Crear Reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }

        // 2. OVERLAY DE ANIMACION (SE PONE ENCIMA DEL FORMULARIO)
        SuccessOverlay(state = saveState)
    }
}

// ==========================================
// COMPONENTE VISUAL DE EXITO (ANIMACION)
// ==========================================
@Composable
fun SuccessOverlay(state: SaveState) {
    AnimatedVisibility(
        visible = state != SaveState.IDLE,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f)) // Fondo casi opaco
                .clickable(enabled = false) {}, // Bloquear clicks
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ANIMACION DE TRANSICION: LOADING -> CHECK
                Box(contentAlignment = Alignment.Center) {

                    // 1. LOADING (Solo visible si state == LOADING)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state == SaveState.LOADING,
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            color = OrangePrimary,
                            strokeWidth = 4.dp
                        )
                    }

                    // 2. CHECK (Solo visible si state == SUCCESS)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = state == SaveState.SUCCESS,
                        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)) + fadeIn()
                    ) {
                        // Circulo Naranja + Icono
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Éxito",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TEXTO "RESERVA CREADA"
                androidx.compose.animation.AnimatedVisibility(
                    visible = state == SaveState.SUCCESS,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) +
                            scaleIn(initialScale = 0.8f)
                ) {
                    Text(
                        text = "¡Reserva creada!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

// ==========================================
// UTILIDADES
// ==========================================
fun formatDateInput(input: String): String {
    if (input.isEmpty()) return ""
    val digits = input.filter { it.isDigit() }
    val trimmed = if (digits.length > 8) digits.substring(0, 8) else digits
    return when {
        trimmed.length >= 5 -> "${trimmed.substring(0, 2)}/${trimmed.substring(2, 4)}/${trimmed.substring(4)}"
        trimmed.length >= 3 -> "${trimmed.substring(0, 2)}/${trimmed.substring(2)}"
        else -> trimmed
    }
}

@Composable
fun isAppDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.onSurface.luminance() > 0.5f
}

// ==========================================
// COMPONENTES DE UI
// ==========================================
@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) { content() }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier.padding(bottom = 16.dp)) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OrangePrimary, modifier = modifier)
}

@Composable
private fun ReservationFormLabel(text: String) {
    Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
}

@Composable
private fun ReservationCustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isNumber: Boolean = false,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    centered: Boolean = false,
    icon: ImageVector? = null
) {
    val isDark = isAppDarkTheme()
    val containerColor = if (isDark) Color(0xFF2D2D2D) else Color(0xFFF3F4F6)
    val contentColor = MaterialTheme.colorScheme.onSurface
    val alphaModifier = if (enabled) Modifier else Modifier.alpha(0.3f)

    BasicTextField(
        value = value,
        onValueChange = { if (enabled) onValueChange(it) },
        readOnly = readOnly,
        enabled = enabled,
        textStyle = TextStyle(fontSize = 15.sp, color = contentColor, fontWeight = FontWeight.Medium, textAlign = if(centered) androidx.compose.ui.text.style.TextAlign.Center else androidx.compose.ui.text.style.TextAlign.Start),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier.then(alphaModifier).height(52.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(containerColor).padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) { Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp)); Spacer(modifier = Modifier.width(12.dp)) }
                Box(modifier = Modifier.weight(1f), contentAlignment = if(centered) Alignment.Center else Alignment.CenterStart) {
                    if (value.isEmpty()) { Text(placeholder, color = contentColor.copy(alpha = 0.4f), fontSize = 14.sp) }
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
    onSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val isDark = isAppDarkTheme()
    val containerColor = if (isDark) Color(0xFF2D2D2D) else Color(0xFFF3F4F6)
    val alphaModifier = if (enabled) Modifier else Modifier.alpha(0.3f)

    Box(
        modifier = Modifier.then(alphaModifier).height(52.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(containerColor).clickable(enabled = enabled) { expanded = true }.padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = selected, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = if(enabled) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@Composable
private fun PaymentSwitch(isPaid: Boolean, onToggle: (Boolean) -> Unit) {
    val isDark = isAppDarkTheme()
    val containerColor = if (isDark) Color(0xFF2D2D2D) else Color(0xFFF3F4F6)
    Row(modifier = Modifier.height(52.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(containerColor).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(10.dp)).background(if (!isPaid) MaterialTheme.colorScheme.surface else Color.Transparent).shadow(if (!isPaid) 2.dp else 0.dp, RoundedCornerShape(10.dp)).clickable { onToggle(false) }, contentAlignment = Alignment.Center) {
            Text("Pendiente", color = if (!isPaid) StatusRed else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(10.dp)).background(if (isPaid) StatusGreen else Color.Transparent).clickable { onToggle(true) }, contentAlignment = Alignment.Center) {
            Text("Pagado", color = if (isPaid) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}