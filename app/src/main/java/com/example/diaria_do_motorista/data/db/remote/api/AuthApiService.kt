package com.example.diaria_do_motorista.data.db.remote.api

import com.example.diaria_do_motorista.data.db.remote.dto.AuthResponse
import com.example.diaria_do_motorista.data.db.remote.dto.TokenRefreshRequest
import com.example.diaria_do_motorista.data.db.remote.dto.TokenRefreshResponse
import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioLoginDto
import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioSolicitarRedefinicaoSenhaDto
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login (@Body loginDto: UsuarioLoginDto): Response<AuthResponse>

    @POST("auth/refhesh")
    suspend fun  refreshToken(@Body request: TokenRefreshRequest): Response<TokenRefreshResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    @POST("auth/solicitar-redefinicao-senha")
    suspend fun solicitarRedefinicaoSenha(
        @Body request: UsuarioSolicitarRedefinicaoSenhaDto
    ): Response<Unit>
    @POST("auth/redefinir-senha")
    suspend fun redefinirSenha(
        @Body request: UsuarioSolicitarRedefinicaoSenhaDto
    ): Response<Unit>
    @GET("auth/verificar-sessao")
    suspend fun verificarSessao(@Header("Authorizatio") token: String): Response<Unit>

    @GET("auth/permissoes")
    suspend fun obterPermissoes(@Header("Authorization") token: String): Response<List<String>>
}

