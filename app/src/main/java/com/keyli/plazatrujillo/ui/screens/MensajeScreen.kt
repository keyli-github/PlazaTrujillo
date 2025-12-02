package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- MODELOS DE DATOS ---

data class Contact(
    val id: Int,
    val name: String,
    val lastMessage: String,
    val role: String,
    val time: String,
    val online: Boolean = false,
    val unreadCount: Int = 0
)

data class UserMessage(
    val id: Long,
    val text: String,
    val isMe: Boolean, // True si yo lo envié
    val timestamp: String = getCurrentTime()
)

fun getCurrentTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajeScreen(navController: NavHostController) {
    // --- COLORES DINÁMICOS ---
    val containerBg = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    // --- LISTA DE CONTACTOS SOLICITADA ---
    val sampleContacts = remember {
        mutableStateListOf(
            Contact(1, "Keyli Roncal", "Por favor revisar el cierre de caja de hoy.", "Gerencia", "10:30", online = true, unreadCount = 1),
            Contact(2, "Frank Castro", "Huésped de la 204 solicita cambio de toallas.", "Recepción", "09:45", online = true),
            Contact(3, "Karina Xiomara", "Habitación 301 lista para inspección.", "Housekeeping", "09:15"),
            Contact(4, "Luis Alonso", "El aire acondicionado del lobby ya está reparado.", "Mantenimiento", "Ayer", online = false),
            Contact(5, "Cristian Zavaleta", "Turno de seguridad iniciado sin novedades.", "Seguridad", "Ayer")
        )
    }

    var query by remember { mutableStateOf("") }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }

    // Filtro de búsqueda
    val filtered = remember(query, sampleContacts) {
        if (query.isBlank()) sampleContacts.toList()
        else sampleContacts.filter {
            it.name.contains(query, true) ||
                    it.role.contains(query, true)
        }
    }

    // --- VISTA PRINCIPAL (LISTA DE CHATS) ---
    if (selectedContact == null) {
        Scaffold(
            containerColor = containerBg, // REEMPLAZADO: Fondo dinámico
            topBar = {
                // Header tipo App de Mensajería
                Surface(color = surfaceColor, shadowElevation = 2.dp) { // REEMPLAZADO: Color Superficie
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = "Mensajes",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor, // REEMPLAZADO: Color Texto
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Barra de Búsqueda
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Buscar personal...", color = subTextColor) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = subTextColor)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = containerBg, // REEMPLAZADO
                                unfocusedContainerColor = containerBg, // REEMPLAZADO
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(surfaceColor) // REEMPLAZADO: Fondo de lista dinámico
            ) {
                itemsIndexed(filtered) { index, contact ->
                    ContactItem(
                        contact = contact,
                        onClick = { selectedContact = contact },
                        textColor = textColor,
                        subTextColor = subTextColor,
                        bgColor = containerBg,
                        surfaceColor = surfaceColor
                    )
                    // Divisor sutil
                    if (index < filtered.lastIndex) {
                        HorizontalDivider(
                            color = subTextColor.copy(alpha = 0.1f), // REEMPLAZADO: Divider sutil
                            thickness = 1.dp,
                            modifier = Modifier.padding(start = 80.dp)
                        )
                    }
                }
            }
        }
    } else {
        // --- VISTA DE CONVERSACIÓN (OVERLAY) ---
        ConversationScreen(
            contact = selectedContact!!,
            onBack = { selectedContact = null }
        )
    }
}

// --- ITEM DE LA LISTA DE CONTACTOS ---
@Composable
fun ContactItem(
    contact: Contact,
    onClick: () -> Unit,
    textColor: Color,
    subTextColor: Color,
    bgColor: Color,
    surfaceColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar con indicador Online
        Box {
            Surface(
                shape = CircleShape,
                color = bgColor, // REEMPLAZADO: Fondo gris suave/negro suave
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = contact.name.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = subTextColor,
                        fontSize = 20.sp
                    )
                }
            }
            if (contact.online) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(StatusGreen, CircleShape)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(surfaceColor) // REEMPLAZADO: Borde falso del color de la tarjeta
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info Texto
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = textColor // REEMPLAZADO
                )
                Text(
                    text = contact.time,
                    fontSize = 12.sp,
                    color = if(contact.unreadCount > 0) OrangePrimary else subTextColor,
                    fontWeight = if(contact.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contact.lastMessage,
                    fontSize = 14.sp,
                    color = subTextColor, // REEMPLAZADO
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (contact.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(OrangePrimary, CircleShape)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = contact.role,
                fontSize = 11.sp,
                color = OrangePrimary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// --- PANTALLA DE CHAT INDIVIDUAL ---
@Composable
fun ConversationScreen(contact: Contact, onBack: () -> Unit) {
    // Colores dinámicos locales
    val containerBg = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    // Mensajes simulados iniciales
    val messages = remember {
        mutableStateListOf(
            UserMessage(1, "Hola ${contact.name.split(" ")[0]}, bienvenido al sistema.", isMe = true, timestamp = "09:00"),
            UserMessage(2, contact.lastMessage, isMe = false, timestamp = contact.time)
        )
    }

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = containerBg, // REEMPLAZADO
        modifier = Modifier.imePadding(),
        topBar = {
            // Header del Chat
            Surface(shadowElevation = 4.dp, color = surfaceColor) { // REEMPLAZADO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = textColor) // REEMPLAZADO
                    }

                    // Avatar pequeño
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(containerBg), // REEMPLAZADO: contraste con header
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.name.take(1),
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor) // REEMPLAZADO
                        Text(
                            text = if(contact.online) "En línea" else contact.role,
                            fontSize = 12.sp,
                            color = if(contact.online) StatusGreen else subTextColor
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Input Flotante
            Surface(
                color = surfaceColor, // REEMPLAZADO
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Escribe un mensaje...", fontSize = 14.sp, color = subTextColor) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = subTextColor.copy(alpha = 0.3f),
                            focusedTextColor = textColor, // Para que se vea en oscuro
                            unfocusedTextColor = textColor
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                messages.add(UserMessage(System.currentTimeMillis(), inputText, isMe = true))
                                val tempText = inputText
                                inputText = ""
                                scope.launch {
                                    delay(100)
                                    listState.animateScrollToItem(messages.lastIndex)
                                    // Respuesta automática simulada
                                    delay(1000)
                                    messages.add(UserMessage(System.currentTimeMillis(), "Entendido, gracias.", isMe = false))
                                    delay(100)
                                    listState.animateScrollToItem(messages.lastIndex)
                                }
                            }
                        },
                        containerColor = OrangePrimary,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp),
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { padding ->
        // Area de mensajes
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(messages) { _, msg ->
                ChatBubble(msg)
            }
        }
    }
}

@Composable
fun ChatBubble(message: UserMessage) {
    val align = if (message.isMe) Alignment.End else Alignment.Start

    // LOGICA DE COLORES DE BURBUJA
    // Si soy yo: Naranja.
    // Si es el otro: Blanco (en tema claro) o Gris Oscuro (en tema oscuro).
    val bubbleColor = if (message.isMe) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant

    // LOGICA DE COLORES DE TEXTO
    // Si soy yo: Blanco.
    // Si es el otro: Negro (en tema claro) o Blanco (en tema oscuro).
    val textColor = if (message.isMe) Color.White else MaterialTheme.colorScheme.onSurface

    val shape = if (message.isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 2.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Surface(
            color = bubbleColor, // REEMPLAZADO: Color dinámico
            shape = shape,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = message.text,
                    color = textColor, // REEMPLAZADO: Color dinámico
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}