package com.example.diaria_do_motorista.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID
@Entity(
    tableName = "diarias",
    indices = [Index(value = ["motoristaId", "dataDiaria"], unique = true)]
)
data class DiariaEntity(
    @PrimaryKey
    val id:String = UUID.randomUUID().toString(),
    val motoristaId:String,
    val matriculaVeiculoId:String,
    val transportadoraId:String,
    val dataDiaria:String,
    val destino:String,
    val horaInicio:String,
    val horaFim:String,
    val totalPortagens: Double? = null,
    val abastecimento: Boolean = false,
    val abastecimentoAdBlue: Boolean = false,
    val observacoes: String? = null,
    val status:String = "EM_ANDAMENTO", //EM_ANDAMENTO, FINALIZADA
    @ColumnInfo("sync_status") val syncStatus:String = "PENDENTE",
    val horaTrabalhadas:Double? = null,
    val kmRodados:Double? = null
)
