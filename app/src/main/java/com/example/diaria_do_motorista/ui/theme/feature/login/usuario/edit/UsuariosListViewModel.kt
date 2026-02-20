package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.diaria_do_motorista.ui.theme.feature.login.usuario.list.UsuariosListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosListViewModel (
    viewModel: UsuariosListViewModel,
    onNavigateToEdit:(String) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
){
    val  uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        TopAppBar(
            title = { Text("Motorista") },
            actions = {
                IconButton(onClick = {onNavigateToEdit ("new") }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Motorista")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

    }
}