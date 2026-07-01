package com.example.diaria_do_motorista.ui.theme.feature.login.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.ui.theme.feature.login.loginsealed.LoginUiState
import com.example.diarias.feature.login.LoginEvent
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginSree(
    onLoginSuccess: (Usuario) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    val formState = screenState.formState
    val uiState = screenState.uiState
    val isLoginMode = formState.isLoginMode
    val isOfflineMode = screenState.isOfflineMode

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    //ESTADO LOCAIS PARA ANIMAÇÔES
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isBiometricAvailable by remember { mutableStateOf(false) }

    //CARREGAR CREDENCIAIS SALVAS AO INICIAR
    LaunchedEffect(Unit) {
        viewModel.loadSavedCredentials()
        // Verificar disponibilidade de biometria
        checkBiometricAvailability(context) { available ->
            isBiometricAvailable = available
        }
    }
    //OBSERVAR EVENTOS DE NAVEGAÇÃO
    LaunchedEffect(Unit) {
        viewModel.loginEvent.collect { event ->
            when (event) {
                is LoginEvent.OnLoginSuccess -> {
                    keyboardController?.hide()
                    onLoginSuccess
                }

                is LoginEvent.OnRegistrationSuccess -> {
                    keyboardController?.hide()
                    onLoginSuccess(event.usuario)
                }

                is LoginEvent.OnForgotPasswordClicked -> {
                    // Navegar para a tela de recuperação de senha
                    Toast.makeText(context, "Recuperação de senha", Toast.LENGTH_SHORT).show()
                }

                else -> {}

            }

        }
    }
    //LIMPAR ERRO QUANDO O USUÁRIO DIGITAR
    LaunchedEffect(formState.email, formState.password) {
        if (uiState is LoginUiState.Error) {
            viewModel.clearError()
        }
    }
    //MOSTRAR MENSAGEM DE ERRO QUANDO HOUVER
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Error -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
                delay(2000)
                viewModel.clearError()
            }

            else -> {}
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center
            ) {
                //LOGO OU ICONE DO APP
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Logo",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Diária do Motorista",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isLoginMode) "Faça login para continuar" else "Crie sua conta",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                //CARD DO FORMULARIO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        //INDICADOR DE MODO OFFLINE
                        if (isOfflineMode) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                tonalElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WifiOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = "Modo offline - Você pode acessar dados salvos localmente",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                        //CAMPO DE EMAIL
                        OutlinedTextField(
                            value = formState.email,
                            onValueChange = { viewModel.handleEvent(LoginEvent.OnEmailChange(it)) },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = formState.emailError != null,
                            supportingText = {
                                if (formState.emailError != null) {
                                    Text(
                                        text = formState.emailError!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = if (isLoginMode) ImeAction.Next else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                focusedBorderColor = MaterialTheme.colorScheme.error,
                                errorLabelColor = MaterialTheme.colorScheme.error
                            )
                        )
                        //CAMPO DE SENHA
                        OutlinedTextField(
                            value = formState.password,
                            onValueChange = { viewModel.handleEvent(LoginEvent.OnPasswordChange(it)) },
                            label = { Text("Senha") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = formState.passwordError != null,
                            supportingText = {
                                if (formState.passwordError != null) {
                                    Text(
                                        text = formState.passwordError!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.handleEvent(LoginEvent.onToggleShowPassword) }
                                ) {
                                    Icon(
                                        if (screenState.showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (screenState.showPassword) "Ocultar senha" else "Mostrar senha"
                                    )
                                }
                            },
                            visualTransformation = if (screenState.showPassword) visualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = keyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next

                            ),
                            keyboardOptions = keyboardOptions(
                                onDone = {
                                    if (isLoginMode) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        viewModel.handleEvent(LoginEvent.OnLogin)
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                errorLabelColor = MaterialTheme.colorScheme.error,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            )

                        )
                       //CAMPOS ADICIONAIS PARA CADASTRO
                        if (isLoginMode){
                            //NOME COMPLETO
                            OutlinedTextField(
                                value = formState.registrationName,
                                onValueChange = { viewModel.handleEvent(LoginEvent.OnRegistrationNameChange(it)) },
                                label = {Text("Nome Completo")},
                                modifier = Modifier.fillMaxWidth(),
                                isError = formState.nameError != null,
                                supportingText = {
                                    if (formState.nameError != null) {
                                        Text(
                                            text = formState.nameError!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                }
            }
        }
    }
}




