package com.seuapp.di.modules

import com.example.diaria_do_motorista.data.db.di.ApplicationScope
import com.example.diaria_do_motorista.data.db.di.DispatcherDefault
import com.example.diaria_do_motorista.data.db.di.DispatcherIO
import com.example.diaria_do_motorista.data.db.di.DispatcherUnconfined
import com.seuapp.di.qualifiers.DispatcherDefault
import com.seuapp.di.qualifiers.DispatcherIO
import com.seuapp.di.qualifiers.DispatcherMain
import com.seuapp.di.qualifiers.DispatcherUnconfined
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @DispatcherIO
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DispatcherMain
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DispatcherDefault
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @DispatcherUnconfined
    fun providesUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    @Provides
    @Singleton
    @ApplicationScope
    fun providesApplicationScope(
        @DispatcherDefault defaultDispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(
        context = SupervisorJob() + defaultDispatcher
    )
}