package com.example.diaria_do_motorista.data.db.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherIO

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherDefault

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class  DispatcherUnconfined

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseVersion

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiBaseUsl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiTimeOut

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthToken

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SharedPrefsName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class  SyncWorkInterval

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope