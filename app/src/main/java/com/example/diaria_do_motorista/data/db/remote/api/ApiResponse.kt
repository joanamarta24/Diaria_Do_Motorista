package com.example.diaria_do_motorista.data.db.remote.api

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
    val message: String? = null
)
data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)
