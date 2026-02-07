package com.example.diaria_do_motorista.data.db.repository

import com.example.diaria_do_motorista.data.db.dao.DiariaDao
import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.dao.VeiculoDao
import com.example.diaria_do_motorista.data.db.domain.Diaria
import com.example.diaria_do_motorista.data.db.domain.RelatorioDiarias
import com.example.diaria_do_motorista.data.db.remote.enums.DiariaStatus
import jakarta.inject.Inject
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.UUID

class DiariaRepository @Inject constructor(
    private val diariaDao: DiariaDao,
    private val usuarioDao: UsuarioDao,
    private val transportadoraDao: TransportadoraDao,
    private val veiculoDao: VeiculoDao,
    private val dispatchers: DispatchersProvider
) {
    suspend fun registrarDiaria(
        motoristaId: String,
        matriculaVeiculo: String,
        transportadoraId: String,
        destino: String,
        horaInicio: String,
        kmInicio: Double
    ): Result<Diaria> = withContext(dispatchers.io) {
        return@withContext try {
            val dataAtual = LocalDate.now().toString()

            // Verifica se já existe diária para o dia
            val diariaExistente = diariaDao.getDiariaDoDia(motoristaId, dataAtual)
            if (diariaExistente != null) {
                return@withContext Result.failure(Exception("Já existe uma diária registrada para hoje"))
            }

            val diaria = Diaria(
                id = UUID.randomUUID().toString(),
                motoristaId = motoristaId,
                matriculaVeiculo = matriculaVeiculo,
                transportadoraId = transportadoraId,
                dataDiaria = dataAtual,
                destino = destino,
                horaInicio = horaInicio,
                kmInicio = kmInicio,
                status = DiariaStatus.EM_ANDAMENTO
            )

            diariaDao.insert(diaria.toEntity())
            Result.success(diaria)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finalizarDiaria(
        diariaId: String,
        horaFim: String,
        kmFim: Double,
        totalPortagens: Double?,
        abastecimento: Boolean,
        abastecimentoAdBlue: Boolean,
        observacoes: String?
    ): Result<Diaria> = withContext(dispatchers.io) {
        return@withContext try {
            val diariaEntity = diariaDao.getById(diariaId)
                ?: return@withContext Result.failure(Exception("Diária não encontrada"))

            val diaria = diariaEntity.toDiaria()
            val diariaAtualizada = diaria.copy(
                horaFim = horaFim,
                kmFim = kmFim,
                totalPortagens = totalPortagens,
                abastecimento = abastecimento,
                abastecimentoAdBlue = abastecimentoAdBlue,
                observacoes = observacoes,
                status = DiariaStatus.FINALIZADA
            )

            diariaDao.update(diariaAtualizada.toEntity())
            Result.success(diariaAtualizada)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistoricoMotorista(motoristaId: String): Result<List<Diaria>> = withContext(dispatchers.io) {
        return@withContext try {
            val entities = diariaDao.getByMotorista(motoristaId)
            val diarias = entities.map { it.toDiaria() }
            Result.success(diarias)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRelatorioDiarias(
        dataInicio: String? = null,
        dataFim: String? = null,
        transportadoraId: String? = null,
        matriculaVeiculo: String? = null,
        motoristaId: String? = null
    ): Result<RelatorioDiarias> = withContext(dispatchers.io) {
        return@withContext try {
            val diariasEntities = diariaDao.getDiariasFiltradas(
                dataInicio = dataInicio,
                dataFim = dataFim,
                transportadoraId = transportadoraId,
                matriculaVeiculo = matriculaVeiculo,
                motoristaId = motoristaId
            )

            val diarias = diariasEntities.map { it.toDiaria() }

            val totalHoras = diarias.sumOf { it.horasTrabalhadas ?: 0.0 }
            val totalKm = diarias.sumOf { it.kmRodados ?: 0.0 }
            val totalPortagens = diarias.sumOf { it.totalPortagens ?: 0.0 }

            val relatorio = RelatorioDiarias(
                diarias = diarias,
                totalHoras = totalHoras,
                totalKm = totalKm,
                totalPortagens = totalPortagens,
                totalDiarias = diarias.size
            )

            Result.success(relatorio)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}