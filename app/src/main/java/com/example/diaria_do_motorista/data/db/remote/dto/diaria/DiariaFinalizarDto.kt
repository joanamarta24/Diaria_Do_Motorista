package com.example.diaria_do_motorista.data.db.remote.dto.diaria

import java.time.LocalTime

data class DiariaFinalizarDto(
    val horaFim:LocalTime,
    val kmFim:Double,
    val totalPortagens:Double? = null,
    val observacoes:String? = null
)
