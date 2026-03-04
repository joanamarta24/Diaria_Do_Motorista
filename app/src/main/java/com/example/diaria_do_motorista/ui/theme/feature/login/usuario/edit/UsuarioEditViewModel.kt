package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

import androidx.lifecycle.SavedStateHandle
import com.example.diaria_do_motorista.data.db.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class UsuarioEditViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val savedStateHandle: SavedStateHandle
)
