package com.jacob.lipsky.giniappstest.services.di

import com.jacob.lipsky.giniappstest.services.MainRepository
import com.jacob.lipsky.giniappstest.services.local.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideMainRepository(photoDao: PhotoDao): MainRepository {
        return MainRepository(photoDao)
    }
}