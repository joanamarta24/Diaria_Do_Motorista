package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.list

import android.app.Person
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.util.query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosListViewModel(
    viewModel: UsuariosListViewModel,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar com titulo e botão de adicionar
        TopAppBar(
            title = { Text("Motoristas") },
            actions = {
                IconButton(onClick = { onNavigateToEdit("new") }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Motorista")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                ConteinerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        //BARRA DE PESQUISA
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.filteredMotoristas.isEmpty()) {
                    item {
                        EmptyState(
                            message = if (uiState.searchQuery.isNotBlank())
                                "Nenhum motorista encontrado para '${uiState.searchQuery}'"
                            else
                                "Nenhum motorista cadastrado",
                            icon = Icons.Default.Person
                        )
                    }
                } else {
                    items(uiState.filteredMotoristas) { motorista ->
                        MotoristaCard(
                            motorista = motorista,
                            onClick = { onNavigateToDetails(motorista.id) },
                            onEdit = { onNavigateToEdit(motorista.id) },
                            onDelete = {
                                viewModel.onMotoristaSelected(motorista)
                                viewModel.onDeleteConfirmationChanged(true)
                            }
                        )
                    }
                }
            }
        }
    }


@Composable
fun EmptyState(message: String, icon: Person) {
    TODO("Not yet implemented")
}

@Composable
fun MotoristaCard(
    motorista: Int,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> onDeleteConfirmationChanged
) {
    TODO("Not yet implemented")
}