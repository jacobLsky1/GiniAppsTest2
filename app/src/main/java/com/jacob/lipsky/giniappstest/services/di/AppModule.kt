package com.jacob.lipsky.giniappstest.services.di

import android.content.Context
import androidx.room.Room
import com.jacob.lipsky.giniappstest.services.local.MyDatabase
import com.jacob.lipsky.giniappstest.services.local.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    /**
     * provides us with the context wherever we need it
     */
    @Singleton
    @Provides
    fun getContext( @ApplicationContext context: Context) = context


    /**
     * provides us with the database wherever we need it
     */
    @Provides
    @Singleton
    fun provideDatabase(context: Context): MyDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MyDatabase::class.java, "database"
        ).build()
    }

    /**
     * provides us with the database Dao wherever we need it
     */
    @Provides
    fun provideUserDao(database: MyDatabase): PhotoDao {
        return database.photoDao()
    }
}