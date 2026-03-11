package com.example.diaria_do_motorista.data.db.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
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
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context,
        @SharedPrefsName prefsName: String
    ): SharedPreferences{
        val masterKeyAlias = MasterKeys.getOnCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            prefsName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}