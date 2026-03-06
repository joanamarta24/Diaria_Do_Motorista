package com.example.diaria_do_motorista.data.db.dao

import VeiculoEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.diaria_do_motorista.data.db.remote.enums.VeiculosStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VeiculoDao {
    //INSERTS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(veiculo: VeiculoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(veiculos: List<VeiculoEntity>): List<Long>

    //UPDATES
    @Update
    suspend fun atualizar(veiculo: VeiculoEntity): Int

    @Update
    suspend fun atualizarTodos(veiculos: List<VeiculoEntity>): Int

    //DELETES
    @Delete suspend fun deletar(veiculo: VeiculoEntity): Int

    @Query("DELETE FROM veiculos WHERE id=:id")
    suspend fun deletarPorId(id: Long): Int

    @Query("DELETE FROM veiculos WHERE transportadoraId = :transportadoraId")
    suspend fun deletarPorTransportadora(transportadoraId: Long): Int

    @Query("DELETE FROM veiculos WHERE syncstatus =:status")
    suspend fun deletarPorStatusSync(status: VeiculosStatus): Int

    @Query("DELETE FROM veiculos")
    suspend fun deletarTodos(): Int

    //QUERIES BÁSICAS
    @Query("SELECT * FROM veiculos WHERE id = :id")
    suspend fun getVeiculoPorId(id: Long): VeiculoEntity?

    @Query("SELECT * FROM veiculos WHERE id = :id")
    fun getVeiculoPorIdFlow(id: Long): Flow<VeiculoEntity?>

    @Query("SELECT * FROM veiculos WHERE placa = :placa")
    suspend fun getVeiculoPorPlaca(placa: String): VeiculoEntity?

    @Query("SELECT * FROM veiculos WHERE placa = :placa")
    fun getVeiculoPorPlacaFlow(placa: String): Flow<VeiculoEntity?>

    @Query("SELECT * FROM veiculos ORDER BY placa ASC")
    suspend fun getTodosVeiculos(): List<VeiculoEntity>

    @Query("SELECT * FROM veiculos ORDER BY placa ASC")
    fun getTodosVeiculosFlow(): Flow<List<VeiculoEntity>>

    @Query("SELECT COUNT(*) FROM veiculos")
    suspend fun contarVeiculos(): Int

    //  FILTRONS POR TRANSPORTADORA
    @Query("SELECT *  FROM veiculos WHERE transportadoraId = :transportadoraId ORDER BY placa ASC")
    suspend fun getVeiculoPorTransportadora(transportadoraId: Long): Flow<VeiculoEntity>

    @Query("SELECT * FROM veiculos WHERE transportadoraId = :transportadoraId ORDER BY placa ASC")
    fun getVeiculosPorTransportadoraFloe(transportadoraId: Long): Flow<List<VeiculoEntity>>

    @Query("SELECT COUNT(*) FROM veiculos WHERE transportadoraId = :transportadoraId")
    suspend fun contarVeiculosPorTransportadora(transportadoraId: Long): Int

    //FILTRONS POR STATUS



}