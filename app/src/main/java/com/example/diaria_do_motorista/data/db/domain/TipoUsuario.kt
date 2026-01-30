package com.example.diaria_do_motorista.data.db.domain

sealed class TipoUsuario {
    object MOTORISTA : TipoUsuario()
    object ADMINISTRADOR : TipoUsuario()

    companion object {
        fun fromString(value: String): TipoUsuario {
            return when (value) {
                "MOTORISTA" -> MOTORISTA
                "ADMINISTRADOR" -> ADMINISTRADOR
                else -> MOTORISTA
            }
        }
    }
}