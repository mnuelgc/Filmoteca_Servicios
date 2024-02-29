package es.ua.eps.filmoteca

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    private val params = workerParams
    override fun doWork(): Result {
        Log.d(TAG, "Performing long running task in scheduled job")
        // TODO(developer): add long running task here.
       manageDataToFilm()

        return Result.success()
    }

    companion object {
        private val TAG = "MyWorker"
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun manageDataToFilm( )
    {
        val type = params.inputData.getString("type")
        val title = params.inputData.getString("title")
        val director = params.inputData.getString("director")
        val year = params.inputData.getString("year")
        val genre = params.inputData.getString("genre")
        val format = params.inputData.getString("format")
        val imdbUrl = params.inputData.getString("imdbUrl")
        val comments = params.inputData.getString("comments")
        val image = params.inputData.getString("image")

        //TODO
        //var imagesResId = R.mipmap.ic_launcher // Propiedades de la clase

        if (type.equals("add")){


            var index = 0
            var finded = false
            for(peli: Film in FilmDataSource.films) {
                if (peli.title.equals(title)) {
                    index = FilmDataSource.films.indexOf(peli)
                    finded = true
                }
            }

            var film = Film(applicationContext)

            film.title = title
            film.director = director
            film.year = year!!.toInt()
            film.genre = genre!!.toInt()
            film.format = format!!.toInt()
            film.imdbUrl = imdbUrl
            film.comments = comments
            film.imageUrl = image ?: ""

            if (finded)
            {
                FilmDataSource.films.set(index,film)
            }
            else{
                FilmDataSource.films.add(film)
            }


            Log.d("Film", film.toString())

            GlobalScope.launch(Dispatchers.Main) {
                FilmListFragment.reloadTable()
            }
            //   applicationContext.
        }
        else if (type.equals("delete"))
        {
            var index = 0
            var finded = false
            for(film: Film in FilmDataSource.films)
            {
                if (film.title.equals(title))
                {
                    index = FilmDataSource.films.indexOf(film)
                    finded = true
                }

                if (finded)
                {
                    FilmDataSource.films.removeAt(index)

                    GlobalScope.launch(Dispatchers.Main) {
                        FilmListFragment.reloadTable()
                    }
                }
            }
        }

    }
}