package com.example.diaria_do_motorista.ui.theme.feature.login.relatorio

import ExportFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.diaria_do_motorista.data.db.domain.Diaria
import com.example.diaria_do_motorista.data.db.domain.RelatorioDiarias
import com.example.diaria_do_motorista.data.db.domain.Transportadora
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.domain.Veiculo

@OptIn(ExperimentalMaterial3Api::class)
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
            // Data Fim
            OutlinedTextField(
                value = uiState.dataFim,
                onValueChange = { onEvent(RelatorioEvent.OnDataFimChange(it)) },
                label = { Text("Data Fim") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { /* Abrir DatePicker */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Selecionar data")
                    }
                }
            )
            //TRANSPORTADORA
            FiltroDropdown(
                label = "Transportadora",
                items = uiState.transportadoraId,
                selectedItemId = uiState.transportadoraId,
                itemNome = { it.nome },
                onItemSelected = { onEvent(RelatorioEvent.OnTransportadoraSelected(it)) },
                onClear = { onEvent(RelatorioEvent.OnTransportadoraSelected(null)) }
            )
            //VEICULO
            if (uiState.veiculos.isNotEmpty()){
                FiltroDropdown(
                    label = "Veículo",
                    items = uiState.veiculos,
                    selectedItemId = uiState.matriculaVeiculo,
                    itemNome = { "${it.marca} ${it.modelo} - ${it.matricula}" },
                    onItemSelected = { onEvent(RelatorioEvent.OnVeiculoSelected(it)) },
                    onClear = { onEvent(RelatorioEvent.OnVeiculoSelected(null)) }
                )
            }
            //Motorista
            FiltroDropdown(
                label = "Motorista",
                items = uiState.motoristas,
                selectedItemId = uiState.motoristaId,
                itemNome = { it.nome },
                onItemSelected = { onEvent(RelatorioEvent.OnMotoristaSelected(it)) },
                onClear = { onEvent(RelatorioEvent.OnMotoristaSelected(null)) },
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun<T> FiltroDropdown(
    label: String,
    items: List<T>,
    selectedItemId: String?,
    itemNome: (T) -> String,
    onItemSelected: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = items.find {
        when(it){
            is Transportadora -> it.id == selectedItemId
           is Veiculo -> it.matricula == selectedItemId
            is Usuario -> it.id == selectedItemId
            else -> false

        }

    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {expanded = !expanded}
    ) {
        OutlinedTextField(
            value = selectedItem?.let(itemNome) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Row {
                    if (selectedItemId != null) {
                        IconButton(onClick = onClear) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemNome(item)) },
                    onClick = {
                        val id = when(item) {
                            is Transportadora -> item.id
                            is Veiculo -> item.matricula
                            is Usuario -> item.id
                            else -> null
                        }
                        onItemSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun ResultadosRelatorio(
    relatorio: RelatorioDiarias,
    onExportPDF: () -> Unit,
    onExportExcel: () -> Unit,
    isExporting: Boolean,
    exportFormat: ExportFormat?,
    modifier: Modifier = Modifier
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Text(
                text = "Resumo do Relatório",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) { ResultadoItem(
                label = "Total Diárias",
                value = relatorio.totalDiarias.toString(),
                icon = Icons.Default.Assignment
            )
                ResultadoItem(
                    label = "Total Horas",
                    value =String.format("%.2f h",relatorio.totalHoras),
                    icon = Icons.Default.Schedule
                )
                ResultadoItem(
                    label ="Total KM",
                    value = String.format("%.0f km", relatorio.totalKm),
                    icon = Icons.Default.DirectionsCar
                )
                ResultadoItem(
                    label = "Total Portagens",
                    value = String.format("€ %.2f", relatorio.totalPortagens),
                    icon = Icons.Default.AttachMoney
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onExportPDF,
                    enabled = !isExporting
                ) {
                    if (isExporting && exportFormat == ExportFormat.PDF){
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }else{
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = "Exportar PDF",
                        tint = MaterialTheme.colorScheme.error

                    )
                }
            }
                IconButton(
                    onClick = onExportExcel,
                    enabled = !isExporting
                ) {
                    if (isExporting && exportFormat == ExportFormat.XLSX){
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }else{
                        Icon(
                            Icons.Default.TableChart,
                            contentDescription = "Exportar Excel",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun ResultadoItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}
@Composable
fun DiariaCard(
    diaria: Diaria,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = diaria.dataDiaria,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = diaria.destino,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${diaria.horaInicio} - ${diaria.horaFim ?: "Em andamento"}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${diaria.kmInicio} km - ${diaria.kmFim?.let { "$it km" } ?: "---"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (diaria.totalPortagens != null) {
                Text(
                    text = "Portagens: € ${String.format("%.2f", diaria.totalPortagens)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (diaria.observacoes != null) {
                Text(
                    text = diaria.observacoes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}






