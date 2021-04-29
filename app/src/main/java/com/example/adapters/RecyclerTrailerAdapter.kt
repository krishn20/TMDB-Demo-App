package com.example.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.models.MovieTrailer
import com.example.tmdbdemoapp.R

class RecyclerTrailerAdapter(
    private val context: Context,
    private val listOfMoviesTrailers: ArrayList<MovieTrailer>
) :
    RecyclerView.Adapter<RecyclerTrailerAdapter.MovieTrailerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerTrailerAdapter.MovieTrailerViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.trailer_list_item, parent, false)
        return MovieTrailerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerTrailerAdapter.MovieTrailerViewHolder, position: Int) {

        val movieTrailer = listOfMoviesTrailers[position]

        holder.trailerName.text = movieTrailer.trailer_name

        val baseUrlYoutube = "https://www.youtube.com/watch?v="
        val baseUrlVimeo = "https://vimeo.com/"
        var finalUrl = ""

        if(movieTrailer.trailer_site == "YouTube")
        {
            val movieTrailerKey = movieTrailer.trailer_link_key
            finalUrl = baseUrlYoutube + movieTrailerKey
        }
        else
        {
            val movieTrailerKey = movieTrailer.trailer_link_key
            finalUrl = baseUrlVimeo + movieTrailerKey
        }

        holder.trailerImageButton.setOnClickListener {
            navigateToMovieTrailer(finalUrl)
        }

    }

    override fun getItemCount(): Int {
        return listOfMoviesTrailers.size
    }

    class MovieTrailerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val trailerName: TextView = view.findViewById(R.id.textView_trailers_name)
        val trailerImageButton: ImageView = view.findViewById(R.id.play_image_button)

    }

    //-------------------------------------------------------------------------------------------------------//

    private fun navigateToMovieTrailer(movieTrailerLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movieTrailerLink))
        context.startActivity(intent)
    }


}