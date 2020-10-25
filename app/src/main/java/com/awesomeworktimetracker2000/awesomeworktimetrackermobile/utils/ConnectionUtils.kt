package com.awesomeworktimetracker2000.awesomeworktimetrackermobile.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build

/**
 * Connectivity helper class that provides helpful methods.
 */
class ConnectionUtils private constructor(private val context: Context) {

    companion object {
        @Volatile private lateinit var instance: ConnectionUtils

        /**
         * Get singleton instance of ConnectionUtils helper.
         */
        fun getInstance(context: Context): ConnectionUtils {
            synchronized(this) {
                if (!::instance.isInitialized) {
                    instance = ConnectionUtils(context)
                }

                return instance
            }
        }
    }

    /**
     * Check if we have connection to internet.
     * https://stackoverflow.com/questions/53532406/activenetworkinfo-type-is-deprecated-in-api-level-28
     *
     * @return True if connected.
     */
    fun hasInternetConnection(): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(TRANSPORT_WIFI) -> true
                actNw.hasTransport(TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        TYPE_WIFI -> true
                        TYPE_MOBILE -> true
                        TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}