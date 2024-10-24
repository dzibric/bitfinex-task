package com.mobtech.bitfinex.task.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkConnectivityObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    enum class Status {
        Available, Unavailable, Losing, Lost
    }

    val networkStatus: Flow<Status> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(Status.Available)
            }

            override fun onLost(network: Network) {
                trySend(Status.Lost)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                trySend(Status.Losing)
            }

            override fun onUnavailable() {
                trySend(Status.Unavailable)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        val currentStatus = getCurrentNetworkStatus()
        trySend(currentStatus)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    private fun getCurrentNetworkStatus(): Status {
        val network = connectivityManager.activeNetwork ?: return Status.Unavailable
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return Status.Unavailable

        return if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            Status.Available
        } else {
            Status.Unavailable
        }
    }
}
