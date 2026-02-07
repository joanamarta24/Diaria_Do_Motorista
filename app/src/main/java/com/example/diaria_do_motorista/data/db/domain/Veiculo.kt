package com.example.diaria_do_motorista.data.db.domain

data class Veiculo(
    val id: String,
    val matricula: String,
    val marca: String,
    val modelo: String,
    val transportadoraId: String,
    val ativo: Boolean = true
)
