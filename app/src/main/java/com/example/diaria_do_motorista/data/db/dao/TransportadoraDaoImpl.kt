package com.example.diaria_do_motorista.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.remote.enums.TransportadoraStatus
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransportadoraDaoImpl : TransportadoraDao {

    // -------------------------------------------------------------------------
    // INSERT / UPDATE / DELETE internos (usados pelas funções @Transaction)
    // -------------------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun inserir(transportadora: Transportadora): Long

    @Update
    abstract suspend fun atualizarInterno(transportadora: Transportadora): Int

    @Query("DELETE FROM transportadoras WHERE id = :id")
    abstract suspend fun excluirInterno(id: String): Int

    // -------------------------------------------------------------------------
    // Operações públicas com @Transaction
    // -------------------------------------------------------------------------

    // FIX #3: separamos o @Insert interno ("inserir") do @Transaction público ("salvar")
    @Transaction
    override suspend fun salvar(transportadora: Transportadora): Transportadora {
        val id = inserir(transportadora)
        return transportadora.copy(id = id.toString())
    }

    @Transaction
    override suspend fun atualizar(transportadora: Transportadora): Transportadora {
        val rows = atualizarInterno(transportadora)
        if (rows == 0) {
            throw IllegalStateException("Transportadora não encontrada para atualização")
        }
        return transportadora
    }

    @Transaction
    override suspend fun excluir(id: String): Boolean {
        return excluirInterno(id) > 0
    }

    // -------------------------------------------------------------------------
    // Consultas simples
    // -------------------------------------------------------------------------

    @Query("SELECT * FROM transportadoras WHERE id = :id")
    abstract override suspend fun obterPorId(id: String): Transportadora?

    @Query("SELECT * FROM transportadoras ORDER BY nome")
    abstract override fun listarTodos(): Flow<List<Transportadora>>

    @Query("SELECT * FROM transportadoras WHERE nome = :nome LIMIT 1")
    abstract override suspend fun obterPorNome(nome: String): Transportadora?

    // FIX #5: retorno nullable para evitar NullPointerException
    @Query("SELECT * FROM transportadoras WHERE email = :email LIMIT 1")
    abstract override suspend fun obterPorEmail(email: String): Transportadora?

    @Query("SELECT * FROM transportadoras WHERE telefone = :telefone LIMIT 1")
    abstract override suspend fun obterPorTelefone(telefone: String): Transportadora?

    @Query("SELECT * FROM transportadoras WHERE contatoresponsavel = :contato LIMIT 1")
    abstract override suspend fun obterPorContatoResponsavel(contato: String): Transportadora?

    @Query("SELECT * FROM transportadoras WHERE id IN (:ids)")
    abstract override suspend fun listarPorIds(ids: List<String>): List<Transportadora>

    // -------------------------------------------------------------------------
    // Filtros e paginação
    // -------------------------------------------------------------------------

    @Query("""
        SELECT * FROM transportadoras
        WHERE (:nome IS NULL OR nome LIKE '%' || :nome || '%')
        AND (:status IS NULL OR status = :status)
        AND (:ativo IS NULL OR ativo = :ativo)
        ORDER BY nome
    """)
    abstract override suspend fun listarPorFiltro(
        nome: String?,
        status: TransportadoraStatus?,
        ativo: Boolean?
    ): List<Transportadora>

    // FIX #2: "SELECT *", apenas um WHERE, typos "limete"→"limite" e "offser"→"offset" corrigidos
    @Query("""
        SELECT * FROM transportadoras
        WHERE (:nome IS NULL OR nome LIKE '%' || :nome || '%')
        AND (:status IS NULL OR status = :status)
        AND (:ativo IS NULL OR ativo = :ativo)
        ORDER BY nome
        LIMIT :limite OFFSET :offset
    """)
    abstract override suspend fun listarPaginado(
        offset: Int,
        limite: Int,
        nome: String?,
        status: TransportadoraStatus?,
        ativo: Boolean?
    ): List<Transportadora>

    // -------------------------------------------------------------------------
    // Ordenação dinâmica — FIX #4: duas queries separadas para ASC e DESC
    // -------------------------------------------------------------------------

    @Query("""
        SELECT * FROM transportadoras
        WHERE (:ativo IS NULL OR ativo = :ativo)
        ORDER BY nome ASC
    """)
    abstract suspend fun listarOrdenadasPorNomeAsc(ativo: Boolean?): List<Transportadora>

    @Query("""
        SELECT * FROM transportadoras
        WHERE (:ativo IS NULL OR ativo = :ativo)
        ORDER BY nome DESC
    """)
    abstract suspend fun listarOrdenadasPorNomeDesc(ativo: Boolean?): List<Transportadora>

    override suspend fun listarOrdenadasPorNome(
        crescente: Boolean,
        ativo: Boolean?
    ): List<Transportadora> =
        if (crescente) listarOrdenadasPorNomeAsc(ativo)
        else listarOrdenadasPorNomeDesc(ativo)

    @Query("""
        SELECT * FROM transportadoras
        WHERE (:ativo IS NULL OR ativo = :ativo)
        ORDER BY dataCriacao ASC
    """)
    abstract suspend fun listarOrdenadasPorDataCriacaoAsc(ativo: Boolean?): List<Transportadora>

    @Query("""
        SELECT * FROM transportadoras
        WHERE (:ativo IS NULL OR ativo = :ativo)
        ORDER BY dataCriacao DESC
    """)
    abstract suspend fun listarOrdenadasPorDataCriacaoDesc(ativo: Boolean?): List<Transportadora>

    override suspend fun listarOrdenadasPorDataCriacao(
        crescente: Boolean,
        ativo: Boolean?
    ): List<Transportadora> =
        if (crescente) listarOrdenadasPorDataCriacaoAsc(ativo)
        else listarOrdenadasPorDataCriacaoDesc(ativo)

    // -------------------------------------------------------------------------
    // Contagens
    // -------------------------------------------------------------------------

    @Query("SELECT COUNT(*) FROM transportadoras WHERE id = :id")
    abstract override suspend fun existePorId(id: String): Boolean

    @Query("SELECT COUNT(*) FROM transportadoras WHERE nome = :nome")
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

    // -------------------------------------------------------------------------
    // Operações em lote
    // -------------------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract override suspend fun salvarTodoss(transportadoras: List<Transportadora>): List<Long>

    // FIX #1: "<" substituído por "," no SET
    @Query("""
        UPDATE transportadoras
        SET status = :status, dataAtualizada = CURRENT_TIMESTAMP
        WHERE id IN (:ids)
    """)
    abstract override suspend fun atualizarStatusEmLote(
        ids: List<String>,
        status: TransportadoraStatus
    ): Int

    @Query("""
        UPDATE transportadoras
        SET ativo = :ativo, dataAtualizada = CURRENT_TIMESTAMP
        WHERE id IN (:ids)
    """)
    abstract override suspend fun atualizarAtivoEmLote(ids: List<String>, ativo: Boolean): Int
}