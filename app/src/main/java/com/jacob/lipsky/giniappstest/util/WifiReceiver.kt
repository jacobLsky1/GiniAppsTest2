package com.jacob.lipsky.giniappstest.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi


class WifiReceiver() : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(context: Context, intent: Intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent!!.action) {
            val noConnectivity: Boolean = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (noConnectivity) {
                Util.hasInternet.postValue(false)

            } else {
                Util.hasInternet.postValue(true)
            }
        }
    }
}