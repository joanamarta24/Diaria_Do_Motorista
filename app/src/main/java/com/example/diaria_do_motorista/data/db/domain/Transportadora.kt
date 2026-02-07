package com.example.diaria_do_motorista.data.db.domain

data class Transportadora(
    val id: String,
    val nome: String,
    val contatoResponsavel: String,
    val telefone: String,
    val email: String? = null,
    val ativo: Boolean = true
)
