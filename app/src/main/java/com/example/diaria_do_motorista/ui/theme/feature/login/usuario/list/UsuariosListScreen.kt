package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.list

import android.app.Person
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosListScreen(
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
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        //BARRA DE PESQUISA
        SearchBar (
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
    // Diálogo de confirmação de exclusão
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.onDeleteConfirmationChanged(false) },
            title = { Text("Confirmar exclusão") },
            text = { Text("Tem certeza que deseja excluir este motorista?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.deleteMotorista()
                            viewModel.onDeleteConfirmationChanged(false)
                        }
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDeleteConfirmationChanged(false) }) {
                    Text("Cancelar")
                }
            }
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Motoristas") },
                actions = {
                    IconButton(onClick = { onNavigateToEdit("new") }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // resto da tela aqui
        }
    }
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
){
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Pesquisar motoristas...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpar pesquisa")
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun MotoristaCard(
    Motorista: Int,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column (
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = Motorista.nome,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Motorista.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = Motorista.telefone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (Motorista.matriculaVeiculo != null){
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Veiculo: ${Motorista.matriculaVeiculo} ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
@Composable
fun EmptyState(
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}