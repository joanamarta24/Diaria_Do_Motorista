package com.example.diaria_do_motorista.data.db.repository

import com.example.diaria_do_motorista.data.db.dao.DiariaDao
import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.dao.VeiculoDao
import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.domain.Veiculo
import com.example.diaria_do_motorista.data.db.mapper.UsuarioMappers.toEntity
import com.example.diaria_do_motorista.data.db.mapper.UsuarioMappers.toUsuario
import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioApi
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class UsuarioRepository  @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val transportadoraDao: TransportadoraDao,
    private val veiculoDao: VeiculoDao,
    private val diariaDao: DiariaDao,
    private val usuarioApi: UsuarioApi,
    private val dispatchers: DispatchersProvider
) {
    suspend fun cadastrarMotorista(
        nome: String,
        email: String,
        telefone: String,
        dataNasciemento: String,
        matriculaVeiculo: String?,
        transportadoraId: String?,
        senha: String
    ): Result<Usuario> = withContext(dispatchers.io) {
        return@withContext try {
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
                // Mantém como pendente para sync posterior
            }

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMotoristas(): Result<List<Usuario>> = withContext(dispatchers.io){
        return@withContext try {
            val entities = usuarioDao.getAllMotoristas()
            val usuarioSolicitarRedefinicaoSenhaDto = entities.map { it.toUsuario() }
            Result.success(usuarios)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getTransportadoras(): Result<List<Transportadora>> = withContext(dispatchers.io) {
        return@withContext try {
            val entities = transportadoraDao.getAllAtivas()
            val transportadoras = entities.map { it.toTransportadora() }
            Result.success(transportadoras)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVeiculosPorTransportadora(transportadoraId: String): Result<List<Veiculo>> = withContext(dispatchers.io) {
        return@withContext try {
            val entities = veiculoDao.getByTransportadora(transportadoraId)
            val veiculos = entities.map { VeiculoMappers.toVeiculo(it) }
            Result.success(veiculos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
