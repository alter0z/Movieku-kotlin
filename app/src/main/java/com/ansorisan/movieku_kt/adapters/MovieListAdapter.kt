package com.ansorisan.movieku_kt.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ansorisan.movieku_kt.BuildConfig
import com.ansorisan.movieku_kt.databinding.MovieListBinding
import com.ansorisan.movieku_kt.listeners.OnMovieClickListener
import com.ansorisan.movieku_kt.models.Genres
import com.ansorisan.movieku_kt.models.Movies
import com.bumptech.glide.Glide

class MovieListAdapter: RecyclerView.Adapter<MovieListAdapter.ViewHolder>() {
    private lateinit var onMivieClickListener: OnMovieClickListener
    private val genreList = ArrayList<Genres>()

    fun onMivieClickListener(onMivieClickListener: OnMovieClickListener) {
        this.onMivieClickListener = onMivieClickListener
    }

    fun setGenres(list: List<Genres>) {
        this.genreList.clear()
        this.genreList.addAll(list)
    }

    private val differCallback = object: DiffUtil.ItemCallback<Movies>() {
        override fun areItemsTheSame(oldItem: Movies, newItem: Movies): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: Movies, newItem: Movies): Boolean = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)

    inner class ViewHolder(val binding: MovieListBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(differ.currentList[position]){
                binding.apply {
                    title.text = originalTitle
                    lang.text = originalLanguage
                    releaseDate.text = differ.currentList[position].releaseDate
                    ratingText.text = voteAverage.toString()
                    ratingBar.rating = voteAverage?.div(2) ?: 0f

                    Glide.with(itemView).load("${BuildConfig.PHOTO_BASE_URL}$posterPath").into(poster)

                    val map = genreList.associate { it.id to it.name }
                    val genres = StringBuilder()

                    val genresId = ArrayList<Int>()
                    if (genreIds != null) {
                        genresId.addAll(genreIds)
                        for (data in genreIds) {
                            genres.append("${map[data]}, ")
                        }
                    }

                    genre.text = genres.dropLast(2)

                    itemView.setOnClickListener { onMivieClickListener.onMoveClick(this@with, genres.toString()) }
                }
            }
        }
    }
}