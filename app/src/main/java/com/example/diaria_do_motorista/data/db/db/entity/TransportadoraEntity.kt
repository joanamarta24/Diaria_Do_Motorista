package com.example.diaria_do_motorista.data.db.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transportadoras")
data class TransportadoraEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val contatoResponsavel: String,
    val telefone: String,
    val email: String? = null,
    val ativo: Boolean = true,
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDENTE"
)