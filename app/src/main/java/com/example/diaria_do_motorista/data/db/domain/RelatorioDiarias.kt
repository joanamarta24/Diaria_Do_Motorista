package com.example.diaria_do_motorista.data.db.domain

data class RelatorioDiarias(
    val diaria: List<Diaria>,
    val totalHoras: Double,
    val totalKm: Double,
    val totalPortagens: Double,
    val  totalDiarias: Int
)