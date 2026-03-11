package com.example.diaria_do_motorista.data.db.domain

import java.util.Date

data class SyncError (
    val message: String,
    val code: String,
    val timestamp: Date = Date(),
    val retryable: Boolean = true,
    val details: Map<String, Any>? = null
) {
    companion object {
        fun networError(throwable: Throwable): SyncError {
            return SyncError(
                message = "Errro de rede: ${throwable.message}",
                code = "NETWORK_ERROR",
                retryable = true
            )
        }

        fun authError(): SyncError {
            return SyncError(
                message = "Erro de autenticação",
                code = "AUTH_ERROR",
                retryable = false
            )
        }

        fun serverError(code: Int): SyncError {
            return SyncError(
                message = "Error do servidor:HTTP $code",
                code = "SERVER_ERROR_$code",
                retryable = code >= 500
            )
        }

        fun timeoutErro(): SyncError {
            return SyncError(
                message = "Tempo limite excedido",
                code = "TIMEOUT_ERROR",
                retryable = true
            )
        }

        fun conflictError(items: List<String>): SyncError {
            return SyncError(
                message = "Conflito detectado em ${items.size} itens",
                code = "CONFLICT_ERROR",
                retryable = true
            )
        }

        fun shouldRetry(): Boolean {
            return retryable
        }
    }
}