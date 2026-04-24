package com.example.diaria_do_motorista.ui.theme.feature.login.relatorio

import ExportFormat
import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.databinding.library.BuildConfig
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaria_do_motorista.data.db.ExportadorRelatorios
import com.example.diaria_do_motorista.data.db.repository.DiariaRepository
import com.example.diaria_do_motorista.data.db.repository.UsuarioRepository
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.io.File

@HiltViewModel
class  RelatorioViewModel @Inject constructor(
    private val diariaRepository: DiariaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val exportadorRelatorios: ExportadorRelatorios
): ViewModel() {
    private val _uiState = MutableStateFlow(RelatorioUiState())
    val uiState: StateFlow<RelatorioUiState> = _uiState.asStateFlow()

    init {
        loadDadosFiltro()
        gerarRelatorio()
    }

    fun handleEvent(event: RelatorioEvent) {
        when (event) {
            is RelatorioEvent.OnDataInicioChange -> updateDataInicio(event.dataInicio)
            is RelatorioEvent.OnDataFimChange -> updateDataFim(event.dataFim)
            is RelatorioEvent.OnTransportadoraSelected -> selectTransportadora(event.transportadoraId)
            is RelatorioEvent.OnVeiculoSelected -> selectVeiculo(event.matricula)
            is RelatorioEvent.OnMotoristaSelected -> selectMotorista(event.motoristaId)
            is RelatorioEvent.OnGerarRelatorio -> gerarRelatorio()
            is RelatorioEvent.OnExportarRelatorio -> exportarRelatorio(event.format)
            RelatorioEvent.ClearMessages -> clearMessages()
        }
    }

    private fun updateDataInicio(dataInicio: String) {
        _uiState.update { it.copy(dataInicio = dataInicio) }
    }

    private fun updateDataFim(dataFim: String) {
        _uiState.update { it.copy(dataFim = dataFim) }
    }

    private fun selectTransportadora(transportadoraId: String?) {
        _uiState.update {
            it.copy(
                transportadoraId = transportadoraId,
                matriculaVeiculo = null // Reset veículo quando muda transportadora
            )
        }
        if (transportadoraId != null) {
            loadVeiculos(transportadoraId)
        }
    }

    private fun selectVeiculo(matricula: String?) {
        _uiState.update { it.copy(matriculaVeiculo = matricula) }
    }

    private fun selectMotorista(motoristaId: String?) {
        _uiState.update { it.copy(motoristaId = motoristaId) }
    }

    private fun loadDadosFiltro() {
        viewModelScope.launch {
            //CARREGA TRANSPORTADORAS
            val transportadorasResult = usuarioRepository.getTransportadoras()
            if (transportadorasResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        transportadoras = transportadorasResult.getOrNull() ?: emptyList
                    )
                }
            }
            // Carrega motoristas
            val motoristasResult = usuarioRepository.getMotoristas()
            if (motoristasResult.isSuccess) {
                _uiState.update {
                    it.copy(motoristas = motoristasResult.getOrNull() ?: emptyList()) }
            }
        }
    }
    private fun loadVeiculos(transportadoraId: String) {
        viewModelScope.launch {
            val result = usuarioRepository.getVeiculosPorTransportadora(transportadoraId)
            if (result.isSuccess){
                _uiState.update { it.copy(veiculos = result.getOrNull()?:emptyList())
                }
            }
        }
    private fun gerarRelatorio() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = diariaRepository.getRelatorioDiarias(
                dataInicio = uiState.value.dataInicio,
                dataFim = uiState.value.dataFim,
                transportadoraId = uiState.value.transportadoraId,
                matriculaVeiculo = uiState.value.matriculaVeiculo,
                motoristaId = uiState.value.motoristaId
            )
            if (result.isSuccess){
                _uiState.update {
                    it.copy(
                        relatorio = result.getOrNull(),
                        isLoading = false
                    )
                }
            }else{
                _uiState.update {
                    it.copy(
                        error ="Erro ao gerar relatorio: ${result.exceptionOrNull()?.message}",
                        isLoading = false

                    )
                }
            }
        }
    }
        private fun exportarRelatorio(format: ExportFormat) {
            viewModelScope.launch {
                _uiState.update { it.copy(isExporting = true, exportFormat = format) }

                try {
                    val relatorio = uiState.value.relatorio
                    if (relatorio != null) {
                        val fileName = "relatorio_diarias_${System.currentTimeMillis()}"
                        val file = when (format) {
                            ExportFormat.PDF -> exportadorService.exportarRelatorioParaPDF(relatorio, fileName)
                            ExportFormat.XLSX -> exportadorService.exportarRelatorioParaXLSX(relatorio, fileName)
                        }

                        // Compartilhar o arquivo
                        compartilharArquivo(file)
                    } else {
                        _uiState.update { it.copy(
                            isExporting = false,
                            erro = "Nenhum relatório disponível para exportar"
                        ) }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(
                        isExporting = false,
                        erro = "Erro ao exportar relatório: ${e.message}"
                    ) }
                } finally {
                    _uiState.update { it.copy(isExporting = false, exportFormat = null) }
                }
            }
        }
    private fun shareFile(file: File) {
        // Implementar compartilhamento do arquivo
        val uri = FileProvider.getUriForFile(
            getApplication<Application>(),
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (file.extension == "pdf") "application/pdf" else "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

    }
    private fun clearMessages() {
        _uiState.update { it.copy(error = null) }
    }
    sealed class RelatorioEvent{
        data class OnDataInicioChange(val dataInicio: String): RelatorioEvent()
        data class OnDataFimChange(val dataFim: String): RelatorioEvent()
        data class OnTransportadoraSelected(val transportadoraId: String?): RelatorioEvent()
        data class OnVeiculoSelected(val matricula: String?) : RelatorioEvent()
        data class OnMotoristaSelected(val motoristaId: String?) : RelatorioEvent()
        object OnGerarRelatorio : RelatorioEvent()
        data class OnExportarRelatorio(val format: ExportFormat) : RelatorioEvent()
        object ClearMessages : RelatorioEvent()

    }
}

