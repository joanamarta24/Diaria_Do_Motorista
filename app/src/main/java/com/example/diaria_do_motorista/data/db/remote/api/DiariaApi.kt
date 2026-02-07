package com.example.diaria_do_motorista.data.db.remote.api

import androidx.room.Query
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaCreateDto
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaFiltroDto
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaResponseDto
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaResumoDto
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaUpdateDto
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DiariaApi {

    @GET("diarias")
    suspend fun listarDiarias(
        @Header("Authorization") token: String,
        @Query("page") page: Int= 0,
        @Query("size") size: Int = 20,
    ): Response<List<DiariaResponseDto>>

    @POST("diarias/filtrar")
    suspend fun filtrarDiarias(
        @Header("Authorization") token: String,
        @Body filtro: DiariaFiltroDto,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Response<List<DiariaResumoDto>>

    @GET("diarias/{id}")
    suspend fun obterDiariaPorId(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<DiariaResponseDto>

    @POST("diarias")
    suspend fun criarDiaria(
        @Header("Authorizatio") token: String,
        @Body diariaCreateDto: DiariaCreateDto
    ): Response<DiariaResponseDto>

    @PUT("diarias/{id}")
    suspend fun atualizarDiaria(
        @Header ("Authorization") token: String,
        @Path ("id") id: String,
        @Body  diariaUpdateDto: DiariaUpdateDto
    ): Response<DiariaResponseDto>


}