package com.example.diaria_do_motorista.ui.theme.feature.login.login

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val isLoginViewModel: Boolean = true,
    val registrationName: String = "",
    val registrationPhone: String = "",
    val registrationConfirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isFormValid: Boolean = false
)
