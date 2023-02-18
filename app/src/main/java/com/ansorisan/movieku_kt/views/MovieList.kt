package com.ansorisan.movieku_kt.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.ansorisan.movieku_kt.adapters.MovieListAdapter
import com.ansorisan.movieku_kt.api.RequestState
import com.ansorisan.movieku_kt.databinding.ActivityMovieListBinding
import com.ansorisan.movieku_kt.listeners.OnMovieClickListener
import com.ansorisan.movieku_kt.models.Movies
import com.ansorisan.movieku_kt.viewmodels.MovieViewModel

class MovieList : AppCompatActivity() {
    private var _binding: ActivityMovieListBinding? = null
    private val binding get() = _binding!!
    private var adapter: MovieListAdapter? = null
    private var layoutManager: LayoutManager? = null
    private val viewModel: MovieViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestThenObserveAnychangeGenres()
        viewModel.getPopularMovie()
        observeAnychangePopularMovie()
        setupRecyclerView()

        adapter?.onMivieClickListener(object: OnMovieClickListener{
            override fun onMoveClick(movies: Movies, genres: String) {
                val intent = Intent(this@MovieList, MovieDetail::class.java)
                intent.putExtra(MovieDetail.movie, movies)
                intent.putExtra(MovieDetail.genres, genres)
                startActivity(intent)
            }

        })

        binding.searchButton.setOnClickListener {
            val query = binding.search.text.toString()
            when {
                query.isEmpty() -> binding.search.error = "Please insert a keyword!"
                else -> {
                    val intent = Intent(this, SearchMovie::class.java)
                    intent.putExtra(SearchMovie.query, query)
                    startActivity(intent)
                }
            }
        }
    }

    private fun observeAnychangePopularMovie() {
        viewModel.popularResponse.observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> showLoading()
                    is RequestState.Success -> {
                        hideLoading()
                        it.data?.results?.let { data -> adapter?.differ?.submitList(data.toList()) }
                    }
                    is RequestState.Error -> {
                        hideLoading()
                        Toast.makeText(this,it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun requestThenObserveAnychangeGenres() {
        viewModel.getGenres().observe(this) {
            if (it != null) {
                when (it) {
                    is RequestState.Loading -> {}
                    is RequestState.Success -> it.data.genres?.let { data -> adapter?.setGenres(data) }
                    is RequestState.Error -> Toast.makeText(this,it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MovieListAdapter()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.apply {
            movieList.adapter = adapter
            movieList.layoutManager = layoutManager
            movieList.addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object: OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                viewModel.getPopularMovie()
            }
        }
    }

    private fun showLoading() {
        binding.loading.show()
    }
    private fun hideLoading() {
        binding.loading.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}