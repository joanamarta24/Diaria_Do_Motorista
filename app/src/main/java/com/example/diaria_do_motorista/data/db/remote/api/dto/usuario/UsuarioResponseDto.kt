package com.example.diaria_do_motorista.data.db.remote.api.dto.usuario

import java.time.LocalDate

data class UsuarioResponseDto (
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val dataNascimento: LocalDate?,
    val tipo: String,
    val tipoDescricao: String,
    val matriculaVeiculo: String?,
    val transportadoraId: String?,
    val transportadoraNome: String?,
    val ativo: Boolean,
    val dataCriacao: String,
    val dataAtualizacao: String?
)