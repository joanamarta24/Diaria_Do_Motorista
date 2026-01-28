package com.example.diaria_do_motorista.data.db.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("veiculos")
data class VeiculoEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val matricula: String, // placa/license plate
    val marca: String,
    val modelo: String,
    val transportadoraId: String,
    val ativo: Boolean = true,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDENTE"

)
