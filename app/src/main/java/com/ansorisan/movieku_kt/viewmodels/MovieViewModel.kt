package com.ansorisan.movieku_kt.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ansorisan.movieku_kt.api.RequestState
import com.ansorisan.movieku_kt.models.GenreResponse
import com.ansorisan.movieku_kt.models.MovieResponse
import com.ansorisan.movieku_kt.repositories.MoveiRepository
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

class MovieViewModel: ViewModel() {
    private val repo: MoveiRepository = MoveiRepository()
    private var popularPage = 1
    private var searchPage = 1
    private var popularMovieResponse: MovieResponse? = null
    private var searchMovieResponse: MovieResponse? = null
    private var _popularResponse = MutableLiveData<RequestState<MovieResponse?>>()
    var popularResponse: LiveData<RequestState<MovieResponse?>> = _popularResponse
    private var _searchResponse = MutableLiveData<RequestState<MovieResponse?>>()
    var searchResponse: LiveData<RequestState<MovieResponse?>> = _searchResponse

    fun getPopularMovie() {
        viewModelScope.launch {
            _popularResponse.postValue(RequestState.Loading)
            val response = repo.getPopularMovie(popularPage)
            _popularResponse.postValue(handlePopularMovieResponse(response))
        }
    }

    private fun handlePopularMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                popularPage++
                if (popularMovieResponse == null) popularMovieResponse = it else {
                    val oldMovies = popularMovieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }

            RequestState.Success(popularMovieResponse ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string()?.let {
                    JSONObject(it).get("status_message")
                }
            } catch (e: JSONException) {
                e.localizedMessage
            } as String
        )
    }

    fun searchMovie(query: String) {
        viewModelScope.launch {
            _searchResponse.postValue(RequestState.Loading)
            val response = repo.searchMovie(query, searchPage)
            _searchResponse.postValue(handlesearchMovieResponse(response))
        }
    }

    private fun handlesearchMovieResponse(response: Response<MovieResponse>): RequestState<MovieResponse?> {
        return if (response.isSuccessful) {
            response.body()?.let {
                searchPage++
                if (searchMovieResponse == null) searchMovieResponse = it else {
                    val oldMovies = searchMovieResponse?.results
                    val newMovies = it.results
                    oldMovies?.addAll(newMovies)
                }
            }

            RequestState.Success(searchMovieResponse ?: response.body())
        } else RequestState.Error(
            try {
                response.errorBody()?.string()?.let {
                    JSONObject(it).get("status_message")
                }
            } catch (e: JSONException) {
                e.localizedMessage
            } as String
        )
    }

    fun getGenres(): LiveData<RequestState<GenreResponse>> = liveData {
        try {
            val response = repo.getGenres()
            emit(RequestState.Success(response))
        } catch (e: HttpException) {
            e.response()?.errorBody()?.string()?.let { RequestState.Error(it) }?.let { emit(it) }
        }
    }
}