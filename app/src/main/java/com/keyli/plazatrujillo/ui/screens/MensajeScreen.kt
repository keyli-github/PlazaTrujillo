package com.keyli.plazatrujillo.ui.screens

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.Contact
import com.keyli.plazatrujillo.ui.viewmodel.DisplayMessage
import com.keyli.plazatrujillo.ui.viewmodel.MessagingViewModel
import com.keyli.plazatrujillo.ui.viewmodel.AttachmentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajeScreen(
    navController: NavHostController,
    messagingViewModel: MessagingViewModel = viewModel()
) {
    val uiState by messagingViewModel.uiState.collectAsState()
    
    // --- COLORES DINÁMICOS ---
    val containerBg = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    var query by remember { mutableStateOf("") }

    // Filtro de búsqueda
    val filteredContacts = remember(query, uiState.contacts) {
        if (query.isBlank()) uiState.contacts
        else uiState.contacts.filter {
            it.name.contains(query, true) ||
                    it.role.contains(query, true)
        }
    }
    
    // Mostrar error si existe
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            delay(3000)
            messagingViewModel.clearError()
        }
    }

    // --- VISTA PRINCIPAL (LISTA DE CHATS) ---
    if (uiState.selectedContact == null) {
        Scaffold(
            containerColor = containerBg,
            topBar = {
                Surface(color = surfaceColor, shadowElevation = 2.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Text(
                            text = "Mensajes",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
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
                                focusedContainerColor = containerBg,
                                unfocusedContainerColor = containerBg,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            ),
                            singleLine = true
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(surfaceColor)
            ) {
                when {
                    uiState.isLoadingContacts -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = OrangePrimary
                        )
                    }
                    filteredContacts.isEmpty() && !uiState.isLoadingContacts -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (query.isNotBlank()) "No se encontraron contactos" else "No hay usuarios disponibles",
                                color = subTextColor,
                                fontSize = 16.sp
                            )
                            if (query.isBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { messagingViewModel.loadContacts() }) {
                                    Text("Reintentar", color = OrangePrimary)
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(filteredContacts) { index, contact ->
                                ContactItem(
                                    contact = contact,
                                    onClick = { messagingViewModel.selectContact(contact) },
                                    textColor = textColor,
                                    subTextColor = subTextColor,
                                    bgColor = containerBg,
                                    surfaceColor = surfaceColor
                                )
                                if (index < filteredContacts.lastIndex) {
                                    HorizontalDivider(
                                        color = subTextColor.copy(alpha = 0.1f),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(start = 80.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        // --- VISTA DE CONVERSACIÓN ---
        ConversationScreen(
            contact = uiState.selectedContact!!,
            messages = uiState.messages,
            isLoading = uiState.isLoadingMessages,
            isSending = uiState.isSendingMessage,
            attachment = uiState.attachment,
            onBack = { messagingViewModel.clearSelectedContact() },
            onSendMessage = { text -> messagingViewModel.sendMessage(text) },
            onSetAttachment = { uri, name, size, type, base64 ->
                messagingViewModel.setAttachment(uri, name, size, type, base64)
            },
            onClearAttachment = { messagingViewModel.clearAttachment() }
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
            if (!contact.photo.isNullOrEmpty()) {
                AsyncImage(
                    model = contact.photo,
                    contentDescription = contact.name,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = bgColor,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = getInitials(contact.name),
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary,
                            fontSize = 20.sp
                        )
                    }
                }
            }
            if (contact.online) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(StatusGreen, CircleShape)
                        .align(Alignment.BottomEnd)
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
                    color = textColor
                )
                contact.lastMessageTime?.let { time ->
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = if (contact.unreadCount > 0) OrangePrimary else subTextColor,
                        fontWeight = if (contact.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = contact.lastMessage ?: "Sin mensajes",
                    fontSize = 14.sp,
                    color = subTextColor,
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
fun ConversationScreen(
    contact: Contact,
    messages: List<DisplayMessage>,
    isLoading: Boolean,
    isSending: Boolean,
    attachment: AttachmentState?,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSetAttachment: (Uri, String, Long, String, String) -> Unit,
    onClearAttachment: () -> Unit
) {
    val context = LocalContext.current
    val containerBg = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showAttachmentOptions by remember { mutableStateOf(false) }
    var showImagePreview by remember { mutableStateOf<String?>(null) }
    
    // Launcher para seleccionar imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                
                if (bytes != null) {
                    if (bytes.size > 10 * 1024 * 1024) {
                        // Archivo muy grande
                        return@let
                    }
                    
                    val base64 = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                    val fileName = selectedUri.lastPathSegment ?: "imagen.jpg"
                    
                    onSetAttachment(selectedUri, fileName, bytes.size.toLong(), "image", base64)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Launcher para seleccionar archivos
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                
                if (bytes != null) {
                    if (bytes.size > 10 * 1024 * 1024) {
                        return@let
                    }
                    
                    val mimeType = context.contentResolver.getType(selectedUri) ?: "application/octet-stream"
                    val base64 = "data:$mimeType;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                    val fileName = selectedUri.lastPathSegment ?: "archivo"
                    
                    onSetAttachment(selectedUri, fileName, bytes.size.toLong(), "file", base64)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Auto-scroll cuando hay nuevos mensajes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    
    // Dialog para ver imagen ampliada
    showImagePreview?.let { imageUrl ->
        Dialog(onDismissRequest = { showImagePreview = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showImagePreview = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen ampliada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    Scaffold(
        containerColor = containerBg,
        modifier = Modifier.imePadding(),
        topBar = {
            Surface(shadowElevation = 4.dp, color = surfaceColor) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = textColor)
                    }

                    // Avatar
                    if (!contact.photo.isNullOrEmpty()) {
                        AsyncImage(
                            model = contact.photo,
                            contentDescription = contact.name,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(containerBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contact.name.take(1),
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                        Text(
                            text = contact.role,
                            fontSize = 12.sp,
                            color = subTextColor
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = surfaceColor,
                shadowElevation = 8.dp
            ) {
                Column {
                    // Preview del archivo adjunto
                    attachment?.let { attach ->
                        Surface(
                            color = containerBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (attach.type == "image" && attach.uri != null) {
                                    AsyncImage(
                                        model = attach.uri,
                                        contentDescription = "Preview",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = attach.name ?: "Archivo",
                                        fontWeight = FontWeight.Medium,
                                        color = textColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatFileSize(attach.size),
                                        fontSize = 12.sp,
                                        color = subTextColor
                                    )
                                }
                                IconButton(onClick = onClearAttachment) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón de adjuntar
                        IconButton(
                            onClick = { showAttachmentOptions = true },
                            enabled = !isSending && attachment == null
                        ) {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Adjuntar",
                                tint = if (attachment == null) OrangePrimary else subTextColor
                            )
                        }
                        
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
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            ),
                            singleLine = true,
                            enabled = !isSending
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = {
                                if ((inputText.isNotBlank() || attachment != null) && !isSending) {
                                    onSendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            containerColor = if (isSending) OrangePrimary.copy(alpha = 0.5f) else OrangePrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp),
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            if (isSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "Enviar", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
            
            // DropdownMenu para opciones de adjuntar
            DropdownMenu(
                expanded = showAttachmentOptions,
                onDismissRequest = { showAttachmentOptions = false }
            ) {
                DropdownMenuItem(
                    text = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = OrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Imagen")
                        }
                    },
                    onClick = {
                        showAttachmentOptions = false
                        imagePickerLauncher.launch("image/*")
                    }
                )
                DropdownMenuItem(
                    text = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = OrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Archivo")
                        }
                    },
                    onClick = {
                        showAttachmentOptions = false
                        filePickerLauncher.launch("*/*")
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OrangePrimary
                    )
                }
                messages.isEmpty() -> {
                    Text(
                        text = "No hay mensajes aún.\n¡Envía el primero!",
                        color = subTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(messages) { _, msg ->
                            MessageBubble(
                                message = msg,
                                onImageClick = { imageUrl -> showImagePreview = imageUrl }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: DisplayMessage, onImageClick: ((String) -> Unit)? = null) {
    val align = if (message.isMe) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isMe) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isMe) Color.White else MaterialTheme.colorScheme.onSurface

    val shape = if (message.isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 2.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Surface(
            color = bubbleColor,
            shape = shape,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Mostrar imagen si es tipo imagen
                if (message.messageType == "image" && !message.attachment.isNullOrEmpty()) {
                    AsyncImage(
                        model = message.attachment,
                        contentDescription = "Imagen",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onImageClick?.invoke(message.attachment) },
                        contentScale = ContentScale.Fit
                    )
                    if (message.text.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Mostrar archivo si es tipo file
                if (message.messageType == "file" && !message.attachmentName.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (message.isMe) Color.White.copy(alpha = 0.2f) 
                                else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = if (message.isMe) Color.White else OrangePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = message.attachmentName,
                                color = textColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            message.attachmentSize?.let { size ->
                                Text(
                                    text = formatFileSize(size),
                                    color = textColor.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    if (message.text.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Mostrar texto si existe
                if (message.text.isNotBlank()) {
                    Text(
                        text = message.text,
                        color = textColor,
                        fontSize = 15.sp
                    )
                }
                
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

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}

private fun getInitials(name: String): String {
    val parts = name.split(" ")
    return when {
        parts.size >= 2 -> "${parts[0].firstOrNull() ?: ""}${parts[1].firstOrNull() ?: ""}".uppercase()
        name.isNotEmpty() -> name.take(2).uppercase()
        else -> "U"
    }
}