package com.example.diaria_do_motorista.data.db.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @SharedPrefsName
    fun provideSharedPrefsName(): String ="app_preferences"


    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
        @SharedPrefsName prefsName: String
    ): SharedPreferences {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton

}