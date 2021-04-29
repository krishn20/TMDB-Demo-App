package com.example.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.models.Movie
import com.example.tmdbdemoapp.MoviesFragmentDirections
import com.example.tmdbdemoapp.R
import com.squareup.picasso.Picasso

class RecyclerMovieAdapter(
    private val context: Context,
    private val listOfMovies: ArrayList<Movie>
) :
    RecyclerView.Adapter<RecyclerMovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerMovieAdapter.MovieViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.movie_now_playing_list_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerMovieAdapter.MovieViewHolder, position: Int) {

        val movie = listOfMovies[position]

        val baseUrl = "https://image.tmdb.org/t/p/original"
        val movieImgUrl = movie.movie_img
        val finalUrl = baseUrl + movieImgUrl

        Picasso.get().load(finalUrl)
            .resize(1100, 1550)
            .error(R.drawable.ic_error)
            .into(holder.movieImageNowPlaying)

        holder.cardViewNowPlaying.setOnClickListener {
            navigateToMovieDetails(it, movie.movie_id)
        }

    }

    override fun getItemCount(): Int {
        return listOfMovies.size
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cardViewNowPlaying: CardView = view.findViewById(R.id.cardViewNowPlaying)
        val movieImageNowPlaying: ImageView = view.findViewById(R.id.movieImageNowPlaying)
    }

    //-------------------------------------------------------------------------------------------------------//

    private fun navigateToMovieDetails(v: View, movieID: Int) {
        val directions =
            MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(movieID)
        v.findNavController().navigate(directions)
    }


}