package com.example.diaria_do_motorista.data.db.remote.api

import androidx.room.Query
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaFiltroDto
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaResponseDto
import com.example.diaria_do_motorista.data.db.remote.dto.diaria.DiariaResumoDto
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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
        @Header("Authorization")
    )
}