package com.example.diaria_do_motorista.data.db.remote.api.dto

data class UsuarioAuthResponse(
    val id:String,
    val nome:String,
    val email:String,
    val tipo:String,
    val tipoDescricao:String,
    val transportadoraId:String?,
    val matriculaVeiculo:String?,
    val permissoes:List<String>
)
data class TokenRefreshRequest(
    val refreshToken:String
)