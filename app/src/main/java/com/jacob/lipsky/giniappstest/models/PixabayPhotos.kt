package com.jacob.lipsky.giniappstest.models

/**
 * the object that we get from the pixabay api
 */
data class PixabayPhotos(
    val hits: List<Photo>,
    val total: Int,
    val totalHits: Int
)