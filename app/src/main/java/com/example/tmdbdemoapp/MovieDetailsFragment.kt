package com.example.tmdbdemoapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.adapters.RecyclerMoviePopularAdapter
import com.example.adapters.RecyclerStarsAdapter
import com.example.adapters.RecyclerTrailerAdapter
import com.example.models.MovieCast
import com.example.models.MovieTrailer
import com.example.tmdbdemoapp.databinding.FragmentMovieDetailsBinding
import com.example.utils.ConnectivityManager
import com.squareup.picasso.Picasso
import org.json.JSONException

class MovieDetailsFragment : Fragment() {

    private val args: MovieDetailsFragmentArgs by navArgs()

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val token: String = "4cb5b12a720f6d1cb8afaaa06c983282"
    private var movieID: Int = 0

    private val listOfMoviesTrailers = arrayListOf<MovieTrailer>()
    private val listOfMoviesStars = arrayListOf<MovieCast>()

    private lateinit var movieTrailerAdapter: RecyclerTrailerAdapter
    private lateinit var movieTrailerLayoutManager: RecyclerView.LayoutManager
    private lateinit var movieStarsAdapter: RecyclerStarsAdapter
    private lateinit var movieStarsLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        movieID = args.movieID

        movieTrailerLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        movieStarsLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.progressBarInfo.visibility = View.VISIBLE
        binding.progressBarTrailers.visibility = View.VISIBLE
        binding.progressBarCasting.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(activity as Context)

        val urlGetMovieDetails =
            "https://api.themoviedb.org/3/movie/${movieID}?api_key=${token}&language=en-US"
        val urlGetMovieTrailers =
            "https://api.themoviedb.org/3/movie/${movieID}/videos?api_key=${token}&language=en-US"
        val urlGetMovieStars =
            "https://api.themoviedb.org/3/movie/${movieID}/credits?api_key=${token}&language=en-US"

        var id = 0
        var message = "Sorry! There was some problem. Please try again later."

        /* ---------------------------------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------------------- */

        /* ------------------------------------------Get Movie Details----------------------------------------------------------------- */

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Method.GET, urlGetMovieDetails, null,

                Response.Listener {

                    try {
                        binding.progressBarInfo.visibility = View.GONE

                        id = it.getInt("id")

                        if (id == movieID) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            binding.textViewMovieInfoName.text = it.getString("original_title")

                            val baseUrl = "https://image.tmdb.org/t/p/w500"
                            val backdropPath = it.getString("backdrop_path")
                            val finalUrl = baseUrl + backdropPath

                            Picasso.get().load(finalUrl)
                                .resize(2000, 1250)
                                .error(R.drawable.ic_error)
                                .into(binding.imageViewInfoImage)

                            binding.textViewGenre.text =
                                it.getJSONArray("genres").getJSONObject(0).getString("name")
                            binding.textViewReleaseDate.text = it.getString("release_date")
                            binding.textViewRuntime.text =
                                it.getInt("runtime").toString() + resources.getString(R.string.mins)

                            binding.textViewMovieJist.text = it.getString("overview")

                            binding.textViewRatingNumber.text = it.getInt("vote_average")
                                .toString() + resources.getString(R.string.outOf10)

                        }

                        /* ---------------------------------------------------------------------------------------------------------------------------- */

                        //Errors and Exceptions Catches

                        else {

                            binding.progressBarInfo.visibility = View.GONE
                            message = it.getString("status_message")

                            Toast.makeText(
                                activity as Context, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        binding.progressBarInfo.visibility = View.GONE
                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        binding.progressBarInfo.visibility = View.GONE
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"

                    return headers

                }
            }

            queue.add(jsonRequest)

        }
        else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure!")
            dialog.setMessage("Couldn't connect to the Internet")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        /* ------------------------------------------Get Movie Trailers----------------------------------------------------------------- */

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Method.GET, urlGetMovieTrailers, null,

                Response.Listener {

                    try {
                        binding.progressBarTrailers.visibility = View.GONE

                        id = it.getInt("id")

                        if (id == movieID) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val trailersJsonArray = it.getJSONArray("results")
                            listOfMoviesTrailers.clear()

                            for (i in 0 until trailersJsonArray.length()) {

                                val trailerJsonObject = trailersJsonArray.getJSONObject(i)
                                val trailerObject = MovieTrailer(
                                    trailerJsonObject.getString("name"),
                                    trailerJsonObject.getString("site"),
                                    trailerJsonObject.getString("key")
                                )

                                listOfMoviesTrailers.add(trailerObject)
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */



                            movieTrailerAdapter =
                                RecyclerTrailerAdapter(activity as Context, listOfMoviesTrailers)
                            binding.recyclerViewTrailers.adapter = movieTrailerAdapter
                            binding.recyclerViewTrailers.layoutManager =
                                movieTrailerLayoutManager


//                            binding.recyclerViewNowPlaying.addItemDecoration(
//                                DividerItemDecoration(
//                                    binding.recyclerViewNowPlaying.context,
//                                    (movieNowPlayingLayoutManager as LinearLayoutManager).orientation
//                                )
//                            )

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                        }

                        /* ---------------------------------------------------------------------------------------------------------------------------- */

                        //Errors and Exceptions Catches

                        else {

                            binding.progressBarTrailers.visibility = View.GONE
                            message = it.getString("status_message")

                            Toast.makeText(
                                activity as Context, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        binding.progressBarTrailers.visibility = View.GONE
                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        binding.progressBarTrailers.visibility = View.GONE
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"

                    return headers

                }
            }

            queue.add(jsonRequest)

        }
        else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure!")
            dialog.setMessage("Couldn't connect to the Internet")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        /* ------------------------------------------Get Movie Casting----------------------------------------------------------------- */

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Method.GET, urlGetMovieStars, null,

                Response.Listener {

                    try {
                        binding.progressBarCasting.visibility = View.GONE

                        id = it.getInt("id")

                        if (id == movieID) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val castJsonArray = it.getJSONArray("cast")
                            listOfMoviesStars.clear()

                            for (i in 0 until 10) {

                                val castJsonObject = castJsonArray.getJSONObject(i)
                                val castObject = MovieCast(
                                    castJsonObject.getString("original_name"),
                                    castJsonObject.getString("known_for_department")
                                )

                                listOfMoviesStars.add(castObject)
                            }


                            for (i in 0 until castJsonArray.length()) {
                                val castJsonObject = castJsonArray.getJSONObject(i)
                                if (castJsonObject.getString("known_for_department") == "Directing") {
                                    binding.textViewDirectorName.text =
                                        castJsonObject.getString("original_name")
                                }
                            }

                            for (i in 0 until castJsonArray.length()) {
                                val castJsonObject = castJsonArray.getJSONObject(i)
                                if (castJsonObject.getString("known_for_department")
                                        .equals("Production")
                                ) {
                                    binding.textViewProducerName.text =
                                        castJsonObject.getString("name")
                                }
                            }

                            for (i in 0 until castJsonArray.length()) {
                                val castJsonObject = castJsonArray.getJSONObject(i)
                                if (castJsonObject.getString("known_for_department")
                                        .equals("Writing")
                                ) {
                                    binding.textViewWriterName.text =
                                        castJsonObject.getString("name")
                                }
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */



                            movieStarsAdapter =
                                RecyclerStarsAdapter(activity as Context, listOfMoviesStars)
                            binding.recyclerViewStarring.adapter = movieStarsAdapter
                            binding.recyclerViewStarring.layoutManager =
                                movieStarsLayoutManager


//                            binding.recyclerViewNowPlaying.addItemDecoration(
//                                DividerItemDecoration(
//                                    binding.recyclerViewNowPlaying.context,
//                                    (movieNowPlayingLayoutManager as LinearLayoutManager).orientation
//                                )
//                            )

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                        }

                        /* ---------------------------------------------------------------------------------------------------------------------------- */

                        //Errors and Exceptions Catches

                        else {

                            binding.progressBarCasting.visibility = View.GONE
                            message = it.getString("status_message")

                            Toast.makeText(
                                activity as Context, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        binding.progressBarCasting.visibility = View.GONE
                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        binding.progressBarCasting.visibility = View.GONE
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"

                    return headers

                }
            }

            queue.add(jsonRequest)

        }
        else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Failure!")
            dialog.setMessage("Couldn't connect to the Internet")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return binding.root
    }

}