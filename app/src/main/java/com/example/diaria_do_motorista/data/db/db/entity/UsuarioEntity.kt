package com.example.diaria_do_motorista.data.db.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val email: String,
    val telefone: String,
    val dataNascimento: String? = null,
    val tipo: String, // "MOTORISTA" ou "ADMINISTRADOR"
    val matriculaVeiculo: String? = null,
    val transportadoraId: String? = null,
    val senha: String,
    val ativo: Boolean = true,
    val dataCriacao: String = LocalDateTime.now().toString(),
    @ColumnInfo(name = "sync_status") val syncStatus: String = "PENDENTE"
)