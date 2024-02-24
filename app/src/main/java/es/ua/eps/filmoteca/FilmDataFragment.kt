package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import es.ua.eps.filmoteca.databinding.FragmentFilmDataBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilmDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilmDataFragment : Fragment() {

    var positionFilm: Int? = null
    var filmData: TextView? = null
    var image: ImageView? = null
    var directorName: TextView? = null
    var year: TextView? = null
    var genreAndFormat: TextView? = null
    var annotation: TextView? = null
    var filmListFragment : FilmListFragment? = null
    private val MOVIE_RESULT = 1


    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult -> onActivityResult(MOVIE_RESULT, result.resultCode, result.data) }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            positionFilm = it.getInt(PARAM_POSICION)
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        res = resources
        cont = requireContext()

        this.setHasOptionsMenu(true); //setHasOptionMenu(true) was not working

        if (activity?.findViewById<View?>(R.id.fragment_container) != null) {
            val actionBar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar
            actionBar?.setHomeButtonEnabled(true)
            actionBar?.setDisplayHomeAsUpEnabled(true)
        }
        if (savedInstanceState != null) {
            positionFilm = savedInstanceState.getInt(PARAM_POSICION)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val binding = FragmentFilmDataBinding.inflate(layoutInflater)
        val view = binding.root

        val extraIntent = activity?.intent

        var film: Film

        bindElements(binding)
        if (positionFilm != null) {

            film = FilmDataSource.films[positionFilm!!]
        } else {
            film = Film(cont)
        }

        bindElements(binding)
        SetFilmData(film)

        val buttonIMBD = binding.IMDBButton
        val buttonEdit = binding.editButton


        val intentEdit = Intent(activity, FilmEditActivity::class.java)


        buttonIMBD.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(film.imdbUrl))

            if (viewIntent.resolveActivity(activity?.packageManager!!) != null) {
                startActivity(viewIntent)
            }

        }

        buttonEdit.setOnClickListener {
            intentEdit.putExtra(EXTRA_FILM_ID, positionFilm)

            if (Build.VERSION.SDK_INT >= 30) {
                startForResult.launch(intentEdit)
            } else {
                @Suppress("DEPRECATION")
                startActivityForResult(intentEdit, MOVIE_RESULT)
            }


        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilmDataFragment.
         */
        // TODO: Rename and change types and number of parameters
        lateinit var res: Resources
        lateinit var cont: Context
        const val PARAM_POSICION = "PARAM_POSICION"
        const val EXTRA_FILM_ID = "EXTRA_FILM_ID"


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilmDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun bindElements(binding: FragmentFilmDataBinding) {
        filmData = binding.filmData
        image = binding.imgFilm
        directorName = binding.directorName
        year = binding.yearValue
        genreAndFormat = binding.genreFormat
        annotation = binding.annotations
    }

    private fun SetFilmData(film: Film) {
        filmData?.text = film.title

        val genresArr = resources.getStringArray(R.array.Genres)
        val posGenre = film.genre as Int
        val genre = genresArr!![posGenre!!]
        val formatArr = resources.getStringArray(R.array.Formats)
        val posFormat = film.format as Int
        val format = formatArr[posFormat!!]

        genreAndFormat?.text = "$genre $format"
        annotation?.text = film.comments

        image?.setImageBitmap(film.imageBitmap)

        directorName?.text = film.director
        year?.text = film.year.toString()

    }


    public fun setFilmItem(position: Int, filmListFragment : FilmListFragment?) {
        positionFilm = position
        val film: Film = FilmDataSource.films[position]
        this.filmListFragment = filmListFragment
        SetFilmData(film)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (positionFilm != null) {
            outState.putInt(PARAM_POSICION, positionFilm!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            MOVIE_RESULT -> if (resultCode == Activity.RESULT_OK) {
                val film: Film = FilmDataSource.films[positionFilm!!]
                this.filmListFragment?.adapter?.notifyDataSetChanged()
                SetFilmData(film)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            val actionBar: ActionBar? = (activity as AppCompatActivity?)!!.supportActionBar
            actionBar?.setHomeButtonEnabled(false)
            actionBar?.setDisplayHomeAsUpEnabled(false)
            activity?.supportFragmentManager?.popBackStack()
        }
        return super.onOptionsItemSelected(item)

    }
}