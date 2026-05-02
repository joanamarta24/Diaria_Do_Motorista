package com.example.diaria_do_motorista.data.db.network

sealed class NetworkState {
    object Unknown : NetworkState()
    object Disconnected : NetworkState()

    sealed class Connected : NetworkState() {
        object WiFi : Connected()
        object Cellular : Connected()
        object Ethernet : Connected()
        object VPN : Connected()
        object Other : Connected()
    }
}