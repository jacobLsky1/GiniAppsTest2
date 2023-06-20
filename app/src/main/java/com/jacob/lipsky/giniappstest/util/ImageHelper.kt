package com.jacob.lipsky.giniappstest.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun downloadImageToBitMap(url: String,context:Context): Resource<Bitmap?> {
    return withContext(Dispatchers.IO) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()

        val result = Coil.execute(request)
        if (result is SuccessResult) {
           Resource.Success(result.drawable?.toBitmap())
        } else {
            Resource.Error("Failed to download image")
        }
    }
}