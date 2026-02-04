package com.example.diaria_do_motorista.data.db.remote.api

import com.example.diaria_do_motorista.data.db.remote.dto.usuario.UsuarioResponseDto
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {
    @GET ("usuario")
    suspend fun listarUsuarios(
        @Header("Authorization") token:String,
        @Query ("transportadoraId") transportadoraId: String? = null,
        @Query("tipo") tipo:String? = null,
        @Query ("page") page: Int = 0,
        @Query ("size") size: Int = 20
    ):Response<List<UsuarioResponseDto>>
    @GET("usuario/{id}")
    suspend fun obterUsuarioPorId(
        @Header("Authorization") token: String,
        @Path("id") id:String
    )
}