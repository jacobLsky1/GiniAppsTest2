package com.jacob.lipsky.giniappstest.services

import com.jacob.lipsky.giniappstest.models.MyPhoto
import com.jacob.lipsky.giniappstest.services.local.PhotoDao


class MainRepository(val photoDao: PhotoDao) {

    fun insertPhotos(photos: List<MyPhoto>) {
        photoDao.insert(photos)
    }

    fun getAllPhotos(): List<MyPhoto> {
        return photoDao.getAllPhotos()
    }

}
