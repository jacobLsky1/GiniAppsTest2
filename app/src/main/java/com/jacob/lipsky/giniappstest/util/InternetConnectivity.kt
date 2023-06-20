package com.jacob.lipsky.giniappstest.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService


class InternetConnectivity {

    companion object{
        @RequiresApi(Build.VERSION_CODES.M)

        fun connectToInternet(context: Context){
            var i  = Intent(Settings.ACTION_WIFI_SETTINGS)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context, i, null)
        }
    }
}