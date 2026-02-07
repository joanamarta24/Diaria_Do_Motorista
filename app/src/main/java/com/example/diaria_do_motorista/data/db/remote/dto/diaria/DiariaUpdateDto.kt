package com.example.diaria_do_motorista.data.db.remote.dto.diaria

import java.time.LocalTime

data class DiariaUpdateDto(
    val horaFim:LocalTime? = null,
    val kmFim:Double? = null,
    val totalPortagens:Double? = null,
    val abastecimento:Boolean? = null,
    val abastecimentoAdBlue: Boolean? = null,
    val observacoes:String? = null,
    val status:String? = null
)
