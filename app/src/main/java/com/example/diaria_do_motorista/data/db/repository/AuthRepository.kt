package com.example.diaria_do_motorista.data.db.repository

import com.example.diaria_do_motorista.data.db.TokenStore
import com.example.diaria_do_motorista.data.db.dao.TokenStoreDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.domain.TipoUsuario
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.mapper.UsuarioMappers.toEntity
import com.example.diaria_do_motorista.data.db.remote.api.AuthApiService
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStoreDao: TokenStoreDao,
    private val usuarioDao: UsuarioDao,
    private val dispatchers: DispatchersProvider
) {
    suspend fun login(email: String, senha: String): Result<Usuario> = withContext(dispatchers.io) {
        return@withContext try {
            // Tenta login online primeiro
            val response = try {
                authApiService.login(email, senha)
            } catch (e: Exception) {
                null
            }

            if (response != null && response.success) {
                // Login online bem sucedido
                val usuario = response.usuario
                val tokenStore = TokenStore(
                    userId = usuario.id,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                    usuarioTipo = usuario.tipo
                )

                tokenStoreDao.insert(tokenStore)
                usuarioDao.insert(usuario.toEntity())

                Result.success(usuario)
            } else {
                // Fallback para login offline
                val usuarioEntity = usuarioDao.getByEmail(email)
                if (usuarioEntity != null && usuarioEntity.senha == senha) {
                    Result.success(usuarioEntity.toUsuario())
                } else {
                    Result.failure(Exception("Credenciais inválidas"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cadastrarAdministrador(
        nome: String,
        email: String,
        telefone: String,
        senha: String
    ): Result<Usuario> = withContext(dispatchers.io) {
        return@withContext try {
            val usuario = Usuario(
                id = UUID.randomUUID().toString(),
                nome = nome,
                email = email,
                telefone = telefone,
                dataNascimento = null,
                tipo = TipoUsuario.ADMINISTRADOR
            )

            val entity = usuario.toEntity().copy(senha = senha)
            usuarioDao.insert(entity)

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(userId: String) {
        withContext(dispatchers.io) {
            tokenStoreDao.deleteByUserId(userId)
        }
    }
}