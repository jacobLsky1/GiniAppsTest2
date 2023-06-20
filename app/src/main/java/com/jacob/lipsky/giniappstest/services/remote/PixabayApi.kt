package com.jacob.lipsky.giniappstest.services.remote

import com.jacob.lipsky.giniappstest.models.PixabayPhotos
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {

    /**
     * Represents the extention call for the pixabay api
     *
     * @Query page page of results.
     * @Query key the api key.
     * @Query min_comments minimum amount of comments.
     * @Query min_likes minimum amount of likes.
     * @Query per_page is 10 because i need to download them first and send them to the database and only then display to user
     */

    @GET("api/")
    fun getPhotos(
        @Query("page") page: Int,
        @Query("key") key: String = "13398314-67b0a9023aca061e2950dbb5a",
        @Query("min_comments") comments: Int = 50,
        @Query("min_likes") likes: Int = 50,
        @Query("per_page") per_page: Int = 10,
    ): Call<PixabayPhotos>
}