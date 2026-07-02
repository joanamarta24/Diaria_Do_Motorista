package com.example.diaria_do_motorista.data.db.remote.enums.biometric

data class BiometricAvailability(
    val isAvailable: Boolean,      // Pode usar biometria
    val isEnrolled: Boolean,       // Tem biometria cadastrada
    val hasHardware: Boolean,      // Dispositivo tem hardware
    val errorMessage: String? = null
)
