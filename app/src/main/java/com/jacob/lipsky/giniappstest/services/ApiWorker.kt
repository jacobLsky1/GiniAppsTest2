package com.jacob.lipsky.giniappstest.services


import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.jacob.lipsky.giniappstest.MainActivity
import com.jacob.lipsky.giniappstest.models.MyPhoto
import com.jacob.lipsky.giniappstest.models.Photo
import com.jacob.lipsky.giniappstest.models.PixabayPhotos
import com.jacob.lipsky.giniappstest.services.remote.RetrofitInstance
import com.jacob.lipsky.giniappstest.util.Resource
import com.jacob.lipsky.giniappstest.util.Util
import com.jacob.lipsky.giniappstest.util.downloadImageToBitMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class ApiWorker @Inject constructor(
    private val context: Context, private val repository: MainRepository,params: WorkerParameters
) :
    CoroutineWorker(context, params) {


    /**
     * gets photos from api even when app is closed
     *
     * @return Result.success() to keep going in its periotic work request
     */
    override suspend fun doWork() = withContext(Dispatchers.IO) {
        getVideosFromWeb(1)
        Result.success()
    }


    /**
     * gets photos from api and sends them to the room database
     */
    private suspend fun getVideosFromWeb(page:Int) {

        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.getPhotos(page).execute()
            }
            if (response.isSuccessful) {
                val photos = response.body()?.hits ?: emptyList()
                handleResponse(photos)
            } else {
                Util.requestError.postValue(1)
            }
        } catch (e: Exception) {
            Util.requestError.postValue(1)
        }
    }

    suspend fun handleResponse(photos: List<Photo>) {
        val photosRoom = repository.getAllPhotos()
        val existingPhotos = mutableListOf<Int>()
        if (photosRoom != null) {
            for (photo in photosRoom) {
                existingPhotos.add(photo.id)
            }
        }
        val newPhotos = photos.filter { !existingPhotos.contains(it.id) }
        val listOfNewPhotos = mutableListOf<MyPhoto>()
        for (photo in newPhotos) {
            when (val bitmapResult = downloadImageToBitMap(photo.largeImageURL, context)) {
                is Resource.Success -> {
                    val bitmap = bitmapResult.data
                    if (bitmap != null) {
                        val myPhoto = MyPhoto(
                            id = photo.id,
                            likes = photo.likes,
                            comments = photo.comments,
                            bitmap = bitmap,
                            fileName = photo.largeImageURL,
                            dimensions = "1024x768",
                            creationDate = System.currentTimeMillis()
                        )
                        listOfNewPhotos.add(myPhoto)
                        Log.i(
                            "mydownload",
                            "downloaded ${newPhotos.indexOf(photo)} of ${newPhotos.size}"
                        )
                    } else {
                        Log.i("mydownload", "bitmap is null")
                    }
                }
                else -> {
                    Log.i("mydownload", "request failed")
                }
            }
        }

        doAsync {
            repository.insertPhotos(listOfNewPhotos)
        }
    }

    companion object {

    }
}

