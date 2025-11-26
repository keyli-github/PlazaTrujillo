package com.keyli.plazatrujillo.ui.screens

import androidx.navigation.NavHostController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.keyli.plazatrujillo.ui.theme.*

data class ChatMessage(val id: Int, val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember { mutableStateListOf<ChatMessage>() }

    // Mensaje inicial del asistente
    LaunchedEffect(Unit) {
        messages.add(
            ChatMessage(
                id = 0,
                text = "¡Hola! Soy el Asistente IA de Plaza Trujillo. 🏨\n\nPuedo responder dudas sobre ingresos, reservas y servicios. ¿En qué puedo ayudarte?",
                isUser = false
            )
        )
        delay(120)
        listState.animateScrollToItem(messages.lastIndex)
    }

    var input by remember { mutableStateOf("") }

    val quickActions = listOf("💰 Ingresos hoy", "📈 Ocupación", "🗓️ Reservas", "☎️ Servicios")

    // Respuesta simulada sencilla
    suspend fun getBotReply(userText: String): String {
        val text = userText.lowercase()
        return when {
            "ingres" in text || "ingresos" in text -> "Hoy hemos registrado S/ 3,450 de ingresos. ¿Deseas ver el detalle por habitación?"
            "ocup" in text || "ocupación" in text -> "La ocupación actual es 78%. Hay 12 habitaciones disponibles."
            "reserv" in text || "reserva" in text -> "Actualmente tienes 5 reservas nuevas para las próximas 24 horas."
            "servicio" in text || "servicios" in text -> "Ofrecemos lavandería, desayuno buffet y transporte al aeropuerto. ¿Cuál te interesa?"
            else -> listOf(
                "Entendido. ¿Quieres que busque más información?",
                "Puedo ayudarte con ingresos, reservas y servicios. ¿Cuál eliges?",
                "Lo siento, no entendí bien. ¿Puedes reformularlo?"
            ).random()
        }
    }

    // --- Diseño principal que coincide con la screenshot ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Cabecera interna del asistente (bloque gris claro con icono circular)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            colors = CardDefaults.cardColors(containerColor = LightSurface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Icono circular naranja
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    // icono blanco dentro del círculo
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Asistente",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Plaza Trujillo AI",
                        color = TextBlack,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Asistente Virtual Hotelero",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Card grande blanca con bordes redondeados que contiene los mensajes
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    itemsIndexed(messages) { _, msg ->
                        MessageBubble(msg)
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    // espacio al final para que el input no tape mensajes
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick action chips: diseño tipo "pill" con borde y fondo blanco
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 10.dp)
        ) {
            quickActions.forEachIndexed { _, action ->
                Surface(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = LightSurface,
                    border = BorderStroke(1.dp, Color(0xFFE6E6E6)),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable {
                                // enviar mensaje simulado al click
                                val id = messages.size
                                messages.add(ChatMessage(id, action, isUser = true))
                                scope.launch {
                                    listState.animateScrollToItem(messages.lastIndex)
                                    delay(220)
                                    val reply = getBotReply(action)
                                    messages.add(ChatMessage(messages.size, reply, isUser = false))
                                    delay(60)
                                    listState.animateScrollToItem(messages.lastIndex)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(action, color = TextBlack, fontSize = 14.sp)
                    }
                }
            }
        }

        // Barra inferior (input + botón) con fondo claro y esquinas superiores redondeadas
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            color = LightSurface,
            shape = RoundedCornerShape(18.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Escribe aquí...", color = TextGray) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = OrangePrimary,
                        focusedTextColor = TextBlack,
                        unfocusedTextColor = TextBlack
                    ),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Botón enviar circular naranja
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary)
                        .clickable {
                            if (input.isBlank()) return@clickable
                            val userText = input.trim()
                            val id = messages.size
                            messages.add(ChatMessage(id, userText, isUser = true))
                            input = ""
                            scope.launch {
                                listState.animateScrollToItem(messages.lastIndex)
                                delay(450)
                                val reply = getBotReply(userText)
                                messages.add(ChatMessage(messages.size, reply, isUser = false))
                                delay(80)
                                listState.animateScrollToItem(messages.lastIndex)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Enviar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage) {
    // Estilo parecido a la imagen: burbuja del asistente en gris muy claro con esquinas redondeadas grandes
    val bubbleBg = if (msg.isUser) OrangePrimary else Color(0xFFF0F4F8)
    val textColor = if (msg.isUser) Color.White else TextBlack

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!msg.isUser) {
            // pequeño icono del asistente a la izquierda (fuera de la burbuja)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Asistente",
                    tint = OrangePrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Burbuja de mensaje
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = bubbleBg),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = msg.text,
                color = textColor,
                modifier = Modifier.padding(14.dp),
                fontSize = 15.sp
            )
        }

        if (msg.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(OrangeSecondary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Usuario",
                    tint = OrangeSecondary
                )
            }
        }
    }
}