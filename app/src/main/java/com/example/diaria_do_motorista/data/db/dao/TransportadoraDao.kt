package com.example.diaria_do_motorista.data.db.dao

import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.remote.enums.TransportadoraStatus
import kotlinx.coroutines.flow.Flow

interface TransportadoraDao {
    //Operações CRUD
    suspend fun salvar(transportadora: Transportadora): Transportadora
    suspend fun atualizar(transportadora: Transportadora): Transportadora
    suspend fun excluir(id: String): Boolean
    suspend fun obterPorId(id: String): Transportadora?

    //CONSULTAS
    fun listarTodos(): Flow<Transportadora>
    suspend fun listarPorFiltros(
        nome: String? = null,
        status: TransportadoraStatus? = null,
        ativo: Boolean? = null
    ): List<Transportadora>

    suspend fun listarPorIds(ids: List<String>):

    // Buscas específicas
            suspend fun obterPorNome(nome: String): Transportadora?
    suspend fun obterPorEmail(email: String): Transportadora?
    suspend fun obterPorTelefone(telefone: String): Transportadora?
    suspend fun obterPorContatoResponsavel(contato: String): Transportadora?

    // Verificações
    suspend fun existePorId(id: String): Boolean
    suspend fun existePorNome(nome: String): Boolean
    suspend fun existePorEmail(email: String): Boolean
    suspend fun existePorTelefone(telefone: String): Boolean

    // Contagem
    suspend fun contarTotal(): Long
    suspend fun contarPorStatus(status: TransportadoraStatus): Long
    suspend fun contarAtivas(): Long
    suspend fun contarInativas(): Long
    suspend fun contarPorFiltro(
        nome: String? = null,
        status: TransportadoraStatus? = null,
        ativo: Boolean? = null
    ): Long

    // Paginação
    suspend fun listarPaginado(
        offset: Int,
        limite: Int,
        nome: String? = null,
        status: TransportadoraStatus? = null,
        ativo: Boolean? = null
    ): List<Transportadora>

    // Ordenação
    suspend fun listarOrdenadasPorNome(
        crescente: Boolean = true,
        ativo: Boolean? = null
    ): List<Transportadora>

    suspend fun listarOrdenadasPorDataCriacao(
        crescente: Boolean = false,
        ativo: Boolean? = null
    ): List<Transportadora>

    // Operações em lote
    suspend fun salvarTodos(transportadoras: List<Transportadora>): List<Transportadora>
    suspend fun atualizarStatusEmLote(ids: List<String>, status: TransportadoraStatus): Int
    suspend fun atualizarAtivoEmLote(ids: List<String>, ativo: Boolean): Int
}