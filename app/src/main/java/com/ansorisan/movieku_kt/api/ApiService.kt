package com.ansorisan.movieku_kt.api

import com.ansorisan.movieku_kt.models.GenreResponse
import com.ansorisan.movieku_kt.models.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    suspend fun getPopularMovie(
        @Query("api_key") key: String?,
        @Query("page") page: Int?
    ): Response<MovieResponse>

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") key: String?,
        @Query("query") query: String?,
        @Query("page") page: Int?
    ): Response<MovieResponse>

    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") key: String?
    ): GenreResponse
}