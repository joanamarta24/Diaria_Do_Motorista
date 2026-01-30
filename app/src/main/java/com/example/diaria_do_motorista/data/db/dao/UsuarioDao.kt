package com.example.diaria_do_motorista.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.diaria_do_motorista.data.db.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: UsuarioEntity)

    @Update
    suspend fun update(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getById(id: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun getByEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE tipo = 'MOTORISTA' AND ativo = 1")
    suspend fun getAllMotoristas(): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios WHERE tipo = :tipo AND ativo = 1")
    suspend fun getByTipo(tipo: String): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios WHERE sync_status = 'PENDENTE'")
    suspend fun getPendentesSync(): List<UsuarioEntity>

    @Query("UPDATE usuarios SET sync_status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)
}