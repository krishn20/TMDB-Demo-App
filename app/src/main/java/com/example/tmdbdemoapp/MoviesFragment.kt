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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdbdemoapp.databinding.FragmentMoviesBinding
import com.example.utils.ConnectivityManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.adapters.*
import com.example.models.Movie
import org.json.JSONException

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding get() = _binding!!
    private val token: String = "4cb5b12a720f6d1cb8afaaa06c983282"

    private val listOfMovies = arrayListOf<Movie>()
    private val listOfMoviesUpcoming = arrayListOf<Movie>()
    private val listOfMoviesTopRated = arrayListOf<Movie>()
    private val listOfMoviesPopular = arrayListOf<Movie>()

    private lateinit var movieNowPlayingAdapter: RecyclerMovieAdapter
    private lateinit var movieNowPlayingLayoutManager: RecyclerView.LayoutManager
    private lateinit var movieUpcomingAdapter: RecyclerMovieUpcomingAdapter
    private lateinit var movieUpcomingLayoutManager: RecyclerView.LayoutManager
    private lateinit var movieTopRatedAdapter: RecyclerMovieTopRatedAdapter
    private lateinit var movieTopRatedLayoutManager: RecyclerView.LayoutManager
    private lateinit var moviePopularAdapter: RecyclerMoviePopularAdapter
    private lateinit var moviePopularLayoutManager: RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMoviesBinding.inflate(inflater, container, false)

        movieNowPlayingLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        movieUpcomingLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        movieTopRatedLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        moviePopularLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)


        binding.progressBarMain.visibility = View.VISIBLE
        binding.progressBarUpcoming.visibility = View.VISIBLE
        binding.progressBarTopRated.visibility = View.VISIBLE
        binding.progressBarPopular.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val urlNowPlaying =
            "https://api.themoviedb.org/3/movie/now_playing?api_key=${token}&language=en-US&page=1"
        val urlUpcoming =
            "https://api.themoviedb.org/3/movie/upcoming?api_key=${token}&language=en-US&page=1"
        val urlTopRated =
            "https://api.themoviedb.org/3/movie/top_rated?api_key=${token}&language=en-US&page=1"
        val urlPopular =
            "https://api.themoviedb.org/3/movie/popular?api_key=${token}&language=en-US&page=1"

        var page = 0
        var message = "Sorry! There was some problem. Please try again later."

        /* ---------------------------------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------------------------------- */

        /* ------------------------------------------Now Playing Movies---------------------------------------------------------------- */


        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Method.GET, urlNowPlaying, null,

                Response.Listener {

                    try {
                        binding.progressBarMain.visibility = View.GONE

                        page = it.getInt("page")

                        if (page == 1) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val resultsJsonArray = it.getJSONArray("results")
                            listOfMovies.clear()

                            for (i in 0 until resultsJsonArray.length()) {

                                val movieJsonObject = resultsJsonArray.getJSONObject(i)
                                val movieObject = Movie(
                                    movieJsonObject.getInt("id"),
                                    movieJsonObject.getString("poster_path"),
                                    "Empty"
                                )

                                listOfMovies.add(movieObject)
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */



                            movieNowPlayingAdapter =
                                RecyclerMovieAdapter(activity as Context, listOfMovies)
                            binding.recyclerViewNowPlaying.adapter = movieNowPlayingAdapter
                            binding.recyclerViewNowPlaying.layoutManager =
                                movieNowPlayingLayoutManager


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

                            binding.progressBarMain.visibility = View.GONE
                            message = it.getString("status_message")

                            Toast.makeText(
                                activity as Context, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        binding.progressBarMain.visibility = View.GONE
                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        binding.progressBarMain.visibility = View.GONE
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

        /* ------------------------------------------Upcoming Movies------------------------------------------------------------------- */

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Method.GET, urlUpcoming, null,

                Response.Listener {

                    try {
                        binding.progressBarUpcoming.visibility = View.GONE

                        page = it.getInt("page")

                        if (page == 1) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val resultsJsonArray = it.getJSONArray("results")
                            listOfMoviesUpcoming.clear()

                            for (i in 0 until resultsJsonArray.length()) {

                                val movieJsonObject = resultsJsonArray.getJSONObject(i)
                                val movieObject = Movie(
                                    movieJsonObject.getInt("id"),
                                    movieJsonObject.getString("backdrop_path"),
                                    movieJsonObject.getString("title")
                                )

                                listOfMoviesUpcoming.add(movieObject)
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            movieUpcomingAdapter =
                                RecyclerMovieUpcomingAdapter(activity as Context, listOfMoviesUpcoming)
                            binding.recyclerViewUpcoming.adapter = movieUpcomingAdapter
                            binding.recyclerViewUpcoming.layoutManager =
                                movieUpcomingLayoutManager


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

                            binding.progressBarUpcoming.visibility = View.GONE
                            message = it.getString("status_message")

                            Toast.makeText(
                                activity as Context, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        binding.progressBarUpcoming.visibility = View.GONE
                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        binding.progressBarUpcoming.visibility = View.GONE
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

        /* ------------------------------------------Top Rated Movies------------------------------------------------------------------ */

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Method.GET, urlTopRated, null,

                Response.Listener {

                    try {
                        binding.progressBarTopRated.visibility = View.GONE

                        page = it.getInt("page")

                        if (page == 1) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val resultsJsonArray = it.getJSONArray("results")
                            listOfMoviesTopRated.clear()

                            for (i in 0 until resultsJsonArray.length()) {

                                val movieJsonObject = resultsJsonArray.getJSONObject(i)
                                val movieObject = Movie(
                                    movieJsonObject.getInt("id"),
                                    movieJsonObject.getString("backdrop_path"),
                                    movieJsonObject.getString("title")
                                )

                                listOfMoviesTopRated.add(movieObject)
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */



                            movieTopRatedAdapter =
                                RecyclerMovieTopRatedAdapter(activity as Context, listOfMoviesTopRated)
                            binding.recyclerViewTopRated.adapter = movieTopRatedAdapter
                            binding.recyclerViewTopRated.layoutManager =
                                movieTopRatedLayoutManager


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

                            binding.progressBarTopRated.visibility = View.GONE
                            message = it.getString("status_message")

                            Toast.makeText(
                                activity as Context, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        binding.progressBarTopRated.visibility = View.GONE
                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        binding.progressBarTopRated.visibility = View.GONE
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

        /* ------------------------------------------Popular Movies-------------------------------------------------------------------- */

        if (ConnectivityManager().checkConnectivity(activity as Context)) {

            val jsonRequest = object : JsonObjectRequest(
                Method.GET, urlPopular, null,

                Response.Listener {

                    try {
                        binding.progressBarPopular.visibility = View.GONE

                        page = it.getInt("page")

                        if (page == 1) {

                            /* ---------------------------------------------------------------------------------------------------------------------------- */

                            val resultsJsonArray = it.getJSONArray("results")
                            listOfMoviesPopular.clear()

                            for (i in 0 until resultsJsonArray.length()) {

                                val movieJsonObject = resultsJsonArray.getJSONObject(i)
                                val movieObject = Movie(
                                    movieJsonObject.getInt("id"),
                                    movieJsonObject.getString("backdrop_path"),
                                    movieJsonObject.getString("title")
                                )

                                listOfMoviesPopular.add(movieObject)
                            }

                            /* ---------------------------------------------------------------------------------------------------------------------------- */



                            moviePopularAdapter =
                                RecyclerMoviePopularAdapter(activity as Context, listOfMoviesPopular)
                            binding.recyclerViewPopular.adapter = moviePopularAdapter
                            binding.recyclerViewPopular.layoutManager =
                                moviePopularLayoutManager


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

                            binding.progressBarPopular.visibility = View.GONE
                            message = it.getString("status_message")

                            Toast.makeText(
                                activity as Context, message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    } catch (e: JSONException) {
                        binding.progressBarPopular.visibility = View.GONE
                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    if (activity != null) {
                        binding.progressBarPopular.visibility = View.GONE
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

        /* ---------------------------------------------------------------------------------------------------------------------------- */

        return binding.root
    }


}