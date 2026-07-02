package com.example.diaria_do_motorista.feature.login.events

import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.models.Usuario

sealed class LoginEvent {
    // Eventos de UI - Entrada de dados
    data class OnEmailChange(val email: String) : LoginEvent()
    data class OnPasswordChange(val password: String) : LoginEvent()
    data class OnRegistrationNameChange(val name: String) : LoginEvent()
    data class OnRegistrationPhoneChange(val phone: String) : LoginEvent()
    data class OnRegistrationConfirmPasswordChange(val confirmPassword: String) : LoginEvent()
    data class OnRegistrationDateOfBirthChange(val dateOfBirth: String) : LoginEvent()
    data class OnRegistrationVehiclePlateChange(val plate: String) : LoginEvent()

    // Eventos de UI - Ações
    object OnToggleLoginMode : LoginEvent()
    object OnToggleRememberMe : LoginEvent()
    object OnToggleShowPassword : LoginEvent()
    object OnLogin : LoginEvent()
    object OnRegister : LoginEvent()
    object OnForgotPassword : LoginEvent()
    object ClearErrors : LoginEvent()
    object ResetForm : LoginEvent()
    object OnBiometricLogin : LoginEvent()

    // Eventos de Resultado - Sucesso
    data class OnLoginSuccess(val usuario: Usuario) : LoginEvent()
    data class OnRegistrationSuccess(val usuario: Usuario) : LoginEvent()
    data class OnForgotPasswordClicked(val email: String) : LoginEvent()
    object OnBiometricSuccess : LoginEvent()

    // Eventos de Resultado - Erro
    data class OnBiometricError(val errorMessage: String) : LoginEvent()
    data class OnError(val message: String) : LoginEvent()
}