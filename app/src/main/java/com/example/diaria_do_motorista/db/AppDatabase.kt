package com.example.diaria_do_motorista.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.diaria_do_motorista.TokenStore
import com.example.diaria_do_motorista.entity.DiariaEntity
import com.example.diaria_do_motorista.entity.TransportadoraEntity
import com.example.diaria_do_motorista.entity.UsuarioEntity
import com.example.diaria_do_motorista.entity.VeiculoEntity

@Database(
    entities = [
        UsuarioEntity::class,
        TransportadoraEntity::class,
        VeiculoEntity::class,
        DiariaEntity::class,
        TokenStore::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun transportadoraDao(): TransportadoraDao
    abstract fun veiculoDao(): VeiculoDao
    abstract fun diariaDao(): DiariaDao
    abstract fun tokenStoreDao(): TokenStoreDao
}