package com.example.diaria_do_motorista.data.db.remote.api.dto.usuario

import java.time.LocalDate

data class UsuarioUpdateDto(
    val nome: String? = null,
    val email:String? = null,
    val telefone:String? = null,
    val dataNascimento: LocalDate?,

    val matriculaVeiculo: String? = null,
    val transportadoraId: String? = null,
    val ativo: Boolean? = null
)
