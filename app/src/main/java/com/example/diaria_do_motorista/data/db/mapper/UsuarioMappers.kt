package com.example.diaria_do_motorista.data.db.mapper

import com.example.diaria_do_motorista.data.db.domain.TipoUsuario
import com.example.diaria_do_motorista.data.db.entity.UsuarioEntity

object UsuarioMappers {
    fun UsuarioEntity.toUsuario():Usuario{
        return Usuario(
            id = id,
            nome = nome,
            email = email,
            telefone = telefone,
            dataNascimento = dataNascimento,
            tipo = TipoUsuario.fromString(tipo),
            matriculaVeiculo = matriculaVeiculo,
            transportadoraId = transportadoraId,
            ativo = ativo
        )
    }
    fun Usuario.toEntity():UsuarioEntity{
        return UsuarioEntity(
            id = id,
            nome = nome,
            email = email,
            telefone = telefone,
            dataNascimento = dataNascimento,
            tipo = when (tipo){
                is TipoUsuario.MOTORISTA ->"MOTORISTA"
                is TipoUsuario.ADMINISTRADOR -> "ADMINISTRADOR"
            },
            matriculaVeiculo = matriculaVeiculo,
            transportadoraId = transportadoraId,
            senha = "",
            ativo = ativo
        )
    }
}