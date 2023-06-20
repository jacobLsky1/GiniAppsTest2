package com.jacob.lipsky.giniappstest.util

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import java.io.ByteArrayOutputStream

class Util {
    companion object{
        var hasInternet: MutableLiveData<Boolean> = MutableLiveData()
        var requestError: MutableLiveData<Int> = MutableLiveData(0)

        fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            return outputStream.toByteArray()
        }
    }
}