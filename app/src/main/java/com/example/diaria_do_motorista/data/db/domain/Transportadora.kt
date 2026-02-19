package com.example.diaria_do_motorista.data.db.domain

import java.time.LocalDateTime

data class Transportadora(
    val id: String,
    val nome: String,
    val contatoResponsavel: String,
    val dataAtualizada: LocalDateTime,
    val dataCriacao: LocalDateTime,
    val telefone: String,
    val email: String? = null,
    val ativo: Boolean = true
)
