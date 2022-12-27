package com.example.lifecycleawarecomponents

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkMonitor constructor(private val context: Context) : DefaultLifecycleObserver {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var connectivityManager: ConnectivityManager? = null
    private var validNetworks = HashSet<Network>()

    private lateinit var job: Job
    private lateinit var coroutineScope: CoroutineScope

    private val _networkAvailableStateFlow: MutableStateFlow<NetworkState> =
        MutableStateFlow(NetworkState.Available)

    val networkAvailableStateFlow get() = _networkAvailableStateFlow

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        Toast.makeText(context, "On Create - init() ", Toast.LENGTH_SHORT).show()
    }

    override fun onStart(owner: LifecycleOwner) {
        initCoroutine()
        initNetworkMonitoring()
        checkCurrentNetworkState()
        Toast.makeText(context, "On Start - registerNetworkCallback!!", Toast.LENGTH_SHORT).show()
    }

    override fun onPause(owner: LifecycleOwner) {
        //Toast.makeText(context, "On Pause - unregisterNetworkCallback!!", Toast.LENGTH_SHORT).show()
        validNetworks.clear()
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        job.cancel()
    }

//    override fun onStop(owner: LifecycleOwner) {
//        super.onStop(owner)
//        Toast.makeText(context, "On Stop - unregisterNetworkCallback!!", Toast.LENGTH_SHORT).show()
//        validNetworks.clear()
//        connectivityManager?.unregisterNetworkCallback(networkCallback)
//        job.cancel()
//    }

    private fun checkCurrentNetworkState() {
        connectivityManager?.allNetworks?.let {
            validNetworks.addAll(it)
        }
        checkValidNetworks()
    }

    private fun initCoroutine() {
        job = Job()
        coroutineScope = CoroutineScope(Dispatchers.Default + job)
    }

    private fun initNetworkMonitoring() {
        networkCallback = createNetworkCallback()

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            connectivityManager?.getNetworkCapabilities(network).also {
                if (it?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                    validNetworks.add(network)
                }
            }
            checkValidNetworks()
        }

        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    private fun checkValidNetworks() {
        coroutineScope.launch {
            _networkAvailableStateFlow.emit(
                if (validNetworks.size > 0)
                    NetworkState.Available
                else
                    NetworkState.Unavailable
            )
        }
    }


    sealed class NetworkState {
        object Unavailable : NetworkState()
        object Available : NetworkState()
    }
}