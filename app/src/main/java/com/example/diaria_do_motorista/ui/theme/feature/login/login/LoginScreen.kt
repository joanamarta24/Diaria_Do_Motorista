package com.example.diaria_do_motorista.ui.theme.feature.login.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.feature.login.LoginViewModel
import com.example.diaria_do_motorista.feature.login.states.LoginUiState
import com.example.diarias.feature.login.LoginEvent
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (Usuario) -> Unit,
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

    var isBiometricAvailable by rememberSaveable { mutableStateOf(false) }

    // Carregar credenciais salvas ao iniciar
    LaunchedEffect(Unit) {
        viewModel.loadSavedCredentials()
        checkBiometricAvailability(context) { available ->
            isBiometricAvailable = available
        }
    }

    // Observar eventos de navegação
    LaunchedEffect(Unit) {
        viewModel.loginEvent.collect { event ->
            when (event) {
                is LoginEvent.OnLoginSuccess -> {
                    keyboardController?.hide()
                    onLoginSuccess(event.usuario)
                }
                is LoginEvent.OnRegistrationSuccess -> {
                    keyboardController?.hide()
                    onLoginSuccess(event.usuario)
                }
                is LoginEvent.OnForgotPasswordClicked -> {
                    Toast.makeText(context, "Recuperação de senha em breve", Toast.LENGTH_SHORT).show()
                }
                is LoginEvent.OnError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    // Limpar erro quando o usuário digitar
    LaunchedEffect(formState.email, formState.password) {
        if (uiState is LoginUiState.Error) {
            viewModel.clearError()
        }
    }

    // Mostrar mensagem de erro (usando Snackbar ou Toast)
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Error -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_LONG).show()
                delay(2000)
                viewModel.clearError()
            }
            else -> {}
        }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
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

            // Card do formulário
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
                    // Indicador offline
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
                                    text = "Modo offline - dados locais disponíveis",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Email
                    OutlinedTextField(
                        value = formState.email,
                        onValueChange = { viewModel.handleEvent(LoginEvent.OnEmailChange(it)) },
                        label = { Text("E-mail") },
                        placeholder = { Text("seu@email.com") },
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
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error
                        )
                    )

                    // Senha
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
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(
                                onClick = { viewModel.handleEvent(LoginEvent.OnToggleShowPassword) }
                            ) {
                                Icon(
                                    if (screenState.showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (screenState.showPassword) "Ocultar senha" else "Mostrar senha"
                                )
                            }
                        },
                        visualTransformation = if (screenState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
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
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error
                        )
                    )

                    // Campos de cadastro
                    if (!isLoginMode) {
                        // Nome
                        OutlinedTextField(
                            value = formState.registrationName,
                            onValueChange = { viewModel.handleEvent(LoginEvent.OnRegistrationNameChange(it)) },
                            label = { Text("Nome Completo") },
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
                            },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Telefone
                        OutlinedTextField(
                            value = formState.registrationPhone,
                            onValueChange = { viewModel.handleEvent(LoginEvent.OnRegistrationPhoneChange(it)) },
                            label = { Text("Telefone") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = formState.phoneError != null,
                            supportingText = {
                                if (formState.phoneError != null) {
                                    Text(
                                        text = formState.phoneError!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        // Confirmar senha
                        OutlinedTextField(
                            value = formState.registrationConfirmPassword,
                            onValueChange = { viewModel.handleEvent(LoginEvent.OnRegistrationConfirmPasswordChange(it)) },
                            label = { Text("Confirmar Senha") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = formState.confirmPasswordError != null,
                            supportingText = {
                                if (formState.confirmPasswordError != null) {
                                    Text(
                                        text = formState.confirmPasswordError!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = if (screenState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    viewModel.handleEvent(LoginEvent.OnRegister)
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Lembrar-me + Biometria
                    if (isLoginMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = screenState.rememberMe,
                                    onCheckedChange = { viewModel.handleEvent(LoginEvent.OnToggleRememberMe) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = "Lembrar-me",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.clickable {
                                        viewModel.handleEvent(LoginEvent.OnToggleRememberMe)
                                    }
                                )
                            }

                            if (isBiometricAvailable && screenState.rememberMe) {
                                IconButton(
                                    onClick = { viewModel.handleEvent(LoginEvent.OnBiometricLogin) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Login com biometria",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { viewModel.handleEvent(LoginEvent.OnForgotPassword) }
                            ) {
                                Text(
                                    text = "Esqueceu a senha?",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botão principal
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            if (isLoginMode) {
                                viewModel.handleEvent(LoginEvent.OnLogin)
                            } else {
                                viewModel.handleEvent(LoginEvent.OnRegister)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = uiState !is LoginUiState.Loading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    ) {
                        when (uiState) {
                            is LoginUiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Aguarde...", color = MaterialTheme.colorScheme.onPrimary)
                            }
                            else -> {
                                Icon(
                                    imageVector = if (isLoginMode) Icons.Default.Login else Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isLoginMode) "Entrar" else "Cadastrar",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    // Alternar modo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLoginMode) "Não tem uma conta? " else "Já tem uma conta? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.handleEvent(LoginEvent.OnToggleLoginMode)
                            }
                        ) {
                            Text(
                                text = if (isLoginMode) "Cadastre-se" else "Faça login",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Rodapé
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Versão 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { /* Navegar para termos */ }) {
                    Text(
                        text = "Termos de Uso",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = " | ",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
                TextButton(onClick = { /* Navegar para privacidade */ }) {
                    Text(
                        text = "Política de Privacidade",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Overlay de loading
        if (uiState is LoginUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) { },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Carregando...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun checkBiometricAvailability(
    context: android.content.Context,
    onResult: (Boolean) -> Unit
) {
    try {
        val biometricManager = androidx.biometric.BiometricManager.from(context)
        when (biometricManager.canAuthenticate(
            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> onResult(true)
            else -> onResult(false)
        }
    } catch (e: Exception) {
        onResult(false)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLoginSuccess = {})
    }
}