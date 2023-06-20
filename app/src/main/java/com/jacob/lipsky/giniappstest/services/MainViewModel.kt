package com.jacob.lipsky.giniappstest.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacob.lipsky.giniappstest.models.MyPhoto
import com.jacob.lipsky.giniappstest.models.Photo
import com.jacob.lipsky.giniappstest.models.PixabayPhotos
import com.jacob.lipsky.giniappstest.services.remote.RetrofitInstance
import com.jacob.lipsky.giniappstest.util.Resource
import com.jacob.lipsky.giniappstest.util.Util
import com.jacob.lipsky.giniappstest.util.downloadImageToBitMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val context: Context, private val repository: MainRepository
): ViewModel() {

    /**
     *on init of the view model we go to get the photos that are stored on the database
     */
    init {
         getRoomPhotos()
    }

    /**
     * at first page set to 0 so the first time we request photos it will increment to 1
     */
    var pageToken: Int = 0


    /**
     * live data to display our photos with an observer
     */
    private var _photosRoom :MutableLiveData<List<MyPhoto>> = MutableLiveData(listOf())
    var photosRoom : LiveData<List<MyPhoto>> = _photosRoom

    /**
     * tells us if the app is getting new photos so to not do multiple requests and to show the user that we are getting new photos
     */
    private var _isSeraching :MutableLiveData<Boolean> = MutableLiveData(false)
    var isSeraching : LiveData<Boolean> = _isSeraching


    /**
     * after we go the Json objects we download the photo to bit map and all together we insert to the room database
     */
    fun addPhotos(photos:List<Photo>) = viewModelScope.launch {
            val existingPhotos = mutableListOf<Int>()
            if(photosRoom.value!=null){
                for(photo in photosRoom.value!!){
                    existingPhotos.add(photo.id)
                }
            }
            val newPhotos = photos.filter { !existingPhotos.contains(it.id) }
            val listOfNewPhotos = mutableListOf<MyPhoto>()
        for (photo in newPhotos) {
            when (val bitmapResult = downloadImageToBitMap(photo.largeImageURL,context)) {
                is Resource.Success -> {
                    val bitmap = bitmapResult.data
                    if (bitmap != null) {
                        val myPhoto = MyPhoto(
                            id = photo.id,
                            likes= photo.likes,
                            comments = photo.comments,
                            bitmap = bitmap,
                            fileName = photo.largeImageURL,
                            dimensions = "1024x768",
                            creationDate = System.currentTimeMillis()
                        )
                        listOfNewPhotos.add(myPhoto)
                        Log.i("mydownload","downloaded ${newPhotos.indexOf(photo)} of ${newPhotos.size}")
                    } else {
                        Log.i("mydownload","bitmap is null")
                    }
                }
                else -> {Log.i("mydownload","request failed")}
            }
        }

        doAsync {
            _isSeraching.postValue(false)
            repository.insertPhotos(listOfNewPhotos)
            getRoomPhotos()

        }

    }


    fun loadNextPage() {
        pageToken++
        getVideosFromWeb(pageToken)
    }


    private fun getRoomPhotos(){
        doAsync {
            val photos = repository.getAllPhotos()
            _photosRoom.postValue(photos)
        }
    }

    /**
     * gets the json objects from the api
     */
    private fun getVideosFromWeb(page:Int){
            _isSeraching.postValue(true)
            val callback = RetrofitInstance.api.getPhotos(page)
            callback.enqueue(object : Callback<PixabayPhotos> {
                override fun onFailure(call: Call<PixabayPhotos>, t: Throwable) {
                    _isSeraching.postValue(false)
                    Util.requestError.postValue(1)
                }

                override fun onResponse(
                    call: Call<PixabayPhotos>,
                    response: Response<PixabayPhotos>
                ) {
                    if (response.isSuccessful) {
                        var listOfVideos =response.body()!!.hits
                        addPhotos(listOfVideos)
                    } else {
                        Util.requestError.postValue(1)
                    }
                }
            })
    }

}