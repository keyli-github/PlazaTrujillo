package com.keyli.plazatrujillo.ui.screens

import androidx.navigation.NavHostController
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Contact(
    val id: Int,
    val name: String,
    val lastMessage: String,
    val role: String,
    val time: String,
    val online: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajeScreen(navController: NavHostController) {
    val sampleContacts = remember {
        mutableStateListOf(
            Contact(1, "María López", "Confirmado, gracias.", "Recepción", "Justo ahora", online = true),
            Contact(2, "Luis García", "Revisaré el aire en la habitación 301 y te aviso", "Mantenimiento", "10:30"),
            Contact(3, "Ana Torres", "Habitación 204 lista para entrega.", "Limpieza", "09:15", online = true),
            Contact(4, "Jorge P.", "Cierre enviado. Revisar reportes", "Caja", "Justo ahora", online = true),
            Contact(5, "Sofía R.", "En camino con el desayuno.", "Servicio", "08:40"),
            Contact(6, "Recepción General", "¿Necesitas algo más?", "Recepción", "Ayer")
        )
    }

    var query by remember { mutableStateOf("") }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }

    val filtered = remember(query, sampleContacts) {
        if (query.isBlank()) sampleContacts.toList()
        else sampleContacts.filter {
            it.name.contains(query, true) ||
                    it.role.contains(query, true) ||
                    it.lastMessage.contains(query, true)
        }
    }

    // Root: sin padding lateral para que el card principal llegue a los bordes
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(top = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // NOTA: eliminé el título grande que estaba arriba (lo controla el AppBar global)

            // CONTENEDOR PRINCIPAL: ocupa todo el ancho sin esquinas redondeadas visibles
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                shape = RoundedCornerShape(0.dp), // sin esquinas para que ocupe todo el ancho
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Encabezado interior: ahora se llama "Mensajes" (hiciste el intercambio)
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                        Text(
                            text = "Mensajes",
                            fontSize = 18.sp,
                            color = TextBlack,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Buscar contacto...", color = TextGray, fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    tint = TextGray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = LightBackground,
                                unfocusedContainerColor = LightBackground,
                                cursorColor = OrangePrimary,
                                focusedTextColor = TextBlack,
                                unfocusedTextColor = TextBlack
                            )
                        )
                    }

                    Divider(color = Color(0xFFECECEC), thickness = 1.dp)

                    // Lista de contactos con padding interior adecuado
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        itemsIndexed(filtered) { index, contact ->
                            ContactRow(contact = contact, onClick = { selectedContact = contact })
                            if (index < filtered.lastIndex) {
                                Divider(
                                    color = Color(0xFFF2F2F2),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(start = 100.dp)
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        // Conversación overlay: full screen, sin bordes laterales
        selectedContact?.let { contact ->
            ConversationOverlayFullWidth(
                contact = contact,
                onClose = { selectedContact = null }
            )
        }
    }
}

@Composable
private fun ContactRow(contact: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F2F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "avatar",
                tint = Color(0xFF9E9E9E),
                modifier = Modifier.size(30.dp)
            )

            if (contact.online) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(StatusGreen)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.name,
                    fontSize = 17.sp,
                    color = TextBlack,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = contact.time,
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contact.lastMessage,
                color = TextGray,
                fontSize = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = contact.role,
                color = OrangePrimary,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationOverlayFullWidth(contact: Contact, onClose: () -> Unit) {
    // Mensajes iniciales (simulados)
    val initialMessages = remember(contact.id) {
        mutableStateListOf(
            ChatMessage(0, "Hola ${contact.name.split(" ").first()}, ¿cómo puedo ayudar?", isUser = false),
            ChatMessage(1, contact.lastMessage, isUser = true)
        )
    }
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    suspend fun simulatedReply(text: String): String {
        delay(450)
        return when {
            "gracias" in text.lowercase() -> "Con gusto. ¿Algo más en lo que te ayude?"
            "aire" in text.lowercase() || "mantenimiento" in text.lowercase() -> "Agendado. Un técnico pasará en breve."
            else -> "Perfecto, lo tomo en cuenta."
        }
    }

    // Overlay que ocupa todo el ancho y alto (sin mostrar bordes laterales)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightSurface
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header compacto (alineado con AppBar) sin padding lateral extra
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .background(LightSurface)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onClose() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextBlack)
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F2F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "avatar",
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(contact.name, fontSize = 17.sp, color = TextBlack)
                    Text(contact.role, fontSize = 13.sp, color = TextGray)
                }
            }

            // Area de mensajes: ocupa todo el ancho sin bordes laterales y con padding interior cómodo
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(LightBackground)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(initialMessages) { _, msg ->
                        ConversationBubble(msg)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) } // espacio para el input
                }
            }

            // Input: barra en la parte inferior, pegada a los bordes laterales
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightSurface),
                color = LightSurface,
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { Text("Escribe un mensaje...", color = TextGray, fontSize = 15.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = OrangePrimary,
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            if (input.isBlank()) return@Button
                            val id = initialMessages.size
                            val text = input.trim()
                            initialMessages.add(ChatMessage(id, text, isUser = true))
                            input = ""
                            scope.launch {
                                listState.animateScrollToItem(initialMessages.lastIndex)
                                val reply = simulatedReply(text)
                                initialMessages.add(ChatMessage(initialMessages.size, reply, isUser = false))
                                delay(120)
                                listState.animateScrollToItem(initialMessages.lastIndex)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text("Enviar", color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationBubble(msg: ChatMessage) {
    // Burbuja con buen lineHeight y máximo ancho para evitar que el texto se vea aplastado
    val bubbleModifier = Modifier.fillMaxWidth(0.78f)
    val bgLeft = Color(0xFFF4F6F8)
    val bgRight = OrangePrimary
    val textColorLeft = TextBlack
    val textColorRight = Color.White

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!msg.isUser) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = bgLeft),
                modifier = bubbleModifier
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = msg.text,
                        color = textColorLeft,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ahora",
                        color = TextGray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = bgRight),
                modifier = bubbleModifier
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = msg.text,
                        color = textColorRight,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ahora",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}