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

    @Query("SELECT * FROM transportadoras WHERE id IN (:ids)")
    abstract override suspend fun listarPorIds(ids: List<String>): List<Transportadora>

     @Query("SELECT * FROM transportadoras WHERE nome = :nome LIMIT 1")
     abstract override suspend fun obterPorNome(nome: String): Transportadora?

     @Query("SELECT * FROM transportadoras WHERE  email = :email LIMIT 1")
     abstract override suspend fun obterPorEmail(email: String): Transportadora

     @Query("SELECT * FROM transportadoras WHERE telefone =:telefone LIMIT 1")
     abstract override suspend fun  obterPorTelefone(telefone: String): Transportadora?

    @Query("SELECT * FROM transportadoras WHERE contatoresponsavel = :contato LIMIT 1")
    abstract override suspend fun obterPorContatoResponsavel(contato: String): Transportadora?

    @Query("SELECT COUNT(*) FROM transportadoras WHERE id = :id")
    abstract override suspend fun existePorId(id: String): Boolean
    @Query("SELECT COUNT(*) FROM transportadoras WHERE nome =:nome")
    abstract override suspend fun existePorNome(nome: String): Boolean

    @Query("SELECT COUNT(*) FROM transportadoras WHERE email = :email")
    abstract override suspend fun existePorEmail(email: String): Boolean
    @Query("SELECT COUNT(*) FROM transportadoras WHERE telefone = :telefone")
    abstract override suspend fun existePorTelefone(telefone: String): Boolean
    @Query("SELECT COUNT(*) FROM transportadoras")
    abstract override suspend fun contarTotal(): Long

    @Query("SELECT COUNT(*) FROM transportadoras WHERE status = :status")
    abstract override suspend fun contarPorStatus(status: TransportadoraStatus): Long

    @Query("SELECT COUNT(*) FROM transportadoras WHERE ativo = 1")
    abstract override suspend fun contarAtivas(): Long

    @Query("SELECT COUNT(*) FROM transportadoras WHERE ativo = 0")
    abstract override suspend fun contarInativas(): Long

    @Query("""
        SELECT COUNT(*) FROM transportadoras
        WHERE (:nome IS NULL OR nome LIKE '%' || :nome || '%')
        AND (:status IS NULL OR status = :status)
        AND (:ativo IS NULL OR ativo = :ativo)
    """)
    abstract override suspend fun contarPorFiltro(
        nome: String?,
        status: TransportadoraStatus?,
        ativo: Boolean?
    ): Long
    @Query("""
        SELECT FROM transportadoras
        WHERE (:nome IS NULL OR nome LIKE '%' || :nome || '%')
        WHERE (:status IS NULL OR status = :status)
        AND (:ativo IS NULL OR ativo = :ativo)
        ORDER BY nome
        LIMIT :limete OFFSET :offset
    """)
    abstract override suspend fun listarPaginado(
        offser:Int,
        limite:Int,
        nome: String?,
        status: TransportadoraStatus?,
        ativo: Boolean?
    ): List<Transportadora>

    @Query(
        """
        SELECT * FROM transportadoras
        WHERE (:ativo IS NULL OR ativo = :ativo)
        ORDER BY nome ${"ASC"}
    """
    )
    abstract override suspend fun listarOrdenadasPorNome(
        crescente: Boolean,
        ativo: Boolean?
    ): List<Transportadora>



}