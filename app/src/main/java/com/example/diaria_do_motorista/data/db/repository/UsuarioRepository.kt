package com.example.diaria_do_motorista.data.db.repository

import UsuarioApi
import com.example.diaria_do_motorista.data.db.dao.DiariaDao
import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.dao.VeiculoDao
import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.domain.Veiculo
import com.example.diaria_do_motorista.data.db.mapper.UsuarioMappers.toEntity
import com.example.diaria_do_motorista.data.db.mapper.UsuarioMappers.toUsuario
// Importes adicionados:
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val transportadoraDao: TransportadoraDao,
    private val veiculoDao: VeiculoDao,
    private val diariaDao: DiariaDao,
    private val usuarioApi: UsuarioApi
) {
    // Usamos Dispatchers.IO diretamente para operações de Banco de Dados e Rede

    suspend fun cadastrarMotorista(
        nome: String,
        email: String,
        telefone: String,
        dataNasciemento: String,
        matriculaVeiculo: String?,
        transportadoraId: String?,
        senha: String
    ): Result<Usuario> = withContext(Dispatchers.IO) {
        try {
            val usuario = Usuario(
                id = UUID.randomUUID().toString(),
                nome = nome,
                email = email,
                telefone = telefone,
                dataNascimento = dataNasciemento,
                matriculaVeiculo = matriculaVeiculo,
                transportadoraId = transportadoraId
            )
            val entity = usuario.toEntity().copy(senha = senha)
            usuarioDao.insert(entity)

            try {
                usuarioApi.syncUsuario(entity)
                usuarioDao.updateSyncStatus(entity.id, "SINCRONIZADO")
            } catch (e: Exception) {
                // Silencia erro de rede para manter offline-first
            }

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMotoristas(): Result<List<Usuario>> = withContext(Dispatchers.IO) {
        try {
            val entities = usuarioDao.getAllMotoristas()
            val usuarios = entities.map { it.toUsuario() }
            Result.success(usuarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTransportadoras(): Result<List<Transportadora>> = withContext(Dispatchers.IO) {
        try {
            val entities = transportadoraDao.getAllAtivas()
            // Certifique-se que o Mapper de Transportadora existe
            val transportadoras = entities.map { it.toTransportadora() }
            Result.success(transportadoras)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVeiculosPorTransportadora(transportadoraId: String): Result<List<Veiculo>> = withContext(Dispatchers.IO) {
        try {
            val entities = veiculoDao.getByTransportadora(transportadoraId)
            // Corrigido para usar o mapper correspondente (assumindo que existe VeiculoMappers)
            val veiculos = entities.map { it.toVeiculo() }
            Result.success(veiculos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}