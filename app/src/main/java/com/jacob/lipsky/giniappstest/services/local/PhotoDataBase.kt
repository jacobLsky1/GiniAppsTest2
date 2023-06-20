package com.jacob.lipsky.giniappstest.services.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jacob.lipsky.giniappstest.models.MyPhoto

@Database(entities = [MyPhoto::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}