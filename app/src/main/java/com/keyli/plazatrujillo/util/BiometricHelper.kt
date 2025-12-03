package com.keyli.plazatrujillo.util

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Helper para manejar autenticación biométrica (huella digital)
 * y almacenamiento seguro de credenciales.
 * 
 * Compatible con:
 * - Sensor de huella en pantalla
 * - Sensor de huella en botón de encendido (lateral)
 * - Sensor de huella trasero
 * - Reconocimiento facial (Face Unlock)
 */
class BiometricHelper(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "secure_credentials",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_EMAIL = "saved_email"
        private const val KEY_PASSWORD = "saved_password"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        
        // Usamos DEVICE_CREDENTIAL como fallback para máxima compatibilidad
        // Esto permite: huella, cara, iris, PIN, patrón, contraseña del dispositivo
        private val AUTHENTICATORS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ soporta combinación de biométrico + credencial de dispositivo
            BiometricManager.Authenticators.BIOMETRIC_WEAK or 
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        } else {
            // Android 10 y anteriores
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        }
        
        // Solo biométrico (sin PIN/patrón como fallback)
        private val BIOMETRIC_ONLY = BiometricManager.Authenticators.BIOMETRIC_WEAK
    }

    /**
     * Verifica si el dispositivo soporta biometría
     */
    fun canAuthenticate(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        
        // Primero intentamos con cualquier biométrico
        val resultWeak = biometricManager.canAuthenticate(BIOMETRIC_ONLY)
        
        return when (resultWeak) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SECURITY_UPDATE_REQUIRED
            else -> BiometricStatus.UNKNOWN_ERROR
        }
    }

    /**
     * Muestra el diálogo de autenticación biométrica
     * Compatible con todos los tipos de sensores biométricos
     */
    fun showBiometricPrompt(
        activity: FragmentActivity,
        title: String = "Autenticación Biométrica",
        subtitle: String = "Usa tu huella digital para iniciar sesión",
        negativeButtonText: String = "Cancelar",
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // No mostrar error si el usuario canceló
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && 
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                    errorCode != BiometricPrompt.ERROR_CANCELED) {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setConfirmationRequired(false) // No requiere confirmación extra después de la huella
        
        // En Android 11+ podemos usar credencial del dispositivo como fallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfoBuilder
                .setAllowedAuthenticators(AUTHENTICATORS)
            // Nota: No se puede usar setNegativeButtonText con DEVICE_CREDENTIAL
        } else {
            promptInfoBuilder
                .setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BIOMETRIC_ONLY)
        }

        val promptInfo = promptInfoBuilder.build()
        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * Guarda las credenciales de forma segura (encriptadas)
     */
    fun saveCredentials(email: String, password: String) {
        encryptedPrefs.edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_BIOMETRIC_ENABLED, true)
            .apply()
    }

    /**
     * Obtiene las credenciales guardadas
     */
    fun getCredentials(): Pair<String, String>? {
        val email = encryptedPrefs.getString(KEY_EMAIL, null)
        val password = encryptedPrefs.getString(KEY_PASSWORD, null)
        
        return if (!email.isNullOrEmpty() && !password.isNullOrEmpty()) {
            Pair(email, password)
        } else {
            null
        }
    }

    /**
     * Verifica si hay credenciales guardadas y biometría habilitada
     */
    fun hasStoredCredentials(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false) &&
               !encryptedPrefs.getString(KEY_EMAIL, null).isNullOrEmpty() &&
               !encryptedPrefs.getString(KEY_PASSWORD, null).isNullOrEmpty()
    }

    /**
     * Verifica si la biometría está habilitada
     */
    fun isBiometricEnabled(): Boolean {
        return encryptedPrefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    /**
     * Elimina las credenciales guardadas (logout)
     */
    fun clearCredentials() {
        encryptedPrefs.edit()
            .remove(KEY_EMAIL)
            .remove(KEY_PASSWORD)
            .putBoolean(KEY_BIOMETRIC_ENABLED, false)
            .apply()
    }

    /**
     * Obtiene solo el email guardado (para mostrar en UI)
     */
    fun getSavedEmail(): String? {
        return encryptedPrefs.getString(KEY_EMAIL, null)
    }
}

/**
 * Estados posibles de la biometría
 */
enum class BiometricStatus {
    AVAILABLE,           // Biometría disponible y lista
    NO_HARDWARE,         // Dispositivo sin hardware biométrico
    HARDWARE_UNAVAILABLE,// Hardware no disponible temporalmente
    NOT_ENROLLED,        // Sin huellas registradas
    SECURITY_UPDATE_REQUIRED, // Requiere actualización de seguridad
    UNKNOWN_ERROR        // Error desconocido
}
