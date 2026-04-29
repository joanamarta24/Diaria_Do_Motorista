package com.example.diaria_do_motorista.ui.theme.feature.login.login

import com.example.diaria_do_motorista.ui.theme.feature.login.loginsealed.LoginUiState

data class LoginScreenState(
    val formState: LoginFormState = LoginFormState(),
    val uiState: LoginUiState = LoginUiState.Idle,
    val rememberMe: Boolean = false,
    val showPassword: Boolean = false,
    val isOfflineMode: Boolean = false
)
