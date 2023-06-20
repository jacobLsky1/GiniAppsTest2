package com.jacob.lipsky.giniappstest.models

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.jacob.lipsky.giniappstest.services.local.BitmapTypeConverter
import java.util.UUID

/**
 * Represents a user in the system.
 *
 * @property id The unique identifier of the photo.
 * @property bitmap holds the bitmap so you can see it offline.
 * @property dimensions discribes the quality of the bitmap.
 */

@Entity(tableName = "photos")
@TypeConverters(BitmapTypeConverter::class)
data class MyPhoto(
    @PrimaryKey
    val id: Int,
    val likes: Int,
    val comments: Int,
    val bitmap: Bitmap,
    val fileName: String,
    val dimensions: String,
    val creationDate: Long,

)