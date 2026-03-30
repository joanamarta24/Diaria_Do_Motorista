package com.example.diaria_do_motorista.ui.theme.feature.login.relatorio

import com.example.diaria_do_motorista.data.db.domain.RelatorioDiarias
import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.domain.Veiculo
import com.example.diaria_do_motorista.data.db.remote.enums.ExportFormat
import java.time.LocalDate

data class RelatorioUiState(
    val relatorio: RelatorioDiarias? =null,
    val isLoading: Boolean = false,
    val erro: String? = null,
    val dataInicio: String = LocalDate.now().withDayOfMonth(1).toString(),
    val dataFim: String = LocalDate.now().toString(),
    val transportadoraId: String? = null,
    val matriculaVeiculo: String? = null,
    val motoristaId: String? = null,
    val transportadoras: List<Transportadora> = emptyList(),
    val veiculos: List <Veiculo> = emptyList(),
    val motoristas: List<Usuario> = emptyList(),
    val isExporting: Boolean = false,
    val exportFormat: ExportFormat? = null
)