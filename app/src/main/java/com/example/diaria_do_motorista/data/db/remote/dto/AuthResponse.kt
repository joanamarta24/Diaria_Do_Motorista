package com.example.diaria_do_motorista.data.db.remote.dto

data class AuthResponse(
    val token:String,
    val refreshToken:String,
    val tokenType:String ="Bearer",
    val expiresIn:Long,
    val usuario:UsuarioAuthResponse
)
