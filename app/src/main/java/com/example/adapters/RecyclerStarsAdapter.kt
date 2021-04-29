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
import com.example.models.MovieCast
import com.example.models.MovieTrailer
import com.example.tmdbdemoapp.R

class RecyclerStarsAdapter(
    private val context: Context,
    private val listOfMoviesStars: ArrayList<MovieCast>
) :
    RecyclerView.Adapter<RecyclerStarsAdapter.StarsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerStarsAdapter.StarsViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.starring_list_item, parent, false)
        return StarsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerStarsAdapter.StarsViewHolder, position: Int) {

        val movieTrailer = listOfMoviesStars[position]
        holder.starName.text = movieTrailer.castName

    }

    override fun getItemCount(): Int {
        return listOfMoviesStars.size
    }

    class StarsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val starName: TextView = view.findViewById(R.id.movie_star_name)

    }

    //-------------------------------------------------------------------------------------------------------//


}