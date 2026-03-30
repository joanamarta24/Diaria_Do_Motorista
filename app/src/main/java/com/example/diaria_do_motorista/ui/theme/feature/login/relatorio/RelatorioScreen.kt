package com.example.diaria_do_motorista.ui.theme.feature.login.relatorio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaria_do_motorista.data.db.remote.enums.ExportFormat

@Composable
fun RelatorioScreen (
    viewModel: RelatorioViewModel,
    modifier: Modifier
){
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        //TOP BAR
        TopAppBar(
            title = {Text("Relatório")},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer

            )
        )
        //FITROS
        FiltrosRelatorio(
            uiState = uiState,
            onEvent = viewModel::handleEvent,
            modifier = Modifier.padding(16.dp)
        )
        //BOTÃO GERAR RELATÓRIO
        Button(
            onClick = {viewModel.handleEvent(RelatorioEvent.OnGerarRelatorio)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = !uiState.isLoading

        ) {
            if (uiState.isLoading){
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }else{
                Text("Gerar Relatório")
            }
        }
        //RESULTADOS
        if (uiState.relatorio != null) {
            ResultadosRelatorio(
                relatorio = uiState.relatorio!!,
                onExportPDF = { viewModel.handleEvent(RelatorioEvent.OnExportarRelatorio(ExportFormat.PDF)) },
                onExportExcel = { viewModel.handleEvent(RelatorioEvent.OnExportarRelatorio(ExportFormat.XLSX)) },
                isExporting = uiState.isExporting,
                exportFormat = uiState.exportFormat,
                modifier = Modifier.padding(16.dp)
            )
        }
        //LISTA DE DIÁRIAS
        if (uiState.relatorio?.diaria?.isNotEmpty() == true){
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.relatorio!!.diaria){diaria ->
                   DiariaCard(diaria)
                }
            }
        }
    }
    //MESSAGENS DE ERRO
    LaunchedEffect(uiState.erro) {
        uiState.erro?.let {
            SnackbatHelper.showSnackbar(it, isError = true)
            viewModel.handleEvent(RelatorioEvent.ClearMessages)
        }
    }
}
@Composable
fun FiltrosRelatorio(
    uiState: RelatorioUiState,
    onEvent: (RelatorioEvent) -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Data Início
            OutlinedTextField(
                value = uiState.dataInicio,
                onValueChange = { onEvent(RelatorioEvent.OnDataInicioChange(it)) },
                label = { Text("Data Início") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { /* Abrir DatePicker */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Selecionar data")
                    }
                }
            )

        }
    }
}

