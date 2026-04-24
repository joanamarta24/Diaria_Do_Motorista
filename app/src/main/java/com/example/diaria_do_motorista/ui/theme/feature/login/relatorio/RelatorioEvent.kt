package com.example.diaria_do_motorista.ui.theme.feature.login.relatorio

import ExportFormat

sealed class RelatorioEvent {
    data class OnDataInicioChange(val dataInicio: String): RelatorioEvent()
    data class OnDataFimChange(val dataFim: String): RelatorioEvent()
    data class OnTransportadoraSelected(val transportadoraId: String?): RelatorioEvent()
    data class OnVeiculoSelected(val matricula: String?) : RelatorioEvent()
    data class OnMotoristaSelected(val motoristaId: String?) : RelatorioEvent()
    object OnGerarRelatorio : RelatorioEvent()
    data class OnExportarRelatorio(val format: ExportFormat) : RelatorioEvent()
    object ClearMessages : RelatorioEvent()

}
