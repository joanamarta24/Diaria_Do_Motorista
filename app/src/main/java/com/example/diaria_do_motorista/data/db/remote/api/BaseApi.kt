package com.example.diaria_do_motorista.data.db.remote.api

import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface BaseApi <T, ID {
    @GET
    suspend fun getAll(@Header("Authorizatio") token: String): Response<List<T>>
    @GET("{id}")
    suspend fun getById(
        @Header("Authorizatio") token: String,
        @Path("id") id:ID
    ): Response<T>

}