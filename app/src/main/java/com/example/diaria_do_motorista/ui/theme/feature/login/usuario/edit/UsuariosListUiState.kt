package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

import com.example.diaria_do_motorista.data.db.domain.Usuario

data class UsuariosListUiState(
    val motoristas: List<Usuario> = emptyList(),
    val filteredMotoristas: List<Usuario> = emptyList(),
    val searQuery: String ="",
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedMotorista: Usuario? = null,
    val showDeleteConfirmation: Boolean = false,
    val successMessage: String? = null

)
