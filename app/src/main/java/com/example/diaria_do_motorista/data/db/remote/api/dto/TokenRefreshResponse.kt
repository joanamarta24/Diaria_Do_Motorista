package com.example.diaria_do_motorista.data.db.remote.api.dto

import java.sql.Timestamp

data class TokenRefreshResponse(
    val token:String,
    val refreshToken:String,
    val tokenType:String = "Bearer",
    val expiresIn:Long
)
data class AuthErrorResponse(
    val mensagem:String,
    val codigo:String,
    val timestamp: String
)
data class PermissaoResponse(
    val  nome:String,
    val descricao:String
)
