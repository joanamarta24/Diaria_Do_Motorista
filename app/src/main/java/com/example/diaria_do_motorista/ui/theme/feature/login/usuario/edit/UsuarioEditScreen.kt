package com.example.diaria_do_motorista.ui.theme.feature.login.usuario.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioEditScreen(
    viewModel: UsuarioEditViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Coleta o estado de forma segura para o ciclo de vida do Android
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estados para controlar a abertura dos Dropdowns
    var transportadoraExpanded by remember { mutableStateOf(false) }
    var veiculoExpanded by remember { mutableStateOf(false) }

    // Efeito para mensagens e navegação
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            // SnackbarHelper.showSnackbar(it) // Certifique-Review se esse helper existe
            viewModel.handleEvent(UsuarioEditEvent.ClearMessages)
            onNavigateBack()
        }
        uiState.error?.let {
            // SnackbarHelper.showSnackbar(it, isError = true)
            viewModel.handleEvent(UsuarioEditEvent.ClearMessages)
        }
    }

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
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.handleEvent(UsuarioEditEvent.OnSave) }) {
                            Icon(Icons.Default.Save, contentDescription = "Salvar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // NOME
            item {
                OutlinedTextField(
                    value = uiState.nome,
                    onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnNomeChange(it)) },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.nomeError != null,
                    supportingText = uiState.nomeError?.let { { Text(it) } },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }

            // EMAIL
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

            // TELEFONE
            item {
                OutlinedTextField(
                    value = uiState.telefone,
                    onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnTelefoneChange(it)) },
                    label = { Text("Telefone") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.telefoneError != null,
                    supportingText = uiState.telefoneError?.let { { Text(it) } },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            // DATA DE NASCIMENTO
            item {
                OutlinedTextField(
                    value = uiState.dataNascimento,
                    onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnDataNascimentoChange(it)) },
                    label = { Text("Data de Nascimento") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("DD/MM/AAAA") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) }
                )
            }

            // TRANSPORTADORA (DROPDOWN)
            if (uiState.transportadoras.isNotEmpty()) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = transportadoraExpanded,
                        onExpandedChange = { transportadoraExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.transportadoras.find { it.id == uiState.transportadoraId }?.nome ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Transportadora") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = transportadoraExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = transportadoraExpanded,
                            onDismissRequest = { transportadoraExpanded = false }
                        ) {
                            uiState.transportadoras.forEach { transportadora ->
                                DropdownMenuItem(
                                    text = { Text(transportadora.nome) },
                                    onClick = {
                                        viewModel.handleEvent(UsuarioEditEvent.OnTransportadoraSelected(transportadora.id))
                                        transportadoraExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // VEICULO (DROPDOWN)
            if (uiState.veiculos.isNotEmpty() && uiState.transportadoraId.isNotBlank()) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = veiculoExpanded,
                        onExpandedChange = { veiculoExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = uiState.veiculos.find { it.matricula == uiState.matriculaVeiculo }?.let {
                                "${it.marca} ${it.modelo} - ${it.matricula}"
                            } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Veículo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = veiculoExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = veiculoExpanded,
                            onDismissRequest = { veiculoExpanded = false }
                        ) {
                            uiState.veiculos.forEach { veiculo ->
                                DropdownMenuItem(
                                    text = { Text("${veiculo.marca} ${veiculo.modelo} - ${veiculo.matricula}") },
                                    onClick = {
                                        viewModel.handleEvent(UsuarioEditEvent.OnVeiculoSelected(veiculo.matricula))
                                        veiculoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // SENHA
            if (!uiState.isEditMode || uiState.senha.isNotBlank()) {
                item {
                    OutlinedTextField(
                        value = uiState.senha,
                        onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnSenhaChange(it)) },
                        label = { Text(if (uiState.isEditMode) "Nova Senha (opcional)" else "Senha") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.senhaError != null,
                        supportingText = uiState.senhaError?.let { { Text(it) } },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            }

            // CONFIRMAR SENHA (dentro da LazyColumn agora)
            if (!uiState.isEditMode || uiState.confirmarSenha.isNotBlank()) {
                item {
                    OutlinedTextField(
                        value = uiState.confirmarSenha,
                        onValueChange = { viewModel.handleEvent(UsuarioEditEvent.OnConfirmarSenhaChange(it)) },
                        label = { Text("Confirmar Senha") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.senhaError != null,
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }
            }
        }
    }
}