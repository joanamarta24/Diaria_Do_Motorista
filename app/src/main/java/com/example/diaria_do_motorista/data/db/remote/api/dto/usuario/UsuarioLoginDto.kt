package com.example.diaria_do_motorista.data.db.remote.api.dto.usuario

data class UsuarioLoginDto(
    val email:String,
    val senha:String,
)
data class UsuarioAlterarSenhaDto(
    val senhaAtual:String,
    val novaSenha:String
)
data class UsuarioRedefinirSenhaDto (
    val email: String,
    val token: String,
    val novaSenha: String
)
data class UsuarioSolicitarRedefinicaoSenhaDto(
    val email: String
)