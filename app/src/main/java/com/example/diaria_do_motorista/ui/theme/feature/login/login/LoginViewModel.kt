import android.app.Application
import android.provider.Settings.Global.putString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.repository.AuthRepository
import com.example.diaria_do_motorista.ui.theme.feature.login.login.LoginFormState
import com.example.diaria_do_motorista.ui.theme.feature.login.login.LoginScreenState
import com.example.diaria_do_motorista.ui.theme.feature.login.loginsealed.LoginUiState
import com.example.diaria_do_motorista.util.DispatchersProvider
import com.seuapp.network.NetworkMonitor
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor,
    private val dispatchers: DispatchersProvider
) : ViewModel() {

    private val _screenState = MutableStateFlow(LoginScreenState())
    val screenState: StateFlow<LoginScreenState> = _screenState.asStateFlow()

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent.asSharedFlow()

    private var isNetworkAvailable = false

    init {
        monitorNetworkState()
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
        }
    }

    private fun onEmailChange(email: String) {
        val newFormState = _screenState.value.formState.copy(
            email = email,
            emailError = validateEmail(email)
        )
        updateFormState(newFormState)
    }

    private fun onPasswordChange(password: String) {
        val newFormState = _screenState.value.formState.copy(
            password = password,
            passwordError = validatePassword(password)
        )
        updateFormState(newFormState)
    }

    private fun onToggleLoginMode() {
        val currentState = _screenState.value.formState
        _screenState.update { state ->
            state.copy(
                formState = state.formState.copy(
                    isLoginMode = !state.formState.isLoginMode,
                    email = if (!state.formState.isLoginMode) state.formState.email else "",
                    password = if (!state.formState.isLoginMode) state.formState.password else "",
                    registrationName = if (state.formState.isLoginMode) "" else state.formState.registrationName,
                    registrationPhone = if (state.formState.isLoginMode) "" else state.formState.registrationPhone,
                    registrationConfirmPassword = if (state.formState.isLoginMode) "" else state.formState.registrationConfirmPassword,
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
            cleanPhone.length < 10 -> "Telefone inválido"
            cleanPhone.length > 11 -> "Telefone inválido"
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

            // Determinar se é cadastro de administrador ou motorista
            // Por padrão, primeiro cadastro será administrador
            val result = withContext(dispatchers.io) {
                authRepository.cadastrarAdministrador(
                    nome = formState.registrationName,
                    email = formState.email,
                    telefone = formState.registrationPhone,
                    senha = formState.password
                )
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

    private fun onForgotPassword() {
        viewModelScope.launch {
            _loginEvent.emit(LoginEvent.OnForgotPasswordClicked)
        }
    }

    private suspend fun saveUserCredentials(email: String, password: String) {
        // Salvar credenciais no DataStore ou SharedPreferences
        withContext(dispatchers.io) {
            // Implementar salvamento seguro das credenciais
            val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                getApplication<Application>()
            )
            prefs.edit().apply {
                putString("saved_email", email)
                putString("saved_password", password)
                putBoolean("remember_me", true)
                apply()
            }
        }
    }

    private suspend fun clearUserCredentials() {
        withContext(dispatchers.io) {
            val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                getApplication<Application>()
            )
            prefs.edit().apply {
                remove("saved_email")
                remove("saved_password")
                putBoolean("remember_me", false)
                apply()
            }
        }
    }

    fun loadSavedCredentials() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(
                    getApplication<Application>()
                )
                val rememberMe = prefs.getBoolean("remember_me", false)
                if (rememberMe) {
                    val savedEmail = prefs.getString("saved_email", "") ?: ""
                    val savedPassword = prefs.getString("saved_password", "") ?: ""

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

    fun resetState() {
        _screenState.update { state ->
            state.copy(
                formState = LoginFormState(),
                uiState = LoginUiState.Idle,
                rememberMe = false,
                showPassword = false
            )
        }
    }
}

sealed class LoginEvent {
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    object OnToggleLoginMode : LoginEvent()
    data class OnRegistrationNameChange(val name: String) : LoginEvent()
    data class OnRegistrationPhoneChange(val phone: String) : LoginEvent()
    data class OnRegistrationConfirmPasswordChange(val confirmPassword: String) : LoginEvent()
    object OnToggleRememberMe : LoginEvent()
    object OnToggleShowPassword : LoginEvent()
    object OnLogin : LoginEvent()
    object OnRegister : LoginEvent()
    object OnForgotPassword : LoginEvent()
    data class OnLoginSuccess(val usuario: Usuario) : LoginEvent()
    data class OnRegistrationSuccess(val usuario: Usuario) : LoginEvent()
    object OnForgotPasswordClicked : LoginEvent()
}