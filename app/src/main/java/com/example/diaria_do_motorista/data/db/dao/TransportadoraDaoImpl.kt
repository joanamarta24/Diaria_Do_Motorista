package com.example.diaria_do_motorista.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.remote.enums.TransportadoraStatus
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransportadoraDaoImpl: TransportadoraDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract override suspend fun salvar(transportadora: Transportadora): Long

    @Update
    abstract override suspend fun atualizar(transportadora: Transportadora): Int

    @Query("DELETE FROM transportadoras WHERE id =:id")
    abstract override suspend fun excluir(id: String): Int

    @Query("SELECT *FROM transportadoras WHERE id =:id")
    abstract override suspend fun obterPorId(id: String): Transportadora?

    @Query("SELECT * FROM transportadoras ORDER BY nome")
    abstract override fun listarTodos(): Flow<List<Transportadora>>

    @Transaction
    override suspend fun salvar(transportadora: Transportadora): Transportadora {
        val id = salvar(transportadora)
        return transportadora.copy(id = id.toString())
    }
   @Transaction
   override suspend fun atualizar(transportadora: Transportadora): Transportadora{
       val rows = atualizar(transportadora)
       if (rows == 0){
           throw IllegalStateException("Transportadora não encotrada para atualização")
       }
       return transportadora
   }
    @Transaction
    override suspend fun excluir(id: String): Boolean{
        return excluir(id) >0
    }
    @Query("""
        SELECT * FROM transportadoras
        WHERE (:nome IS NULL OR nome LIKE '%'|| :nome|| '%')
        AND (:status IS NULL OR status =:status)
        AND (:ativo IS NULL OR ativo = :ativo)
        ORDER BY nome
    """)
    abstract override suspend fun listarPorFiltro(
        nome: String?,
        status: TransportadoraStatus?,
        ativo: Boolean?
    ): List<Transportadora>


}