package com.example.diarias.feature.login

import com.example.diaria_do_motorista.data.db.domain.Usuario

sealed class LoginEvent {

    // Eventos de entrada de dados
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    data class OnRegistrationNameChange(val name: String) : LoginEvent()
    data class OnRegistrationPhoneChange(val phone: String) : LoginEvent()
    data class OnRegistrationConfirmPasswordChange(val confirmPassword: String) : LoginEvent()

    // Eventos de UI

    object OnToggleLoginMode : LoginEvent()

    object OnToggleRememberMe : LoginEvent()

    object OnToggleShowPassword : LoginEvent()

    // Eventos de ação
    object OnLogin : LoginEvent()

    object OnRegister : LoginEvent()

    object OnForgotPassword : LoginEvent()

    // Eventos de resultado
    data class OnLoginSuccess(val usuario: Usuario) : LoginEvent()

    data class OnRegistrationSuccess(val usuario: Usuario) : LoginEvent()

    data class OnError(val message: String) : LoginEvent()
    object OnForgotPasswordClicked : LoginEvent()

    // Eventos de navegação
    object OnNavigateToForgotPassword : LoginEvent()

    object OnNavigateToTermsOfUse : LoginEvent()

    object OnNavigateToPrivacyPolicy : LoginEvent()

    // Eventos de validação

    object ClearErrors : LoginEvent()

    object ResetForm : LoginEvent()

    // Eventos de biometria
    object OnBiometricLogin : LoginEvent()
    object OnBiometricSuccess : LoginEvent()


    data class OnBiometricError(val errorMessage: String) : LoginEvent()
}