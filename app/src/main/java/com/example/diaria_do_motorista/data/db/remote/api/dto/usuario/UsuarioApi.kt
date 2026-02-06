package com.example.diaria_do_motorista.data.db.remote.api.dto.usuario

import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {
    @GET("usuario")
    suspend fun listarUsuarios(
        @Header("Authorization") token:String,
        @Query("transportadoraId") transportadoraId: String? = null,
        @Query("tipo") tipo:String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<List<UsuarioResponseDto>>

    @GET("usuarios/{id}")
    suspend fun obterUsuarioPorId(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<UsuarioResponseDto>
    @GET("usuarios/email/{email}")
    suspend fun obterUsuarioPorEmail(
        @Header("Authorization") token: String,
        @Path("email") email: String
    ): Response<UsuarioResponseDto>

    @POST("usuarios")
    suspend fun criarUsuario(
        @Header("Authorization") token: String,
        @Body usuarioCreateDto: UsuarioCreateDto
    ): Response<UsuarioResponseDto>

    @PUT("usuarios/{id}")
    suspend fun atualizarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body usuarioUpdateDto: UsuarioUpdateDto
    ): Response<UsuarioResponseDto>

    @PUT("usuarios/{id}/alterar-senha")
    suspend fun alterarSenha(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body alterarSenhaDto: UsuarioAlterarSenhaDto
    ): Response<Unit>

    @PUT("usuarios/{id}/status")
    suspend fun alterarStatusUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("ativo") ativo: Boolean
    ): Response<Unit>

    @DELETE("usuarios/{id}")
    suspend fun excluirUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @GET("usuarios/transportadora/{transportadoraId}")
    suspend fun listarUsuariosPorTransportadora(
        @Header("Authorization") token: String,
        @Path("transportadoraId") transportadoraId: String,
        @Query("ativo") ativo: Boolean? = null
    ): Response<List<UsuarioResponseDto>>

    @GET("usuarios/motoristas/disponiveis")
    suspend fun listarMotoristasDisponiveis(
        @Header("Authorization") token: String,
        @Query("data") data: String,
        @Query("transportadoraId") transportadoraId: String? = null
    ): Response<List<UsuarioResponseDto>>

    @GET("usuarios/meu-perfil")
    suspend fun obterMeuPerfil(
        @Header("Authorization") token: String
    ): Response<UsuarioResponseDto>
}