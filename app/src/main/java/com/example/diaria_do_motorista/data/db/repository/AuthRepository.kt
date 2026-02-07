package com.example.diaria_do_motorista.data.db.repository

import com.example.diaria_do_motorista.data.db.dao.TokenStoreDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.domain.Usuario
import com.example.diaria_do_motorista.data.db.remote.api.AuthApiService
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private  val authApiService: AuthApiService,
    private val tokenStoreDao: TokenStoreDao,
    private val usuarioDao: UsuarioDao,
    private val dispatchers: DispatcherProvider
){
    suspend fun login(email: String,senha: String): Result<Usuario> = withContext(dispatchers.io){
        return@withContext try {
            val response = try {
                authApiService.login(email,senha)
            }catch (e: Exception){
                null
            }
         if (response != null && response.success){
             val usuario = response
         }
        }
    }
}