package es.ua.eps.filmoteca

import android.content.res.Resources
import android.content.Context

object FilmDataSource {

    val films: MutableList<Film> = mutableListOf<Film>()

    init {
        var f = Film(null)
        f.title = FilmListFragment.res.getString(R.string.Back_to_the_future)
        f.director = "Robert Zemeckis"
        f.imagesResId = R.mipmap.ic_launcher
        f.comments  =  FilmListFragment.res.getString(R.string.Back_to_the_future_annotations)
        f.format = Film.Companion.FORMAT_DIGITAL
        f.genre = Film.Companion.FORMAT_SCIFI
        f.imdbUrl  ="http://www.imdb.com/title/tt0088763"
        f.year = 1985
        f.convertImageDrawableToBitmap(FilmListFragment.cont)
        f.lattitude = 34.098907
        f.longitude = -118.327759
        f.hasFence = false

        films.add(f)

        f = Film(null)
        f.title =  FilmListFragment.res.getString(R.string.Spiderman)
        f.director = "Joaquim Dos Santos"
        f.imagesResId = R.drawable.spiderposter
        f.comments  = FilmListFragment.res.getString(R.string.SpidermanAnnotations)
        f.format = Film.Companion.FORMAT_DIGITAL
        f.genre = Film.Companion.FORMAT_ACTION
        f.imdbUrl  ="http://www.imdb.com/title/tt9362722"
        f.year = 2023
        f.convertImageDrawableToBitmap(FilmListFragment.cont)
        f.lattitude = 40.650002
        f.longitude = -73.949997
        f.hasFence = true

        films.add(f)

        f = Film(null)
        f.title =FilmListFragment.res.getString(R.string.Scarface)
        f.director = "Brian de Palma"
        f.imagesResId = R.drawable.scarface
        f.comments  =  FilmListFragment.res.getString(R.string.ScarfaceAnnotations)
        f.format = Film.Companion.FORMAT_DVD
        f.genre = Film.Companion.FORMAT_ACTION
        f.imdbUrl  ="http://www.imdb.com/title/tt9362722"
        f.year = 1983
        f.convertImageDrawableToBitmap(FilmListFragment.cont)
        f.lattitude = 21.521757
        f.longitude = -77.781167
        f.hasFence = true

        films.add(f)

    }
}