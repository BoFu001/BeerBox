package com.bofu.beerbox.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

fun Context.hasConnection(): Boolean {


    val TAG = javaClass.simpleName

    var connectionType = 0

    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

    networkCapabilities?.run {
        if(hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            connectionType = 1
        } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            connectionType = 2
        }
    }


    when(connectionType){
        0 -> Log.d(TAG, "hasConnection? Not Connected.")
        1 -> Log.d(TAG, "hasConnection? WIFI Connected.")
        2 -> Log.d(TAG, "hasConnection? Cellular Connected.")
    }


    return connectionType != 0
}