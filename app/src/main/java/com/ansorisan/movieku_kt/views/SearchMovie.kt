package com.ansorisan.movieku_kt.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ansorisan.movieku_kt.adapters.MovieListAdapter
import com.ansorisan.movieku_kt.api.RequestState
import com.ansorisan.movieku_kt.databinding.ActivitySearchMovieBinding
import com.ansorisan.movieku_kt.listeners.OnMovieClickListener
import com.ansorisan.movieku_kt.models.Movies
import com.ansorisan.movieku_kt.viewmodels.MovieViewModel

class SearchMovie : AppCompatActivity() {
    private var _binding: ActivitySearchMovieBinding? = null
    private val binding get() = _binding!!
    private var adapter: MovieListAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val viewModel: MovieViewModel by viewModels()
    private var isSearchAgain = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestThenObserveAnychangeGenres()

        binding.search.setText(intent.getStringExtra(query))

        if (!isSearchAgain) viewModel.searchMovie(binding.search.text.toString())

        binding.searchButton.setOnClickListener {
            val query = binding.search.text.toString()
            when {
                query.isEmpty() -> binding.search.error = "Please insert a keyword!"
                else -> {
                    isSearchAgain = true
                    viewModel.searchMovie(query)
                }
            }
        }

        observeAnychangeSearchMovie()
        setupRecyclerView()

        adapter?.onMivieClickListener(object: OnMovieClickListener {
            override fun onMoveClick(movies: Movies, genres: String) {
                val intent = Intent(this@SearchMovie, MovieDetail::class.java)
                intent.putExtra(MovieDetail.movie, movies)
                intent.putExtra(MovieDetail.genres, genres)
                startActivity(intent)
            }

        })
    }

    private fun observeAnychangeSearchMovie() {
        viewModel.searchResponse.observe(this) {
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

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                viewModel.searchMovie(binding.search.text.toString())
            }
        }
    }

    private fun showLoading() {
        binding.loading.show()
    }
    private fun hideLoading() {
        binding.loading.hide()
    }

    companion object {
        const val query = "query"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}