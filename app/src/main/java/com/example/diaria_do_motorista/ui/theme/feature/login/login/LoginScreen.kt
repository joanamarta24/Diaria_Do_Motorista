package com.example.diaria_do_motorista.ui.theme.feature.login.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
){
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
        when (uiState){
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
                .
        )
}



