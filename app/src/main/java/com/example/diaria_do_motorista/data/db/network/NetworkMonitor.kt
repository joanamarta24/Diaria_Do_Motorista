package com.seuapp.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.example.diaria_do_motorista.data.db.remote.enums.NetworkSpeed
import com.example.diaria_do_motorista.data.db.remote.enums.connection.ConnectionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val context: Context
) {

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Unknown)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val _connectionType = MutableStateFlow<ConnectionType>(ConnectionType.UNKNOWN)
    val connectionType: StateFlow<ConnectionType> = _connectionType.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            updateNetworkState()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            updateNetworkState()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            updateNetworkState()
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var checkJob: Job? = null

    init {
        registerNetworkCallback()
        startPeriodicChecks()
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        updateNetworkState()
    }

    private fun startPeriodicChecks() {
        checkJob = scope.launch {
            while (true) {
                updateNetworkState()
                delay(30000) // Verificar a cada 30 segundos
            }
        }
    }

    fun updateNetworkState() {
        scope.launch {
            val currentState = getCurrentNetworkState()
            val currentConnectionType = getCurrentConnectionType()
            val currentIsConnected = isNetworkAvailable()

            _networkState.value = currentState
            _connectionType.value = currentConnectionType
            _isConnected.value = currentIsConnected
        }
    }

    private fun getCurrentNetworkState(): NetworkState {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            when {
                capabilities == null -> NetworkState.Disconnected
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.Connected.WiFi
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.Connected.Cellular
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkState.Connected.Ethernet
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkState.Connected.VPN
                else -> NetworkState.Connected.Other
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            when {
                networkInfo == null || !networkInfo.isConnected -> NetworkState.Disconnected
                networkInfo.type == ConnectivityManager.TYPE_WIFI -> NetworkState.Connected.WiFi
                networkInfo.type == ConnectivityManager.TYPE_MOBILE -> NetworkState.Connected.Cellular
                networkInfo.type == ConnectivityManager.TYPE_ETHERNET -> NetworkState.Connected.Ethernet
                networkInfo.type == ConnectivityManager.TYPE_VPN -> NetworkState.Connected.VPN
                else -> NetworkState.Connected.Other
            }
        }
    }

    private fun getCurrentConnectionType(): ConnectionType {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            when {
                capabilities == null -> ConnectionType.NONE
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    when {
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_TEMPORARILY_NOT_METERED) -> ConnectionType.CELLULAR_UNMETERED
                        else -> ConnectionType.CELLULAR_METERED
                    }
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectionType.VPN
                else -> ConnectionType.UNKNOWN
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            when (networkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> ConnectionType.WIFI
                ConnectivityManager.TYPE_MOBILE -> ConnectionType.CELLULAR_METERED
                ConnectivityManager.TYPE_ETHERNET -> ConnectionType.ETHERNET
                ConnectivityManager.TYPE_VPN -> ConnectionType.VPN
                else -> ConnectionType.NONE
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities != null && (
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                        )
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo
                networkInfo != null && networkInfo.isConnected
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isWifiConnected(): Boolean {
        return networkState.value == NetworkState.Connected.WiFi
    }

    fun isMeteredConnection(): Boolean {
        return when (connectionType.value) {
            ConnectionType.CELLULAR_METERED -> true
            else -> false
        }
    }

    fun getNetworkSpeed(): NetworkSpeed {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            capabilities?.linkDownstreamBandwidthKbps?.let { bandwidth ->
                when {
                    bandwidth > 10000 -> NetworkSpeed.VERY_FAST // > 10 Mbps
                    bandwidth > 5000 -> NetworkSpeed.FAST // > 5 Mbps
                    bandwidth > 1000 -> NetworkSpeed.MODERATE // > 1 Mbps
                    else -> NetworkSpeed.SLOW
                }
            } ?: NetworkSpeed.UNKNOWN
        } else {
            when (connectionType.value) {
                ConnectionType.WIFI -> NetworkSpeed.FAST
                ConnectionType.ETHERNET -> NetworkSpeed.VERY_FAST
                ConnectionType.CELLULAR_METERED, ConnectionType.CELLULAR_UNMETERED -> NetworkSpeed.MODERATE
                else -> NetworkSpeed.SLOW
            }
        }
    }

    fun cleanup() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            checkJob?.cancel()
        } catch (e: Exception) {
            // Ignorar exceções ao desregistrar
        }
    }
}

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



