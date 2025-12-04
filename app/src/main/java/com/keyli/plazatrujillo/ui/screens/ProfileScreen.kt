package com.keyli.plazatrujillo.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.viewmodel.UserViewModel
import com.keyli.plazatrujillo.data.model.UpdateProfileRequest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Convierte una URI de imagen a Base64 (Data URL)
 */
fun uriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        // Redimensionar si es muy grande (máximo 800px)
        val maxSize = 800
        val scaledBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val ratio = minOf(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).toInt(),
                (bitmap.height * ratio).toInt(),
                true
            )
        } else bitmap
        
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val bytes = outputStream.toByteArray()
        
        "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Decodifica una URL Base64 (data:image/...) a Bitmap
 */
fun base64ToBitmap(base64Url: String): Bitmap? {
    return try {
        // Extraer solo la parte Base64 (quitar "data:image/jpeg;base64,")
        val base64Data = if (base64Url.contains(",")) {
            base64Url.substringAfter(",")
        } else {
            base64Url
        }
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // --- CONTEXTO ---
    val context = LocalContext.current
    
    // --- VIEWMODEL ---
    val userViewModel: UserViewModel = viewModel()
    val uiState by userViewModel.uiState.collectAsState()
    
    // Cargar perfil al iniciar
    LaunchedEffect(Unit) {
        userViewModel.loadOwnProfile()
    }
    
    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val iconColor = MaterialTheme.colorScheme.onSurface

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // --- DATOS DEL PERFIL desde API ---
    val profile = uiState.currentProfile
    var nombre by remember(profile) { mutableStateOf(profile?.displayName ?: "") }
    val email = profile?.email ?: "Cargando..."
    val rol = when (profile?.role?.lowercase()) {
        "admin" -> "Administrador"
        "receptionist" -> "Recepcionista"
        "housekeeping" -> "Hotelero"
        else -> profile?.role ?: "Cargando..."
    }
    val estado = "En línea"

    // Datos del Hotel (Fijos)
    val nombreHotel = "Hotel Plaza Trujillo"
    val ciudadRegion = "Trujillo, La Libertad, Perú"
    val direccion = "Jr. Bolognesi 344, Trujillo"
    val telefono = "+51 992 810 971"
    val paginaWeb = "www.plazatrujillo.com"

    // --- ESTADO FOTO DE PERFIL ---
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var deletePhoto by remember { mutableStateOf(false) }
    
    // URL de la foto actual del perfil (de la API)
    val currentPhotoUrl = profile?.profilePhotoUrl
    
    // Actualizar nombre cuando cambie el perfil
    LaunchedEffect(profile?.displayName) {
        profile?.displayName?.let { nombre = it }
    }
    
    // Mostrar mensajes de éxito/error
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            userViewModel.clearMessages()
            // Recargar perfil para obtener la URL actualizada de la foto
            userViewModel.loadOwnProfile()
        }
    }
    
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            userViewModel.clearMessages()
        }
    }

    // --- LAUNCHER ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
            coroutineScope.launch {
                snackbarHostState.showSnackbar("¡Foto seleccionada!", withDismissAction = true)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = surfaceColor,
                    titleContentColor = textColor,
                    navigationIconContentColor = iconColor
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(bgColor)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TARJETA 1: DATOS PERSONALES ---
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Avatar ---
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(OrangePrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            // Si se seleccionó una nueva imagen local
                            profileImageUri != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(profileImageUri),
                                    contentDescription = "Foto de perfil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            }
                            // Si hay foto actual en el perfil y no se marcó para eliminar
                            !currentPhotoUrl.isNullOrEmpty() && !deletePhoto -> {
                                // Verificar si es Base64 o URL normal
                                if (currentPhotoUrl.startsWith("data:")) {
                                    // Es Base64, decodificar manualmente
                                    val bitmap = remember(currentPhotoUrl) { 
                                        base64ToBitmap(currentPhotoUrl) 
                                    }
                                    if (bitmap != null) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Foto de perfil",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = OrangePrimary,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                } else {
                                    // Es URL normal, usar Coil
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(currentPhotoUrl)
                                            .diskCachePolicy(CachePolicy.DISABLED)
                                            .memoryCachePolicy(CachePolicy.DISABLED)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Foto de perfil",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                                    )
                                }
                            }
                            // Sin foto
                            else -> {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    // Botón para cambiar foto
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = {
                            imagePickerLauncher.launch("image/*")
                            deletePhoto = false
                        }) {
                            Text(
                                "Cambiar Foto",
                                color = OrangePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Mostrar botón quitar solo si hay foto
                        if (profileImageUri != null || (!currentPhotoUrl.isNullOrEmpty() && !deletePhoto)) {
                            TextButton(onClick = {
                                profileImageUri = null
                                deletePhoto = true
                                coroutineScope.launch { snackbarHostState.showSnackbar("Foto marcada para eliminar.") }
                            }) {
                                Text(
                                    "Quitar Foto",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text("Mi Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(20.dp))

                    // --- CAMPOS ---

                    // NOMBRE: isEditable = true
                    ProfileInfoField(
                        label = "Nombre Completo",
                        value = nombre,
                        icon = Icons.Default.Person,
                        isEditable = true,
                        onValueChange = { nombre = it }
                    )

                    // EL RESTO: isEditable = false
                    ProfileInfoField("Correo Electrónico", email, Icons.Default.Email, isEditable = false)
                    ProfileInfoField("Rol", rol, Icons.Default.VerifiedUser, isEditable = false)
                    ProfileInfoField("Estado", estado, Icons.Default.OnlinePrediction, isEditable = false)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TARJETA 2: INFORMACIÓN DEL HOTEL ---
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Información del Hotel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileInfoField("Nombre", nombreHotel, Icons.Default.Business, isEditable = false)
                    ProfileInfoField("Ciudad / Región", ciudadRegion, Icons.Default.LocationOn, isEditable = false)
                    ProfileInfoField("Dirección", direccion, Icons.Default.LocationOn, isEditable = false)
                    ProfileInfoField("Teléfono", telefono, Icons.Default.Call, isEditable = false)
                    ProfileInfoField("Página Web", paginaWeb, Icons.Default.Language, isEditable = false)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = {
                    // Determinar qué enviar para la foto
                    val photoToSend: String? = when {
                        deletePhoto -> "" // Enviar cadena vacía para eliminar
                        profileImageUri != null -> uriToBase64(context, profileImageUri!!) // Nueva foto en Base64
                        else -> null // No cambiar la foto
                    }
                    
                    val request = UpdateProfileRequest(
                        displayName = nombre,
                        profilePhotoUrl = photoToSend
                    )
                    userViewModel.updateOwnProfile(request)
                    
                    // Resetear estados locales después de guardar
                    profileImageUri = null
                    deletePhoto = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Componente reutilizable para campos de perfil.
 */
@Composable
fun ProfileInfoField(
    label: String,
    value: String,
    icon: ImageVector,
    isEditable: Boolean,
    onValueChange: (String) -> Unit = {}
) {
    // Fondo del input: un poco más transparente si es solo lectura, pero sutil.
    val baseInputColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val finalInputColor = if (isEditable) baseInputColor else baseInputColor.copy(alpha = 0.15f)

    val textColor = MaterialTheme.colorScheme.onSurface
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    // IMPORTANTE: El ícono SIEMPRE será Naranja (OrangePrimary) para que se vea bien
    val iconTint = OrangePrimary

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        // Label y Candado
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = labelColor,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
            // Icono de candado pequeño si es solo lectura
            if (!isEditable) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Solo lectura",
                    tint = labelColor.copy(alpha = 0.4f), // Candado sutil
                    modifier = Modifier.size(12.dp).padding(end = 4.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(finalInputColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Icono Principal (Siempre visible y naranja)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            BasicTextField(
                value = value,
                onValueChange = { if (isEditable) onValueChange(it) },
                readOnly = !isEditable,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = textColor, // Texto siempre visible (blanco/negro según tema)
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}