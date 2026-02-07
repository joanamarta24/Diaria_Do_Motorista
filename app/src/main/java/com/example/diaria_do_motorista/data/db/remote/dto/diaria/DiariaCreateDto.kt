package com.example.diaria_do_motorista.data.db.remote.dto.diaria

import java.time.LocalDate
import java.time.LocalTime

data class DiariaCreateDto(
    val motoristaId: String,
    val matriculaVeiculo:String,
    val transportadoraId:String,
    val dataDiaria:LocalDate,
    val destino:String,
    val horaInicio:LocalTime,
    val horaFim:LocalTime? = null,
    val totalPortagens:Double? = null,
    val abastecimento:Boolean = false,
    val abastecimentoAdBlue:Boolean = false,
    val observacoes:String? = null
)
