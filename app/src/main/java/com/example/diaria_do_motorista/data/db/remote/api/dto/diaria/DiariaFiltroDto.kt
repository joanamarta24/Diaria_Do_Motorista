package com.example.diaria_do_motorista.data.db.remote.api.dto.diaria

import java.time.LocalDate

data class DiariaFiltroDto(
    val motoristaId:String? = null,
    val transportadoraId:String? = null,
    val matriculaVeiculo: String? = null,
    val dataInicio: LocalDate? = null,
    val dataFim: LocalDate? = null,
    val status: String? = null,
    val destino: String? = null
)