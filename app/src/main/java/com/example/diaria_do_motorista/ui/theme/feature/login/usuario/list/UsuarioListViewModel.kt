package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.list

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaria_do_motorista.data.db.repository.UsuarioRepository
import com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit.UsuarioEditEvent
import com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit.UsuarioEditUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UsuarioEditViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsuarioEditUiState())
    val uiState: StateFlow<UsuarioEditUiState> = _uiState.asStateFlow()

    private val usuarioId: String? = savedStateHandle["usuarioId"]

    init {
        if (usuarioId != null && usuarioId != "new") {
            _uiState.update { it.copy(isEditMode = true) }
            loadUsuario(usuarioId)
        }
        loadTransportadoras()
    }

    fun handleEvent(event: UsuarioEditEvent) {
        when (event) {
            is UsuarioEditEvent.OnNomeChange -> updateNome(event.nome)
            is UsuarioEditEvent.OnEmailChange -> updateEmail(event.email)
            is UsuarioEditEvent.OnTelefoneChange -> updateTelefone(event.telefone)
            is UsuarioEditEvent.OnDataNascimentoChange -> updateDataNascimento(event.dataNascimento)
            is UsuarioEditEvent.OnTransportadoraSelected -> selectTransportadora(event.transportadoraId)
            is UsuarioEditEvent.OnVeiculoSelected -> selectVeiculo(event.matricula)
            is UsuarioEditEvent.OnSenhaChange -> updateSenha(event.senha)
            is UsuarioEditEvent.OnConfirmarSenhaChange -> updateConfirmarSenha(event.confirmarSenha)
            UsuarioEditEvent.OnSave -> saveUsuario()
            UsuarioEditEvent.OnLoadData -> loadTransportadoras()
            UsuarioEditEvent.ClearMessages -> clearMessages()
        }
    }

    private fun updateNome(nome: String) {
        _uiState.update { it.copy(nome = nome) }
        validateNome()
    }

    private fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
        validateEmail()
    }

    private fun updateTelefone(telefone: String) {
        _uiState.update { it.copy(telefone = telefone) }
        validateTelefone()
    }

    private fun updateDataNascimento(dataNascimento: String) {
        _uiState.update { it.copy(dataNascimento = dataNascimento) }
    }

    private fun updateSenha(senha: String) {
        _uiState.update { it.copy(senha = senha) }
        validateSenha()
    }

    private fun updateConfirmarSenha(confirmarSenha: String) {
        _uiState.update { it.copy(confirmarSenha = confirmarSenha) }
        validateConfirmarSenha()
    }

    private fun selectTransportadora(transportadoraId: String) {
        _uiState.update { it.copy(transportadoraId = transportadoraId) }
        loadVeiculos(transportadoraId)
    }

    private fun selectVeiculo(matricula: String) {
        _uiState.update { it.copy(matriculaVeiculo = matricula) }
    }

    private fun validateNome() {
        val nome = uiState.value.nome
        val error = when {
            nome.isBlank() -> "Nome é obrigatório"
            nome.length < 3 -> "Nome deve ter pelo menos 3 caracteres"
            else -> null
        }
        _uiState.update { it.copy(nomeError = error) }
    }

    private fun validateEmail() {
        val email = uiState.value.email
        val error = when {
            email.isBlank() -> "E-mail é obrigatório"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-mail inválido"
            else -> null
        }
        _uiState.update { it.copy(emailError = error) }
    }

    private fun validateTelefone() {
        val telefone = uiState.value.telefone
        val error = when {
            telefone.isBlank() -> "Telefone é obrigatório"
            telefone.replace("[^0-9]".toRegex(), "").length < 10 -> "Telefone inválido"
            else -> null
        }
        _uiState.update { it.copy(telefoneError = error) }
    }

    private fun validateSenha() {
        val senha = uiState.value.senha
        val error = when {
            !uiState.value.isEditMode && senha.isBlank() -> "Senha é obrigatória"
            senha.isNotBlank() && senha.length < 6 -> "Senha deve ter pelo menos 6 caracteres"
            else -> null
        }
        _uiState.update { it.copy(senhaError = error) }
    }

    private fun validateConfirmarSenha() {
        val senha = uiState.value.senha
        val confirmarSenha = uiState.value.confirmarSenha
        val error = when {
            senha != confirmarSenha -> "As senhas não coincidem"
            else -> null
        }
        _uiState.update { it.copy(senhaError = error) }
    }

    private fun isValid(): Boolean {
        validateNome()
        validateEmail()
        validateTelefone()
        validateSenha()
        validateConfirmarSenha()

        val state = uiState.value
        return state.nomeError == null &&
                state.emailError == null &&
                state.telefoneError == null &&
                state.senhaError == null &&
                state.nome.isNotBlank() &&
                state.email.isNotBlank() &&
                state.telefone.isNotBlank() &&
                (state.isEditMode || (state.senha.isNotBlank() && state.confirmarSenha.isNotBlank()))
    }

    private fun loadUsuario(id: String) {
        viewModelScope.launch {
            // 1. Inicia o carregamento
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val result = usuarioRepository.getUsuarioById(id)

                result.fold(
                    onSuccess = { usuario ->
                        if (usuario != null) {
                            // 2. Atualiza tudo de uma vez
                            _uiState.update {
                                it.copy(
                                    id = usuario.id,
                                    nome = usuario.nome,
                                    email = usuario.email,
                                    telefone = usuario.telefone,
                                    dataNascimento = usuario.dataNascimento ?: "",
                                    matriculaVeiculo = usuario.matriculaVeiculo ?: "",
                                    transportadoraId = usuario.transportadoraId ?: "",
                                    isLoading = false
                                )
                            }
                            // 3. Carrega dependências externas
                            usuario.transportadoraId?.let { loadVeiculos(it) }
                        } else {
                            // Caso o sucesso retorne um corpo vazio/null
                            _uiState.update { it.copy(isLoading = false, error = "Usuário não encontrado") }
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                error = "Erro ao carregar motorista: ${exception.message}",
                                isLoading = false
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                // Tratamento para erros inesperados (ex: falta de internet sem tratamento no repo)
                _uiState.update { it.copy(isLoading = false, error = "Erro inesperado: ${e.message}") }
            }
        }
    }

    private fun loadTransportadoras() {
        viewModelScope.launch {
            val result = usuarioRepository.getTransportadoras()
            if (result.isSuccess) {
                _uiState.update { it.copy(transportadoras = result.getOrNull() ?: emptyList()) }
            }
        }
    }

    private fun loadVeiculos(transportadoraId: String) {
        viewModelScope.launch {
            val result = usuarioRepository.getVeiculosPorTransportadora(transportadoraId)
            if (result.isSuccess) {
                _uiState.update { it.copy(veiculos = result.getOrNull() ?: emptyList()) }
            }
        }
    }

    private fun saveUsuario() {
        if (!isValid()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = if (uiState.value.isEditMode) {
                usuarioRepository.atualizarMotorista(
                    id = uiState.value.id,
                    nome = uiState.value.nome,
                    email = uiState.value.email,
                    telefone = uiState.value.telefone,
                    dataNascimento = uiState.value.dataNascimento,
                    matriculaVeiculo = uiState.value.matriculaVeiculo.ifBlank { null },
                    transportadoraId = uiState.value.transportadoraId.ifBlank { null },
                    senha = uiState.value.senha.ifBlank { null }
                )
            } else {
                usuarioRepository.cadastrarMotorista(
                    nome = uiState.value.nome,
                    email = uiState.value.email,
                    telefone = uiState.value.telefone,
                    dataNascimento = uiState.value.dataNascimento,
                    matriculaVeiculo = uiState.value.matriculaVeiculo.ifBlank { null },
                    transportadoraId = uiState.value.transportadoraId.ifBlank { null },
                    senha = uiState.value.senha
                )
            }

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = if (uiState.value.isEditMode)
                            "Motorista atualizado com sucesso"
                        else
                            "Motorista cadastrado com sucesso"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Erro ao salvar: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    private fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}