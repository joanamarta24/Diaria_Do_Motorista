package com.example.diaria_do_motorista.data.db.remote.dto.usuario

import java.time.LocalDate

data class UsuarioDto(
    val nome:String,
    val email:String,
    val telefone:String,
    val senha:String,
    val dataNascimento:LocalDate? = null,
    val tipo:String = "MOTORISTA",
    val matriculaVeiculo:String? = null,
    val transportadoraId:String? = null,
)
