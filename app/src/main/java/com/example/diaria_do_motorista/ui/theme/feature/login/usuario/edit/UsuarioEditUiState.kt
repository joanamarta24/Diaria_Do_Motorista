package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.domain.Veiculo

data class UsuarioEditUiState(
    val id: String ="",
    val nome: String = "",
    val email: String = "",
    val telefone: String ="",
    val dataNascimento: String = "",
    val matriculaVeiculo: String ="",
    val transportadoraId: String = "",
    val senha: String ="",
    val confirmaSenha: String ="",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error:String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false,
    val transportadoras: List<Transportadora> = emptyList(),
    val veiculos: List<Veiculo> = emptyList(),
    val nomeError: String? = null,
    val emailError: String? = null,
    val telefoneError: String? = null,
    val senhaError: String? = null
)