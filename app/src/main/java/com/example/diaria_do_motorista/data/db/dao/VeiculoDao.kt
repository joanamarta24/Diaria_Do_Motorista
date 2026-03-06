package com.example.diaria_do_motorista.data.db.dao

import VeiculoEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.diaria_do_motorista.data.db.remote.enums.SyncStatus
import com.example.diaria_do_motorista.data.db.remote.enums.TipoVeiculo
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
    @Query("SELECT * FROM veiculos WHERE ativo = :ativo ORDER BY placa ASC")
    suspend fun getVeiculosPorStatus(ativo: Boolean): List<VeiculoEntity>

    @Query("SELECT * FROM veiculos WHERE ativo = :ativo ORDER BY placa ASC")
    fun getVeiculosPorStatusFlow(ativo: Boolean): Flow<List<VeiculoEntity>>

    @Query("SELECT * FROM veiculos WHERE tipo = :tipo ORDER BY placa ASC")
    suspend fun getVeiculosPorTipo(tipo: TipoVeiculo):List<VeiculoEntity>

    @Query("SELECT * FROM veiculos WHERE tipo = :tipo ORDER BY placa ASC")
    fun getVeiculosPorTipoFlow(tipo: TipoVeiculo): Flow<List<VeiculoEntity>>

    //FILTROS COMBINADOS
    @Query("""
        SELECT * FROM veiculos 
        WHERE (:transportadoraId IS NULL OR transportadoraId = :transportadoraId)
        AND (:ativo IS NULL OR ativo = :ativo)
        AND (:tipo IS NULL OR tipo = :tipo)
        ORDER BY placa ASC
    """)
    suspend fun getVeiculosFiltrados(
        transportadoraId: Long? = null,
        ativo: Boolean? = null,
        tipo: TipoVeiculo? = null
    ): List<VeiculoEntity>
    //BUSCA POR TEXTO
    @Query("""
        SELECT * FROM veiculos
        WHERE (:transportadoraId IS NULL OR transportadoraId = :transportadoraId)
        AND (:ativo IS NULL OR ativo = :ativo)
        AND (:tipo IS NULL OR TIPO = :tipo)
        ORDER BY placa ASC
    """)
    fun getVeiculosFiltradosFlow(
        transportadoraId: Long? = null,
        ativo: Boolean? = null,
        tipo: TipoVeiculo? = null,
    ): Flow<List<VeiculoEntity>>

    //BUSCA POR TEXTO
    @Query("""
        SELECT * FROM veiculos 
        WHERE placa LIKE '%'|| :termo || '%'
        OR modelo LIKE '%' || :termo || '%'
        OR marca LIKE '%'  || :termo || '%'
        OR renavam LIKE '%'|| :termo || '%'
        ORDER BY placa ASC
    """)
    suspend fun  buscaVeiculos(termo:String): List<VeiculoEntity>

    @Query("""
        SELECT * FROM veiculos 
        WHERE placa LIKE '%' || :termo || '%' 
        OR modelo LIKE '%' || :termo || '%'
        OR marca LIKE '%' || :termo || '%'
        OR renavam LIKE '%' || :termo || '%'
        ORDER BY placa ASC
    """)
    fun buscarVeiculosFlow(termo: String): Flow<List<VeiculoEntity>>

    //SICRONIZAÇÃO
    @Query("SELECT * FROM veiculos WHERE syncStatus =:status ORDER BY dataAtualizacao DESC")
    suspend fun getVeiculosPorStausSync(status: SyncStatus): List<VeiculoEntity>

    @Query("SELECT * FROM veiculos WHERE syncStatus != 'SYNCED' ORDER BY dataAtualizacao DESC")
    suspend fun getVeiculosPendentesSync(): List<VeiculoEntity>

    @Query("UPDATE veiculos SET syncStatus = :status, dataSincronizacao = :dataSincronizacao WHERE id IN (:ids)")
    suspend fun atualizarStatusSync(
        ids: List<Long>,
        status: SyncStatus,
        dataSincronizacao: String
    ): Int

    @Query("UPDATE veiculos SET syncStatus = 'SYNCED', dataSincronizacao = :dataSincronizacao WHERE id = :id")
    suspend fun marcarComoSincronizado(id: Long, dataSincronizacao: String): Int

    @Query("UPDATE veiculos SET syncStatus = 'PENDING' WHERE id = :id")
    suspend fun marcarParaRetentar(id: Long): Int

        //ESTATISTICAS
    @Query("SELECT COUNT(*) FROM veiculos WHERE ativo = 1")
    suspend fun contarVeiculosAtivos(): Int

    @Query("SELECT COUNT(*) FROM veiculos WHERE ativo = 0")
    suspend fun contarVeiculosInativos(): Int

    @Query("SELECT COUNT(*) FROM veiculos WHERE syncStatus = 'PENDING'")
    suspend fun contarPendentesSync(): Int

    @Query("SELECT tipo, COUNT(*) as quantidade FROM veiculos GROUP BY tipo")
    suspend fun contarPorTipo(): List<TipoCount>

    @Query("SELECT transportadoraId, COUNT(*) as quantidade FROM veiculos GROUP BY transportadoraId")
    suspend fun contarPorTransportadora(): List<TransportadoraCount>
       //RELACIONAMENTOS
    @Query("""
        SELECT v.* FROM veiculos v
        INNER JOIN transportadoras t ON v.transportadoraId = t.id
        WHERE t.ativo = 1
        ORDER BY v.placa ASC
    """)
    suspend fun  getVeiculosComTransportadoraAtiva(): List<VeiculoEntity>
    //QUERIES COMPLEXAS
    @Query("""
        SELECT * FROM veiculos
        WHERE dataVencimentoDocumento BETWEEN :dataInicio AND :dataFim
        AND ativo = 1
        ORDER BY dataVencimentoDocumento ASC
    """)
    suspend fun getVeiculosComDoucumentoVencendo(
        dataInicio: String,
        dataFim: String,
    ):List<VeiculoEntity>

    @Query("""
        SELECT * FROM veiculos 
        WHERE quilometragem >= :kmMinimo
        AND ativo = 1
        ORDER BY quilometragem DESC
    """)
    suspend fun getVeiculosAcimaDeQuilometragem(kmMinimo: Int): List<VeiculoEntity>

     //PAGINAÇÃO
     @Query("SELECT * FROM veiculos ORDER BY placa ASC LIMIT :limit OFFSET :offset")
     suspend fun getVeiculosPaginados(limit: Int,offset: Int): List<VeiculoEntity>

    @Query("SELECT * FROM veiculos WHERE transportadoraId = :transportadoraId ORDER BY placa ASC LIMIT :limit OFFSET :offset")
    suspend fun getVeiculosPaginadosPorTransportadora(
        transportadoraId: Long,
        limit: Int,
        offset: Int
    ): List<VeiculoEntity>
}
     //CLASSE AUXILIARES
        data class TipoCount(
            val tipo: TipoVeiculo,
            val quantidade: Int
        )
    data class TransportadoraCount(
        val transportadoraId: Long,
        val quantidade: Int
    )

