package com.example.diaria_do_motorista.feature.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diaria_do_motorista.network.NetworkMonitor
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.preferences.SecurePreferences
import com.example.diaria_do_motorista.data.db.repository.AuthRepository
import com.example.diaria_do_motorista.feature.login.events.LoginEvent
import com.example.diaria_do_motorista.feature.login.states.LoginFormState
import com.example.diaria_do_motorista.feature.login.states.LoginScreenState
import com.example.diaria_do_motorista.feature.login.states.LoginUiState
import com.example.diaria_do_motorista.util.DispatchersProvider
import com.example.diaria_do_motorista.util.biometric.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
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
    private val securePreferences: SecurePreferences,
    private val biometricHelper: BiometricHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _screenState = MutableStateFlow(LoginScreenState())
    val screenState: StateFlow<LoginScreenState> = _screenState.asStateFlow()

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent.asSharedFlow()

    private var isNetworkAvailable = false
    private var networkMonitorJob: Job? = null
    private var biometricCheckJob: Job? = null
    private var loadCredentialsJob: Job? = null

    init {
        monitorNetworkState()
        loadSavedCredentials()
        checkBiometricAvailability()
    }

    // ========== INICIALIZAÇÃO ==========

    private fun monitorNetworkState() {
        networkMonitorJob = viewModelScope.launch {
            networkMonitor.isConnected.collect { isConnected ->
                isNetworkAvailable = isConnected
                _screenState.update {
                    it.copy(
                        isOfflineMode = !isConnected,
                        uiState = if (!isConnected && _screenState.value.uiState is LoginUiState.Loading) {
                            LoginUiState.Error("Sem conexão com a internet")
                        } else {
                            _screenState.value.uiState
                        }
                    )
                }
            }
        }
    }

    private fun loadSavedCredentials() {
        loadCredentialsJob = viewModelScope.launch {
            withContext(dispatchers.io) {
                val credentials = securePreferences.getCredentials()
                if (credentials != null) {
                    _screenState.update { state ->
                        state.copy(
                            rememberMe = true,
                            formState = state.formState.copy(
                                email = credentials.first,
                                password = credentials.second
                            )
                        )
                    }
                    validateForm()
                }
            }
        }
    }

    private fun checkBiometricAvailability() {
        biometricCheckJob = viewModelScope.launch {
            try {
                if (!biometricHelper.isBiometricSupportedByOS()) {
                    _screenState.update {
                        it.copy(
                            biometricAvailable = false,
                            biometricErrorMessage = "Seu dispositivo não suporta biometria"
                        )
                    }
                    return@launch
                }

                val availability = withContext(dispatchers.main) {
                    biometricHelper.isBiometricAvailable()
                }

                _screenState.update { state ->
                    state.copy(
                        biometricAvailable = availability.isAvailable,
                        biometricIsEnrolled = availability.isEnrolled,
                        biometricHasHardware = availability.hasHardware,
                        biometricErrorMessage = availability.errorMessage
                    )
                }

            } catch (e: Exception) {
                _screenState.update {
                    it.copy(
                        biometricAvailable = false,
                        biometricErrorMessage = "Erro ao verificar biometria: ${e.message}"
                    )
                }
            }
        }
    }

    // ========== HANDLE EVENTS ==========

    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnEmailChange -> onEmailChange(event.email)
            is LoginEvent.OnPasswordChange -> onPasswordChange(event.password)
            is LoginEvent.OnRegistrationNameChange -> onRegistrationNameChange(event.name)
            is LoginEvent.OnRegistrationPhoneChange -> onRegistrationPhoneChange(event.phone)
            is LoginEvent.OnRegistrationConfirmPasswordChange -> onRegistrationConfirmPasswordChange(event.confirmPassword)
            is LoginEvent.OnRegistrationDateOfBirthChange -> onRegistrationDateOfBirthChange(event.dateOfBirth)
            is LoginEvent.OnRegistrationVehiclePlateChange -> onRegistrationVehiclePlateChange(event.plate)

            LoginEvent.OnToggleLoginMode -> onToggleLoginMode()
            LoginEvent.OnToggleRememberMe -> onToggleRememberMe()
            LoginEvent.OnToggleShowPassword -> onToggleShowPassword()

            LoginEvent.OnLogin -> onLogin()
            LoginEvent.OnRegister -> onRegister()
            LoginEvent.OnForgotPassword -> onForgotPassword()
            LoginEvent.ClearErrors -> clearError()
            LoginEvent.ResetForm -> resetState()
            LoginEvent.OnBiometricLogin -> onBiometricLogin()

            is LoginEvent.OnBiometricError -> onBiometricError(event.errorMessage)
            is LoginEvent.OnError -> handleError(event.message)
            else -> {}
        }
    }

    // ========== HANDLERS DE EVENTOS DE UI ==========

    private fun onEmailChange(email: String) {
        val error = validateEmail(email)
        updateFormField(
            email = email,
            emailError = error
        )
        validateForm()
        clearErrorIfNeeded()
    }

    private fun onPasswordChange(password: String) {
        val error = validatePassword(password)
        updateFormField(
            password = password,
            passwordError = error
        )
        validateForm()
        clearErrorIfNeeded()
    }

    private fun onRegistrationNameChange(name: String) {
        val error = validateName(name)
        updateFormField(
            registrationName = name,
            nameError = error
        )
        validateForm()
    }

    private fun onRegistrationPhoneChange(phone: String) {
        val error = validatePhone(phone)
        updateFormField(
            registrationPhone = phone,
            phoneError = error
        )
        validateForm()
    }

    private fun onRegistrationConfirmPasswordChange(confirmPassword: String) {
        val currentPassword = _screenState.value.formState.password
        val error = validateConfirmPassword(currentPassword, confirmPassword)
        updateFormField(
            registrationConfirmPassword = confirmPassword,
            confirmPasswordError = error
        )
        validateForm()
    }

    private fun onRegistrationDateOfBirthChange(dateOfBirth: String) {
        val error = validateDateOfBirth(dateOfBirth)
        updateFormField(
            registrationDateOfBirth = dateOfBirth,
            dateOfBirthError = error
        )
        validateForm()
    }

    private fun onRegistrationVehiclePlateChange(plate: String) {
        val error = validateVehiclePlate(plate)
        updateFormField(
            registrationVehiclePlate = plate,
            vehiclePlateError = error
        )
        validateForm()
    }

    private fun onToggleLoginMode() {
        _screenState.update { state ->
            val isLogin = state.formState.isLoginMode
            state.copy(
                formState = state.formState.copy(
                    isLoginMode = !isLogin,
                    registrationName = "",
                    registrationPhone = "",
                    registrationConfirmPassword = "",
                    registrationDateOfBirth = "",
                    registrationVehiclePlate = "",
                    nameError = null,
                    phoneError = null,
                    confirmPasswordError = null,
                    dateOfBirthError = null,
                    vehiclePlateError = null
                ),
                uiState = LoginUiState.Idle
            )
        }
        validateForm()
    }

    private fun onToggleRememberMe() {
        _screenState.update { state ->
            state.copy(rememberMe = !state.rememberMe)
        }
        if (!_screenState.value.rememberMe) {
            viewModelScope.launch {
                clearUserCredentials()
            }
        }
    }

    private fun onToggleShowPassword() {
        _screenState.update { state ->
            state.copy(showPassword = !state.showPassword)
        }
    }

    // ========== AÇÕES PRINCIPAIS ==========

    private fun onLogin() {
        val formState = _screenState.value.formState
        val uiState = _screenState.value.uiState

        if (uiState is LoginUiState.Loading) return

        validateForm()
        if (!formState.isFormValid) {
            setError("Por favor, preencha todos os campos corretamente")
            return
        }

        if (!isNetworkAvailable) {
            setError("Sem conexão com a internet. Verifique sua rede.")
            return
        }

        viewModelScope.launch {
            setLoading(true)

            val result = withContext(dispatchers.io) {
                authRepository.login(
                    email = formState.email.trim(),
                    password = formState.password
                )
            }

            result.fold(
                onSuccess = { usuario ->
                    handleLoginSuccess(usuario)
                },
                onFailure = { error ->
                    handleLoginError(error)
                }
            )
        }
    }

    private fun onRegister() {
        val formState = _screenState.value.formState
        val uiState = _screenState.value.uiState

        if (uiState is LoginUiState.Loading) return

        validateForm()
        if (!formState.isFormValid) {
            setError("Por favor, preencha todos os campos corretamente")
            return
        }

        if (!isNetworkAvailable) {
            setError("Sem conexão com a internet. Verifique sua rede.")
            return
        }

        viewModelScope.launch {
            setLoading(true)

            val isFirstUser = withContext(dispatchers.io) {
                authRepository.isFirstUser()
            }

            val result = if (isFirstUser) {
                withContext(dispatchers.io) {
                    authRepository.cadastrarAdministrador(
                        nome = formState.registrationName.trim(),
                        email = formState.email.trim(),
                        telefone = formState.registrationPhone.trim(),
                        dataNascimento = formState.registrationDateOfBirth,
                        senha = formState.password
                    )
                }
            } else {
                withContext(dispatchers.io) {
                    authRepository.cadastrarMotorista(
                        nome = formState.registrationName.trim(),
                        email = formState.email.trim(),
                        telefone = formState.registrationPhone.trim(),
                        dataNascimento = formState.registrationDateOfBirth,
                        matriculaVeiculo = formState.registrationVehiclePlate.trim().ifEmpty { null },
                        transportadoraId = null,
                        senha = formState.password
                    )
                }
            }

            result.fold(
                onSuccess = { usuario ->
                    handleRegistrationSuccess(usuario)
                },
                onFailure = { error ->
                    handleRegistrationError(error)
                }
            )
        }
    }

    private fun onForgotPassword() {
        val email = _screenState.value.formState.email
        if (email.isBlank()) {
            setError("Digite seu e-mail para recuperar a senha")
            return
        }

        viewModelScope.launch {
            _loginEvent.emit(LoginEvent.OnForgotPasswordClicked(email))
        }
    }

    private fun onBiometricLogin() {
        if (!_screenState.value.biometricAvailable) {
            setError("Biometria não disponível. Use e-mail e senha.")
            return
        }

        viewModelScope.launch {
            _loginEvent.emit(LoginEvent.OnBiometricLogin)
        }
    }

    private fun onBiometricLoginSuccess() {
        viewModelScope.launch {
            try {
                val credentials = withContext(dispatchers.io) {
                    securePreferences.getCredentials()
                }

                if (credentials == null) {
                    setError("Credenciais não encontradas. Faça login manualmente.")
                    return@launch
                }

                val (email, password) = credentials

                _screenState.update { state ->
                    state.copy(
                        formState = state.formState.copy(
                            email = email,
                            password = password
                        )
                    )
                }

                validateForm()
                if (_screenState.value.formState.isFormValid) {
                    onLogin()
                } else {
                    setError("Credenciais inválidas. Faça login manualmente.")
                }

            } catch (e: Exception) {
                setError("Erro ao fazer login biométrico: ${e.message}")
            }
        }
    }

    private fun onBiometricError(errorMessage: String) {
        val friendlyMessage = when {
            errorMessage.contains("cancel", ignoreCase = true) -> "Autenticação cancelada"
            errorMessage.contains("lockout", ignoreCase = true) -> "Muitas tentativas. Aguarde."
            errorMessage.contains("failed", ignoreCase = true) -> "Falha na autenticação. Tente novamente."
            else -> errorMessage
        }
        setError(friendlyMessage)
    }

    private fun handleError(message: String) {
        setError(message)
    }

    // ========== HANDLERS DE RESULTADOS ==========

    private suspend fun handleLoginSuccess(usuario: Usuario) {
        setLoading(false)
        _screenState.update { state ->
            state.copy(uiState = LoginUiState.Success(usuario))
        }

        if (_screenState.value.rememberMe) {
            saveUserCredentials(
                email = _screenState.value.formState.email,
                password = _screenState.value.formState.password
            )
        }

        _loginEvent.emit(LoginEvent.OnLoginSuccess(usuario))
    }

    private suspend fun handleLoginError(error: Throwable) {
        setLoading(false)
        val message = when {
            error.message?.contains("401") == true -> "E-mail ou senha incorretos"
            error.message?.contains("404") == true -> "Usuário não encontrado"
            else -> error.message ?: "Erro ao fazer login. Tente novamente."
        }
        setError(message)
    }

    private suspend fun handleRegistrationSuccess(usuario: Usuario) {
        setLoading(false)
        _screenState.update { state ->
            state.copy(uiState = LoginUiState.Success(usuario))
        }
        _loginEvent.emit(LoginEvent.OnRegistrationSuccess(usuario))
    }

    private suspend fun handleRegistrationError(error: Throwable) {
        setLoading(false)
        val message = when {
            error.message?.contains("email already exists") == true -> "Este e-mail já está cadastrado"
            error.message?.contains("unique constraint") == true -> "Dados já cadastrados"
            else -> error.message ?: "Erro ao realizar cadastro. Tente novamente."
        }
        setError(message)
    }

    // ========== VALIDAÇÕES ==========

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "E-mail é obrigatório"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-mail inválido"
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
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
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

    private fun validateDateOfBirth(dateOfBirth: String): String? {
        return when {
            dateOfBirth.isBlank() -> "Data de nascimento é obrigatória"
            !isValidDate(dateOfBirth) -> "Data inválida (formato: DD/MM/AAAA)"
            else -> null
        }
    }

    private fun validateVehiclePlate(plate: String): String? {
        if (plate.isBlank()) return null
        val cleanPlate = plate.uppercase().replace(Regex("[^A-Z0-9]"), "")
        return when {
            cleanPlate.isNotEmpty() && cleanPlate.length !in 7..8 -> "Placa inválida"
            else -> null
        }
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val pattern = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            pattern.isLenient = false
            pattern.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ========== MÉTODOS DE FORMULÁRIO ==========

    private fun validateForm() {
        val state = _screenState.value
        val form = state.formState

        val isValid = if (form.isLoginMode) {
            form.email.isNotBlank() &&
                    form.password.isNotBlank() &&
                    form.emailError == null &&
                    form.passwordError == null
        } else {
            form.email.isNotBlank() &&
                    form.password.isNotBlank() &&
                    form.registrationName.isNotBlank() &&
                    form.registrationPhone.isNotBlank() &&
                    form.registrationConfirmPassword.isNotBlank() &&
                    form.registrationDateOfBirth.isNotBlank() &&
                    form.emailError == null &&
                    form.passwordError == null &&
                    form.nameError == null &&
                    form.phoneError == null &&
                    form.confirmPasswordError == null &&
                    form.dateOfBirthError == null &&
                    form.vehiclePlateError == null
        }

        updateFormField(isFormValid = isValid)
    }

    private fun updateFormField(vararg fields: Pair<String, Any?>) {
        _screenState.update { state ->
            var newForm = state.formState
            fields.forEach { (key, value) ->
                when (key) {
                    "email" -> newForm = newForm.copy(email = value as String)
                    "password" -> newForm = newForm.copy(password = value as String)
                    "registrationName" -> newForm = newForm.copy(registrationName = value as String)
                    "registrationPhone" -> newForm = newForm.copy(registrationPhone = value as String)
                    "registrationConfirmPassword" -> newForm = newForm.copy(registrationConfirmPassword = value as String)
                    "registrationDateOfBirth" -> newForm = newForm.copy(registrationDateOfBirth = value as String)
                    "registrationVehiclePlate" -> newForm = newForm.copy(registrationVehiclePlate = value as String)
                    "emailError" -> newForm = newForm.copy(emailError = value as? String)
                    "passwordError" -> newForm = newForm.copy(passwordError = value as? String)
                    "nameError" -> newForm = newForm.copy(nameError = value as? String)
                    "phoneError" -> newForm = newForm.copy(phoneError = value as? String)
                    "confirmPasswordError" -> newForm = newForm.copy(confirmPasswordError = value as? String)
                    "dateOfBirthError" -> newForm = newForm.copy(dateOfBirthError = value as? String)
                    "vehiclePlateError" -> newForm = newForm.copy(vehiclePlateError = value as? String)
                    "isFormValid" -> newForm = newForm.copy(isFormValid = value as Boolean)
                }
            }
            state.copy(formState = newForm)
        }
    }

    // ========== ESTADO DE UI ==========

    private fun setLoading(isLoading: Boolean) {
        _screenState.update { state ->
            state.copy(
                isLoading = isLoading,
                uiState = if (isLoading) LoginUiState.Loading else LoginUiState.Idle
            )
        }
    }

    private fun setError(message: String) {
        _screenState.update { state ->
            state.copy(uiState = LoginUiState.Error(message))
        }
    }

    private fun clearErrorIfNeeded() {
        if (_screenState.value.uiState is LoginUiState.Error) {
            _screenState.update { it.copy(uiState = LoginUiState.Idle) }
        }
    }

    /**
     * Limpa todos os erros do formulário e da UI
     */
    fun clearError() {
        _screenState.update { state ->
            state.copy(
                uiState = LoginUiState.Idle,
                formState = state.formState.copy(
                    emailError = null,
                    passwordError = null,
                    nameError = null,
                    phoneError = null,
                    confirmPasswordError = null,
                    dateOfBirthError = null,
                    vehiclePlateError = null
                ),
                biometricErrorMessage = null
            )
        }
    }

    fun resetState() {
        _screenState.update { state ->
            state.copy(
                formState = LoginFormState(),
                uiState = LoginUiState.Idle,
                rememberMe = false,
                showPassword = false,
                isLoading = false
            )
        }
    }

    // ========== GERENCIAMENTO DE CREDENCIAIS ==========

    private suspend fun saveUserCredentials(email: String, password: String) {
        withContext(dispatchers.io) {
            securePreferences.saveCredentials(email, password)
        }
    }

    private suspend fun clearUserCredentials() {
        withContext(dispatchers.io) {
            securePreferences.clearCredentials()
        }
    }

    // ========== CLEANUP ==========

    /**
     * Limpa todos os recursos quando o ViewModel é destruído
     *
     * O que é limpo:
     * - Jobs de corrotinas (network, biometric, credentials)
     * - Estados do formulário
     * - Credenciais temporárias
     * - Fluxos e coletores
     */
    override fun onCleared() {
        super.onCleared()

        // 1. Cancelar todos os Jobs
        networkMonitorJob?.cancel()
        networkMonitorJob = null

        biometricCheckJob?.cancel()
        biometricCheckJob = null

        loadCredentialsJob?.cancel()
        loadCredentialsJob = null

        // 2. Limpar estados (opcional - depende do caso de uso)
        // resetState()

        // 3. Limpar credenciais se não estiver "remember me"
        if (!_screenState.value.rememberMe) {
            viewModelScope.launch {
                clearUserCredentials()
            }
        }

        // 4. Fechar recursos se necessário
        // (ex: fechar conexões de banco de dados, cancelar operações em andamento)

        // 5. Log para debug
        android.util.Log.d("LoginViewModel", "ViewModel destroyed and resources cleaned up")
    }
}