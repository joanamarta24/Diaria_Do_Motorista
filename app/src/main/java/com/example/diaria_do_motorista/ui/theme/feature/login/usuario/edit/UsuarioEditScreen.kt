package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun UsuarioEditScreen(
    viewModel: UsuarioEditViewModel,
    onNavigateBack:() -> Unit,
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditMode) "Editar Motorista" else "Novo Motorista")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (uiState.isSaving){
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }else{
                       IconButton({viewModel.handleEvent(UsuarioEditEvent.OnSave) })
                       Icon(Icons.Default.Save, contentDescription = "Salvar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //NOME
            item {
                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnNomeChange(it)) },
                    label = {Text("Nome Completo")},
                    modifier = Modifier.nomeError != null,
                    supportingText = uiState.nomeError?.let{{Text(it) } },
                    singleLine = true,
                    leadingIcon = {Icon(Icons.Default.Person, contentDescription = null)}
                )
            }
            //EMAIL
            item {
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnEmailChange(it)) },
                    label = { Text("E-mail") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.emailError != null,
                    supportingText = uiState.emailError?.let { { Text(it) } },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

        //TELEFONE
            item {
                OutlinedTextField(
                    value = uiState.telefone,
                    onValueChange = {viewModel.handleEvent(UsuarioEditEvent.OnTelefoneChange(it))},
                    label = {Text("Telefone")},
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.telefoneError !=null,
                    supportingText = uiState.telefoneError?.let{ {Text(it)}},
                    singleLine = true,
                    leadingIcon = {Icon(Icons.Default.Phone, contentDescription = null)},
                    KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)

                )
            }
            //DATA DE NASCIMENTO
            item {
                OutlinedTextField(
                    value = uiState.dataNascimento,
                    onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnDataNascimentoChange(it)) },


                )
            }


    }
}