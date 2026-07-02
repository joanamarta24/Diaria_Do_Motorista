package com.example.diaria_do_motorista.util.biometric

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import com.example.diaria_do_motorista.data.db.remote.enums.biometric.BiometricAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Verifica se a biometria está disponível e configurada
     */
    suspend fun isBiometricAvailable(): BiometricAvailability {
        return withContext(Dispatchers.Main) {
            try {
                val biometricManager = BiometricManager.from(context)

                // Verifica se o hardware de biometria existe
                val canAuthenticate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG)
                } else {
                    biometricManager.canAuthenticate()
                }

                when (canAuthenticate) {
                    BiometricManager.BIOMETRIC_SUCCESS -> {
                        // Hardware disponível e usuário tem biometria cadastrada
                        BiometricAvailability(
                            isAvailable = true,
                            isEnrolled = true,
                            hasHardware = true,
                            errorMessage = null
                        )
                    }
                    BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                        BiometricAvailability(
                            isAvailable = false,
                            isEnrolled = false,
                            hasHardware = false,
                            errorMessage = "Dispositivo não suporta biometria"
                        )
                    }
                    BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                        BiometricAvailability(
                            isAvailable = false,
                            isEnrolled = false,
                            hasHardware = true,
                            errorMessage = "Hardware de biometria indisponível no momento"
                        )
                    }
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        BiometricAvailability(
                            isAvailable = false,
                            isEnrolled = false,
                            hasHardware = true,
                            errorMessage = "Nenhuma biometria cadastrada no dispositivo"
                        )
                    }
                    else -> {
                        BiometricAvailability(
                            isAvailable = false,
                            isEnrolled = false,
                            hasHardware = false,
                            errorMessage = "Erro desconhecido ao verificar biometria"
                        )
                    }
                }
            } catch (e: Exception) {
                // Em caso de erro, retorna falso com mensagem
                BiometricAvailability(
                    isAvailable = false,
                    isEnrolled = false,
                    hasHardware = false,
                    errorMessage = e.message ?: "Erro ao verificar biometria"
                )
            }
        }
    }

    /**
     * Verifica apenas se o dispositivo possui hardware de biometria
     */
    suspend fun hasBiometricHardware(): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                val biometricManager = BiometricManager.from(context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG) !=
                            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
                } else {
                    biometricManager.canAuthenticate() !=
                            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Verifica se o usuário tem biometria cadastrada
     */
    suspend fun isBiometricEnrolled(): Boolean {
        return withContext(Dispatchers.Main) {
            try {
                val biometricManager = BiometricManager.from(context)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG) ==
                            BiometricManager.BIOMETRIC_SUCCESS
                } else {
                    biometricManager.canAuthenticate() ==
                            BiometricManager.BIOMETRIC_SUCCESS
                }
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Verifica se o Android suporta a versão mínima para biometria
     */
    fun isBiometricSupportedByOS(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M // Android 6.0+
    }

}

