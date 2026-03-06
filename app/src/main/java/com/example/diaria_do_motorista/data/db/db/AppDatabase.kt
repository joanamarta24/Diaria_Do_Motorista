package com.example.diaria_do_motorista.data.db.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.diaria_do_motorista.data.db.TokenStore
import com.example.diaria_do_motorista.data.db.dao.DiariaDao
import com.example.diaria_do_motorista.data.db.dao.TokenStoreDao
import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.dao.VeiculoDao
import com.example.diaria_do_motorista.data.db.entity.DiariaEntity
import com.example.diaria_do_motorista.data.db.entity.TransportadoraEntity
import com.example.diaria_do_motorista.data.db.entity.UsuarioEntity

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