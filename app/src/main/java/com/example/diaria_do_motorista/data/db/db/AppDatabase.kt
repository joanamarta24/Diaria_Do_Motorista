package com.seuapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.diaria_do_motorista.data.db.dao.DiariaDao
import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.dao.VeiculoDao
import com.example.diaria_do_motorista.data.db.entity.DiariaEntity
import com.example.diaria_do_motorista.data.db.entity.TransportadoraEntity
import com.example.diaria_do_motorista.data.db.entity.UsuarioEntity
import com.github.binodnme.dateconverter.converter.DateConverter
import com.example.diaria_do_motorista.data.db.TipoUsuarioConverter
import com.example.diaria_do_motorista.data.db.TipoVeiculoConverter
import com.example.diaria_do_motorista.data.db.SyncStatusConverter

@Database(
    entities = [
        UsuarioEntity::class,
        DiariaEntity::class,
        TransportadoraEntity::class,
        VeiculoEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    DateConverter::class,
    TipoUsuarioConverter::class,
    TipoVeiculoConverter::class,
    SyncStatusConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun diariaDao(): DiariaDao
    abstract fun transportadoraDao(): TransportadoraDao
    abstract fun veiculoDao(): VeiculoDao

    companion object {
        const val DATABASE_NAME = "app_database.db"
        const val DATABASE_VERSION = 1
    }
}