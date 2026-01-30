package com.example.diaria_do_motorista.data.db.remote.dto.diaria

import java.time.LocalDate
import java.time.LocalTime

data class DiariaResumoDto(
    val id:Long,
    val dataDiaria:LocalDate,
    val destino:String,
    val motoristaNome:String,
    val matriculaVeiculo:String,
    val horaInicio:LocalTime,
    val horaFim:String,
    val status:String,
    val kmRodados:Double?
)
