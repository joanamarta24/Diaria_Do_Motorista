package com.example.diaria_do_motorista.data.db.domain

import com.example.diaria_do_motorista.data.db.remote.enums.DiariaStatus

data class Diaria(
    val id: String,
    val motoristaId: String,
    val matriculaVeiculo: String,
    val transportadoraId: String,
    val dataDiaria: String,
    val destino: String,
    val horaInicio: String,
    val horaFim: String? = null,
    val kmInicio: Double,
    val kmFim: Double? = null,
    val totalPortagens: Double? = null,
    val abastecimento: Boolean = false,
    val abastecimentoAdBlue: Boolean = false,
    val observacoes: String? = null,
    val status: DiariaStatus = DiariaStatus.EM_ANDAMENTO,
    val horasTrabalhadas: Double? = null,
    val kmRodados: Double? = null
)