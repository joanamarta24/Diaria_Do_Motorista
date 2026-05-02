package com.example.diaria_do_motorista.ui.theme.feature.login.login

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diaria_do_motorista.network.NetworkMonitor
import com.example.diaria_do_motorista.data.db.repository.AuthRepository
import com.example.diaria_do_motorista.ui.theme.feature.login.loginsealed.LoginUiState
import com.example.diaria_do_motorista.util.DispatchersProvider
import com.example.diarias.feature.login.LoginEvent
import com.seuapp.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor,
    private val dispatchers: DispatchersProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _screenState = MutableStateFlow(LoginScreenState())
    val screenState: StateFlow<LoginScreenState> = _screenState.asStateFlow()

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent.asSharedFlow()

    private var isNetworkAvailable = false

    init {
        monitorNetworkState()
        loadSavedCredentials()
    }

    private fun monitorNetworkState() {
        viewModelScope.launch {
            networkMonitor.isConnected.collect { isConnected ->
                isNetworkAvailable = isConnected
                _screenState.update { it.copy(isOfflineMode = !isConnected) }
            }
        }
    }

    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> onEmailChange(event.email)
            is LoginEvent.OnPasswordChange -> onPasswordChange(event.password)
            is LoginEvent.OnToggleLoginMode -> onToggleLoginMode()
            is LoginEvent.OnRegistrationNameChange -> onRegistrationNameChange(event.name)
            is LoginEvent.OnRegistrationPhoneChange -> onRegistrationPhoneChange(event.phone)
            is LoginEvent.OnRegistrationConfirmPasswordChange -> onRegistrationConfirmPasswordChange(event.confirmPassword)
            is LoginEvent.OnToggleRememberMe -> onToggleRememberMe()
            is LoginEvent.OnToggleShowPassword -> onToggleShowPassword()
            LoginEvent.OnLogin -> onLogin()
            LoginEvent.OnRegister -> onRegister()
            LoginEvent.OnForgotPassword -> onForgotPassword()
            LoginEvent.ClearErrors -> clearError()
            LoginEvent.ResetForm -> resetState()
            LoginEvent.OnBiometricLogin -> onBiometricLogin()
            LoginEvent.OnBiometricSuccess -> onBiometricSuccess()
            is LoginEvent.OnBiometricError -> onBiometricError(event.errorMessage)
            else -> {} // Para outros eventos não tratados
        }
    }

    private fun onEmailChange(email: String) {
        val newFormState = _screenState.value.formState.copy(
            email = email,
            emailError = validateEmail(email)
        )
        updateFormState(newFormState)
        clearUiError()
    }

    private fun onPasswordChange(password: String) {
        val newFormState = _screenState.value.formState.copy(
            password = password,
            passwordError = validatePassword(password)
        )
        updateFormState(newFormState)
        clearUiError()
    }

    private fun onToggleLoginMode() {
        val currentFormState = _screenState.value.formState
        val isCurrentlyLogin = currentFormState.isLoginMode
        val willBeLogin = !isCurrentlyLogin

        _screenState.update { state ->
            state.copy(
                formState = state.formState.copy(
                    isLoginMode = willBeLogin,
                    email = currentFormState.email,
                    password = currentFormState.password,
                    registrationName = if (willBeLogin) "" else currentFormState.registrationName,
                    registrationPhone = if (willBeLogin) "" else currentFormState.registrationPhone,
                    registrationConfirmPassword = if (willBeLogin) "" else currentFormState.registrationConfirmPassword,
                    emailError = null,
                    passwordError = null,
                    nameError = null,
                    phoneError = null,
                    confirmPasswordError = null
                ),
                uiState = LoginUiState.Idle
            )
        }
        validateForm()
    }

    private fun onRegistrationNameChange(name: String) {
        val newFormState = _screenState.value.formState.copy(
            registrationName = name,
            nameError = validateName(name)
        )
        updateFormState(newFormState)
    }

    private fun onRegistrationPhoneChange(phone: String) {
        val newFormState = _screenState.value.formState.copy(
            registrationPhone = phone,
            phoneError = validatePhone(phone)
        )
        updateFormState(newFormState)
    }

    private fun onRegistrationConfirmPasswordChange(confirmPassword: String) {
        val currentPassword = _screenState.value.formState.password
        val newFormState = _screenState.value.formState.copy(
            registrationConfirmPassword = confirmPassword,
            confirmPasswordError = validateConfirmPassword(currentPassword, confirmPassword)
        )
        updateFormState(newFormState)
    }

    private fun onToggleRememberMe() {
        _screenState.update { state ->
            state.copy(rememberMe = !state.rememberMe)
        }
    }

    private fun onToggleShowPassword() {
        _screenState.update { state ->
            state.copy(showPassword = !state.showPassword)
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "E-mail é obrigatório"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-mail inválido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Senha é obrigatória"
            password.length < 6 -> "Senha deve ter pelo menos 6 caracteres"
            else -> null
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Nome é obrigatório"
            name.length < 3 -> "Nome deve ter pelo menos 3 caracteres"
            else -> null
        }
    }

    private fun validatePhone(phone: String): String? {
        val cleanPhone = phone.replace("[^0-9]".toRegex(), "")
        return when {
            phone.isBlank() -> "Telefone é obrigatório"
            cleanPhone.length < 10 -> "Telefone inválido (mínimo 10 dígitos)"
            cleanPhone.length > 11 -> "Telefone inválido (máximo 11 dígitos)"
            else -> null
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Confirmação de senha é obrigatória"
            password != confirmPassword -> "As senhas não coincidem"
            else -> null
        }
    }

    private fun validateForm() {
        val formState = _screenState.value.formState

        val isValid = if (formState.isLoginMode) {
            formState.emailError == null &&
                    formState.passwordError == null &&
                    formState.email.isNotBlank() &&
                    formState.password.isNotBlank()
        } else {
            formState.emailError == null &&
                    formState.passwordError == null &&
                    formState.nameError == null &&
                    formState.phoneError == null &&
                    formState.confirmPasswordError == null &&
                    formState.email.isNotBlank() &&
                    formState.password.isNotBlank() &&
                    formState.registrationName.isNotBlank() &&
                    formState.registrationPhone.isNotBlank() &&
                    formState.registrationConfirmPassword.isNotBlank()
        }

        updateFormState(formState.copy(isFormValid = isValid))
    }

    private fun updateFormState(formState: LoginFormState) {
        _screenState.update { state ->
            state.copy(formState = formState)
        }
    }

    private fun onLogin() {
        validateForm()

        if (!_screenState.value.formState.isFormValid) {
            _screenState.update { state ->
                state.copy(
                    uiState = LoginUiState.Error("Por favor, preencha todos os campos corretamente")
                )
            }
            return
        }

        if (!isNetworkAvailable) {
            _screenState.update { state ->
                state.copy(
                    uiState = LoginUiState.Error("Sem conexão com a internet. Verifique sua rede.")
                )
            }
            return
        }

        viewModelScope.launch {
            _screenState.update { state ->
                state.copy(uiState = LoginUiState.Loading)
            }

            val email = _screenState.value.formState.email
            val password = _screenState.value.formState.password

            val result = withContext(dispatchers.io) {
                authRepository.login(email, password)
            }

            result.fold(
                onSuccess = { usuario ->
                    _screenState.update { state ->
                        state.copy(uiState = LoginUiState.Success(usuario))
                    }

                    if (_screenState.value.rememberMe) {
                        saveUserCredentials(email, password)
                    } else {
                        clearUserCredentials()
                    }

                    _loginEvent.emit(LoginEvent.OnLoginSuccess(usuario))
                },
                onFailure = { error ->
                    _screenState.update { state ->
                        state.copy(
                            uiState = LoginUiState.Error(
                                error.message ?: "Erro ao fazer login. Tente novamente."
                            )
                        )
                    }
                }
            )
        }
    }

    private fun onRegister() {
        validateForm()

        if (!_screenState.value.formState.isFormValid) {
            _screenState.update { state ->
                state.copy(
                    uiState = LoginUiState.Error("Por favor, preencha todos os campos corretamente")
                )
            }
            return
        }

        if (!isNetworkAvailable) {
            _screenState.update { state ->
                state.copy(
                    uiState = LoginUiState.Error("Sem conexão com a internet. Verifique sua rede.")
                )
            }
            return
        }

        viewModelScope.launch {
            _screenState.update { state ->
                state.copy(uiState = LoginUiState.Loading)
            }

            val formState = _screenState.value.formState

            // Verificar se é o primeiro usuário do sistema
            val isFirstUser = checkIfFirstUser()

            val result = if (isFirstUser) {
                withContext(dispatchers.io) {
                    authRepository.cadastrarAdministrador(
                        nome = formState.registrationName,
                        email = formState.email,
                        telefone = formState.registrationPhone,
                        senha = formState.password
                    )
                }
            } else {
                withContext(dispatchers.io) {
                    authRepository.cadastrarMotorista(
                        nome = formState.registrationName,
                        email = formState.email,
                        telefone = formState.registrationPhone,
                        dataNascimento = "", // Será preenchido depois
                        matriculaVeiculo = null,
                        transportadoraId = null,
                        senha = formState.password
                    )
                }
            }

            result.fold(
                onSuccess = { usuario ->
                    _screenState.update { state ->
                        state.copy(uiState = LoginUiState.Success(usuario))
                    }

                    _loginEvent.emit(LoginEvent.OnRegistrationSuccess(usuario))
                },
                onFailure = { error ->
                    _screenState.update { state ->
                        state.copy(
                            uiState = LoginUiState.Error(
                                error.message ?: "Erro ao realizar cadastro. Tente novamente."
                            )
                        )
                    }
                }
            )
        }
    }

    private suspend fun checkIfFirstUser(): Boolean {
        return withContext(dispatchers.io) {
            // Verificar se já existe algum usuário cadastrado
            val usuarios = authRepository.getAllUsuarios()
            usuarios.isEmpty()
        }
    }

    private fun onForgotPassword() {
        viewModelScope.launch {
            _loginEvent.emit(LoginEvent.OnForgotPasswordClicked)
        }
    }

    private suspend fun saveUserCredentials(email: String, password: String) {
        withContext(dispatchers.io) {
            try {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                prefs.edit().apply {
                    putString("saved_email", email)
                    putString("saved_password", password)
                    putBoolean("remember_me", true)
                    apply()
                }
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }

    private suspend fun clearUserCredentials() {
        withContext(dispatchers.io) {
            try {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                prefs.edit().apply {
                    remove("saved_email")
                    remove("saved_password")
                    putBoolean("remember_me", false)
                    apply()
                }
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }

    fun loadSavedCredentials() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                try {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    val rememberMe = prefs.getBoolean("remember_me", false)
                    if (rememberMe) {
                        val savedEmail = prefs.getString("saved_email", "") ?: ""
                        val savedPassword = prefs.getString("saved_password", "") ?: ""

                        if (savedEmail.isNotBlank() && savedPassword.isNotBlank()) {
                            _screenState.update { state ->
                                state.copy(
                                    rememberMe = true,
                                    formState = state.formState.copy(
                                        email = savedEmail,
                                        password = savedPassword
                                    )
                                )
                            }
                            validateForm()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun clearError() {
        _screenState.update { state ->
            if (state.uiState is LoginUiState.Error) {
                state.copy(uiState = LoginUiState.Idle)
            } else {
                state
            }
        }
    }

    private fun clearUiError() {
        if (_screenState.value.uiState is LoginUiState.Error) {
            _screenState.update { it.copy(uiState = LoginUiState.Idle) }
        }
    }

    fun resetState() {
        _screenState.update { state ->
            state.copy(
                formState = LoginFormState(),
                uiState = LoginUiState.Idle,
                rememberMe = false,
                showPassword = false,
                isOfflineMode = state.isOfflineMode
            )
        }
    }

    private fun onBiometricLogin() {
        viewModelScope.launch {
            _loginEvent.emit(LoginEvent.OnBiometricLogin)
        }
    }

    private fun onBiometricSuccess() {
        viewModelScope.launch {
            // Carregar credenciais e fazer login automático
            loadSavedCredentials()
            val formState = _screenState.value.formState
            if (formState.email.isNotBlank() && formState.password.isNotBlank()) {
                onLogin()
            }
        }
    }

    private fun onBiometricError(errorMessage: String) {
        viewModelScope.launch {
            _screenState.update { state ->
                state.copy(
                    uiState = LoginUiState.Error(errorMessage)
                )
            }
        }
    }
}