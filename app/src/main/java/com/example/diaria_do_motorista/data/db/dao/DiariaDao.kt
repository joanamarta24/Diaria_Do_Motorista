package com.example.diaria_do_motorista.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.diaria_do_motorista.data.db.entity.DiariaEntity

@Dao
interface DiariaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diaria: DiariaEntity)

    @Update
    suspend fun update(diaria: DiariaEntity)

    @Query("SELECT * FROM diarias WHERE motoristaId = :motoristaId ORDER BY dataDiaria DESC")
    suspend fun getByMotorista(motoristaId:String):List<DiariaEntity>

    @Query("SELECT * FROM diarias WHERE ID = :id")
    suspend fun getId(id:String):DiariaEntity?

    @Query("SELECT *FROM diarias WHERE motoristaId = :motoristaId AND dataDiaria =:dataDiaria")
    suspend fun getDiatiaDoDia(motoristaId: String, dataDiaria:String):DiariaEntity?

    @Query("SELECT * FROM diarias WHERE sync_status = 'PENDETE'")
    suspend fun getPedentesSync():List<DiariaEntity>

    @Query("""
        SELECT * FROM diarias 
        WHERE (:dataInicio IS NULL OR dataDiaria >= :dataInicio)
        AND (:dataFim IS NULL OR dataDiaria <= :dataFim)
        AND (:transportadoraId IS NULL OR transportadoraId = :transportadoraId)
        AND (:matriculaVeiculo IS NULL OR matriculaVeiculoId = :matriculaVeiculo)
        AND (:motoristaId IS NULL OR motoristaId = :motoristaId)
        ORDER BY dataDiaria DESC
    """)
    suspend fun getDiariasFiltradas(
        dataInicio: String? = null,
        dataFim: String? = null,
        transportadoraId: String? = null,
        matriculaVeiculo: String? = null,
        motoristaId: String? = null
    ): List<DiariaEntity>

    @Query("UPDATE diarias SET sync_status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)

}