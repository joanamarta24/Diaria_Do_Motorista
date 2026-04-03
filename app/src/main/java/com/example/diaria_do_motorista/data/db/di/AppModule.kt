package com.example.diaria_do_motorista.data.db.di

import android.content.Context
import androidx.room.Room
import com.example.diaria_do_motorista.data.db.dao.DiariaDao
import com.example.diaria_do_motorista.data.db.dao.TokenStoreDao
import com.example.diaria_do_motorista.data.db.dao.TransportadoraDao
import com.example.diaria_do_motorista.data.db.dao.UsuarioDao
import com.example.diaria_do_motorista.data.db.dao.VeiculoDao
import com.example.diaria_do_motorista.data.db.repository.AuthRepository
import com.example.diaria_do_motorista.data.db.repository.DiariaRepository
import com.example.diaria_do_motorista.data.db.repository.UsuarioRepository
import com.example.diaria_do_motorista.data.db.session.AuthStateViewModel
import com.example.diaria_do_motorista.ui.theme.feature.login.home.HomeViewModel
import com.example.diaria_do_motorista.ui.theme.feature.login.login.LoginViewModel
import com.seuapp.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "diarias_db"
        ).fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    @Singleton
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao{
        return database.usuarioDao()
    }
    @Provides
    @Singleton
    fun provideDiariaDao(database: AppDatabase): DiariaDao{
        return  database.diariaDao()
    }
    @Provides
    @Singleton
    fun provideTransportadoraDao(database: AppDatabase): TransportadoraDao{
        return database.transportadoraDao()
    }
    @Provides
    @Singleton
    fun provideVeiculoDao(database: AppDatabase): VeiculoDao{
        return  database.veiculoDao()
    }
    @Provides
    @Singleton
    fun provideTokenStoreDao(database: AppDatabase): TokenStoreDao{
        return database.tokenStoreDao()
    }
    @Module
    @InstallIn(ViewModelComponent::class)
    object  ViewModelModule{
        fun provideLoginViewModel(
            authRepository: AuthRepository
        ): LoginViewModel{
            return LoginViewModel (authRepository)
        }
        @Provides
        fun provideHomeViewModel(
            diariaRepository: DiariaRepository,
            usuarioRepository: UsuarioRepository,
            authStateViewModel: AuthStateViewModel
        ): HomeViewModel{
            return HomeViewModel(diariaRepository, usuarioRepository,authStateViewModel)
        }
    }

}