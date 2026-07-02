package com.example.diaria_do_motorista.feature.login.states

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val registrationName: String = "",
    val registrationPhone: String = "",
    val registrationConfirmPassword: String = "",
    val registrationDateOfBirth: String = "",
    val registrationVehiclePlate: String = "",
    val isLoginMode: Boolean = true,
    val isFormValid: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val phoneError: String? = null,
    val confirmPasswordError: String? = null,
    val dateOfBirthError: String? = null,
    val vehiclePlateError: String? = null
)