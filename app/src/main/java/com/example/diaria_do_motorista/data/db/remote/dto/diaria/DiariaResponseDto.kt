package com.example.diaria_do_motorista.data.db.remote.dto.diaria

import java.time.LocalDate
import java.time.LocalTime

data class DiariaResponseDto(
    val id: String,
    val motoristaId: String,
    val motoristaNome: String,
    val matriculaVeiculo: String,
    val veiculoModelo: String?,
    val transportadoraId: String,
    val transportadoraNome: String?,
    val dataDiaria: LocalDate,
    val destino: String,
    val horaInicio: LocalTime,
    val horaFim: LocalTime?,
    val kmInicio: Double,
    val kmFim: Double?,
    val totalPortagens: Double?,
    val abastecimento: Boolean,
    val abastecimentoAdBlue: Boolean,
    val observacoes: String?,
    val status: String,
    val statusDescricao: String,
    val horasTrabalhadas: Double?,
    val kmRodados: Double?,
    val dataCriacao: String,
    val dataAtualizacao: String?
)