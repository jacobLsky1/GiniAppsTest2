package com.jacob.lipsky.giniappstest.services.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jacob.lipsky.giniappstest.models.MyPhoto

@Dao
interface  PhotoDao {
    /**
     * inserts the new photos into the database
     *
     * @param photos The new photos to be add.
     */
    @Insert
    fun insert(photos: List<MyPhoto>)


    /**
     * gets all exsisting photos from the database
     *
     * @return list of object Myphoto`
     */
    @Query("SELECT * FROM Photos")
    fun getAllPhotos(): List<MyPhoto>
}