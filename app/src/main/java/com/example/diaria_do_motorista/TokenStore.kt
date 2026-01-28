package com.example.diaria_do_motorista

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token_store")
data class TokenStore(
    @PrimaryKey
    val userID:String,
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val usuarioTipo: String
)
