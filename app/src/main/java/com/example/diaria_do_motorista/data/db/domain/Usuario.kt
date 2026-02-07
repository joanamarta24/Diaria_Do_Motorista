package com.example.diaria_do_motorista.data.db.domain

data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val dataNascimento: String?,
    val tipo: TipoUsuario,
    val matriculaVeiculo: String? = null,
    val transportadoraId: String? = null,
    val ativo: Boolean = true
)
