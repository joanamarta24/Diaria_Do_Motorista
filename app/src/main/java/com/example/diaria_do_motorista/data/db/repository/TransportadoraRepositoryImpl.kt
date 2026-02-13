package com.example.diaria_do_motorista.data.db.repository

import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.remote.enums.TransportadoraStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class TransportadoraRepositoryImpl (
    private val transportadoraDao: TransportadoraDao
): TransportadoraRepository{
    override suspend fun salvar(transportadora: Transportadora): Transportadora {
        return transportadoraDao.salvar(transportadora)
    }

    override suspend fun atualizar(transportadora: Transportadora): Transportadora {
        val existente = obterPorIdOuErro(transportadora.id)
        val atualizada = existente.copy(
            nome = transportadora.nome,
            contatoResponsavel = transportadora.contatoResponsavel,
            telefone = transportadora.telefone,
            email = transportadora.email,
            ativo = transportadora.ativo,
            dataAtualizada = LocalDateTime.now()
        )
        return transportadoraDao.atualizar(atualizada)
    }


    override suspend fun excluir(id: String): Boolean {
        return transportadoraDao.excluir(id)
    }

    override suspend fun obterPorId(id: String): Transportadora? {
        return transportadoraDao.obterPorId(id)
    }

    override suspend fun obterPorIdOuErro(id: String): Transportadora {
       return obterPorId(id) ?: throw IllegalArgumentException("Transportadora com ID $id naõ encontrada")
    }

    override fun listarTodos(): Flow<Transportadora> {
        return transportadoraDao.listarTodos()
    }

    override suspend fun listarPorFiltro(
        nome: String?,
        status: TransportadoraStatus?,
        ativo: Boolean?
    ): List<Transportadora> {
        return transportadoraDao.listarPorFiltros(nome,status,ativo)
    }

    override suspend fun listarPorIds(ids: List<String>): List<Transportadora> {
       return transportadoraDao.listarPorIds(ids)
    }

    override suspend fun obterPorNome(nome: String): Transportadora? {
       return transportadoraDao.obterPorNome(nome)
    }

    override suspend fun obterPorEmail(email: String): Transportadora? {
      return transportadoraDao.obterPorEmail(email)
    }

    override suspend fun obterPorTelefone(telefone: String): Transportadora? {
        return transportadoraDao.obterPorTelefone(telefone)
    }

    override suspend fun obterPorContatoResponsavel(contato: String): Transportadora? {
        return transportadoraDao.obterPorContatoResponsavel(contato)
    }

    override suspend fun existePorId(id: String): Boolean {
       return transportadoraDao.existePorId(id)
    }

    override suspend fun existePorNome(nome: String): Boolean {
       return  transportadoraDao.existePorNome(nome)
    }

    override suspend fun existePorEmail(email: String): Boolean {
        return transportadoraDao.existePorEmail(email)
    }

    override suspend fun existePorTelefone(telefone: String): Boolean {
        return transportadoraDao.existePorTelefone(telefone)
    }

    override suspend fun atualizarStatus(id: String, status: TransportadoraStatus): Boolean {
        val transportadora = obterPorIdOuErro(id)
        val atualizada = transportadora.copy(
            status = status,
            dataAtualizada = LocalDateTime.now()
        )
        transportadoraDao.atualizar(atualizada)
        return true
    }

    override suspend fun atualizarAtivo(id: String, ativo: Boolean): Boolean {
        val transportadora = obterPorIdOuErro(id)
        val atualizada = transportadora.copy(
            ativo = ativo,
            dataAtualizada = LocalDateTime.now()
        )
        transportadoraDao.atualizar(atualizada)
        return true
    }

    override suspend fun atualizarContatoResponsavel(id: String, contato: String, telefone: String): Boolean {
        val transportadora = obterPorIdOuErro(id)
        val atualizada = transportadora.copy(
            contatoResponsavel = contato,
            telefone = telefone,
            dataAtualizada = LocalDateTime.now()
        )
        transportadoraDao.atualizar(atualizada)
        return true
    }

    override suspend fun atualizarEmail(id: String, email: String): Boolean {
        val transportadora = obterPorIdOuErro(id)
        val atualizada = transportadora.copy(
            email = email,
            dataAtualizada = LocalDateTime.now()
        )
        transportadoraDao.atualizar(atualizada)
        return true
    }

    override suspend fun contarTotal(): Long {
        return transportadoraDao.contarTotal()
    }

    override suspend fun contarPorStatus(status: TransportadoraStatus): Long {
       return transportadoraDao.contarPorStatus(status)
    }

    override suspend fun contarAtivas(): Long {
       return transportadoraDao.contarAtivas()
    }

    override suspend fun contarInativas(): Long {
       return transportadoraDao.contarInativas()
    }

    override suspend fun listarPaginado(
        pagina: Int,
        tamanhoPagina: Int,
        nome: String?,
        status: TransportadoraStatus?,
        ativo: Boolean?
    ): Pair<List<Transportadora>, Long> {
        val offset = pagina * tamanhoPagina
        val transportadoras = transportadoraDao.listarPaginado(
            offset = offset,
            limite = tamanhoPagina,
            nome = nome,
            status = status,
            ativo = ativo
        )
        val total = transportadoraDao.contarPorFiltro(nome, status, ativo)
        return Pair(transportadoras, total)
    }

    override suspend fun listarOrdenadasPorNome(
        crescente: Boolean,
        ativo: Boolean?
    ): List<Transportadora> {
       return transportadoraDao.listarOrdenadasPorNome(crescente,ativo)
    }

    override suspend fun listarOrdenadasPorDataCriacao(
        crescente: Boolean,
        ativo: Boolean?
    ): List<Transportadora> {
      return transportadoraDao.listarOrdenadasPorDataCriacao(crescente,ativo)
    }

    override suspend fun salvarTodos(transportadoras: List<Transportadora>): List<Transportadora> {
        return transportadoraDao.salvarTodos(transportadoras)
    }

    override suspend fun atualizarStatusEmLote(ids: List<String>, status: TransportadoraStatus): Int {
        return  transportadoraDao.atualizarStatusEmLote(ids, status)
    }

    override suspend fun atualizarAtivoEmLote(ids: List<String>, ativo: Boolean): Int {
        return transportadoraDao.atualizarAtivoEmLote(ids,ativo)
    }

}