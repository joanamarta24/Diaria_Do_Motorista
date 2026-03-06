package com.example.diaria_do_motorista.util

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersProvider {

    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

    val default: CoroutineDispatcher
}