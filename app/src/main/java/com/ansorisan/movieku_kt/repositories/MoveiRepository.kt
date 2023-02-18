package com.ansorisan.movieku_kt.repositories

import com.ansorisan.movieku_kt.BuildConfig
import com.ansorisan.movieku_kt.api.ApiConfig

class MoveiRepository {
    private val client = ApiConfig.getApiService()

    suspend fun getPopularMovie(page: Int) = client.getPopularMovie(BuildConfig.API_KEY, page)
    suspend fun searchMovie(query: String, page: Int) = client.searchMovie(BuildConfig.API_KEY, query, page)
    suspend fun getGenres() = client.getGenres(BuildConfig.API_KEY)
}