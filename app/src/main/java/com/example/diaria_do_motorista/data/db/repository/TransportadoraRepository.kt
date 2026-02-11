package com.example.diaria_do_motorista.data.db.repository

import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.remote.enums.TransportadoraStatus
import kotlinx.coroutines.flow.Flow

interface TransportadoraRepository {
    //Operações CRUD básicas
    suspend fun  salvar(transportadora: Transportadora): Transportadora
    suspend fun atualizar (transportadora: Transportadora): Transportadora
    suspend fun excluir (id: String): Boolean
    suspend fun  obterPorId(Id: String): Transportadora?
    suspend fun obterPorIdOuErro(id: String): Transportadora

    //Listagem e consultas
    fun listarTodos(): Flow<Transportadora>
    suspend fun listarPorFiltro(
        nome: String? = null,
        status: TransportadoraStatus? = null,
        ativo: Boolean? = null
    ): List<Transportadora>
    suspend fun listarPorIds(ids: List<String>): List<Transportadora>

    //CONSULTAS ESPECIFICAS
    suspend fun obterPorNome(nome: String): Transportadora?
    suspend fun obterPorEmail(email: String): Transportadora?
    suspend fun  obterPorTelefone(telefone: String): Transportadora?
    suspend fun obterPorContatoResponsavel(contato: String): Transportadora?

    //VERIFICAÇÕES
    suspend fun existePorId(id: String): Boolean
    suspend fun existePorNome(nome: String): Boolean
    suspend fun existePorEmail(email: String): Boolean
    suspend fun existePorTelefone(telefone: String): Boolean

    //Atualizações parciais
    suspend fun atualizarStatus(id: String, status: TransportadoraStatus): Boolean
    suspend fun atualizarAtivo(id: String, ativo: Boolean): Boolean
    suspend fun atualizarContatoResponsavel(id: String,contato: String, telefone: String): Boolean
    suspend fun atualizarEmail(id: String,email: String): Boolean

    //CONTAGEM E ESTATISTICAS
    suspend fun contarTotal(): Long
    suspend fun contarPorStatus(status: TransportadoraStatus): Long
    suspend fun contarAtivas(): Long
    suspend fun contarInativas(): Long

    //BUSCA PAGINADA
    suspend fun listarPaginado(
        pagina: Int,
        tamanhoPagina:Int,
        nome: String? = null,
        status: TransportadoraStatus? = null,
        ativo: Boolean? = null
    ): Pair<List<Transportadora>, Long>

    //BUSCA COM ORDENAÇÃO
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