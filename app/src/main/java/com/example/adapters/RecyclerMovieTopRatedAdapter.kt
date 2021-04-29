package com.example.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.models.Movie
import com.example.tmdbdemoapp.MoviesFragmentDirections
import com.example.tmdbdemoapp.R
import com.squareup.picasso.Picasso

class RecyclerMovieTopRatedAdapter(
    private val context: Context,
    private val listOfMovies: ArrayList<Movie>
) :
    RecyclerView.Adapter<RecyclerMovieTopRatedAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerMovieTopRatedAdapter.MovieViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.other_movies_list_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerMovieTopRatedAdapter.MovieViewHolder, position: Int) {

        val movie = listOfMovies[position]

        val baseUrl = "https://image.tmdb.org/t/p/original"
        val movieImgUrl = movie.movie_img
        val finalUrl = baseUrl + movieImgUrl

        holder.movieNameOthers.text = movie.movie_title

        Picasso.get().load(finalUrl)
            .resize(1050, 680)
            .error(R.drawable.ic_error)
            .into(holder.movieImageOthers)

        holder.cardViewOthers.setOnClickListener {
            navigateToMovieDetails(it, movie.movie_id)
        }

    }

    override fun getItemCount(): Int {
        return listOfMovies.size
    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val cardViewOthers: CardView = view.findViewById(R.id.cardViewOtherMovies)
        val movieImageOthers: ImageView = view.findViewById(R.id.movieImageOtherMovies)
        val movieNameOthers: TextView = view.findViewById(R.id.movieNameOtherMovies)
    }

    //-------------------------------------------------------------------------------------------------------//

    private fun navigateToMovieDetails(v: View, movieID: Int) {
        val directions =
            MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(movieID)
        v.findNavController().navigate(directions)
    }


}