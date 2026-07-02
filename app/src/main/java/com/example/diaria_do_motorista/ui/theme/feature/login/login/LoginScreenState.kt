package com.example.diaria_do_motorista.feature.login.states

data class LoginScreenState(
    val formState: LoginFormState = LoginFormState(),
    val uiState: LoginUiState = LoginUiState.Idle,
    val rememberMe: Boolean = false,
    val showPassword: Boolean = false,
    val isOfflineMode: Boolean = false,
    val isLoading: Boolean = false,

    // Campos de biometria
    val biometricAvailable: Boolean = false,
    val biometricIsEnrolled: Boolean = false,
    val biometricHasHardware: Boolean = false,
    val biometricErrorMessage: String? = null,

    // Campos adicionais
    val shouldShowBiometricPrompt: Boolean = false
)