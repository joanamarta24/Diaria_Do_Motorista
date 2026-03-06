package com.seuapp.di.modules

import android.content.Context
import androidx.room.Room
import com.example.diaria_do_motorista.data.db.dao.DiariaDao
import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.dao.VeiculoDao
import com.example.diaria_do_motorista.data.db.db.AppDatabase
import com.example.diaria_do_motorista.data.db.di.DatabaseName
import com.example.diaria_do_motorista.data.db.di.DatabaseVersion
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @DatabaseName
    fun provideDatabaseName(): String = "app_database.db"

    @Provides
    @DatabaseVersion
    fun provideDatabaseVersion(): Int = 1

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @DatabaseName databaseName: String,
        @DatabaseVersion databaseVersion: Int
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        )
            .fallbackToDestructiveMigration() // Ou use .addMigrations() para migrações controladas
            .enableMultiInstanceInvalidation() // Para múltiplos processos
            .setQueryExecutor(java.util.concurrent.Executors.newSingleThreadExecutor()) // Para queries em background
            .build()
    }

    // ========== DAOs ==========

    @Provides
    @Singleton
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao {
        return database.usuarioDao()
    }

    @Provides
    @Singleton
    fun provideDiariaDao(database: AppDatabase): DiariaDao {
        return database.diariaDao()
    }

    @Provides
    @Singleton
    fun provideTransportadoraDao(database: AppDatabase): TransportadoraDao {
        return database.transportadoraDao()
    }

    @Provides
    @Singleton
    fun provideVeiculoDao(database: AppDatabase): VeiculoDao {
        return database.veiculoDao()
    }

    // Opcional: Provider para Database em memória (útil para testes)
    @Provides
    @Singleton
    fun provideInMemoryDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries() // Apenas para testes
            .build()
    }
}