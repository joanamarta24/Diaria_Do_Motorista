package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.diaria_do_motorista.data.db.repository.UsuarioRepository
import com.example.diaria_do_motorista.ui.theme.feature.login.usuario.list.UsuariosListViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UsuariosListViewModel  @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private  val _uiState = MutableStateFlow(UsuarioEditUiState())
    val uiState: StateFlow<UsuarioEditUiState> = _uiState.asStateFlow()

    private val usuarioId: String? = savedStateHandle["UsuarioId"]

    init {
        if (usuarioId != null && usuarioId != "new") {
            _uiState.update { it.copy(isEditMode = true) }
            loadUsuario(usuarioId)
        }
        loadTransportadoras()
    }



}
