package com.example.diaria_do_motorista.feature.login.states

import com.example.diaria_do_motorista.data.db.domain.Usuario

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val usuario: Usuario) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}