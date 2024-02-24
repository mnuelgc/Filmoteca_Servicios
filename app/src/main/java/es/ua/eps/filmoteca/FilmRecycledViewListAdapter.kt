package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Context
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class FilmRecycledViewListAdapter(val films : List<Film>, val context: Context) :
    RecyclerView.Adapter<FilmRecycledViewListAdapter.ViewHolder?>(){



    var actionMode : ActionMode? = null

    private var listener : (position: Int) -> Unit = {}
    private var listenerLong : (position: Int,) -> Unit = {}
    fun setOnItemCLickListener(listener: (position: Int ) -> Unit) {
        this.listener = listener
    }

    fun setOnItemLongCLickListener(listener: (position: Int ) -> Unit) {
        this.listenerLong = listener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film,parent, false)
        val holder = ViewHolder(v)

        v.setOnClickListener{
            val position : Int = holder.adapterPosition
            listener(position)
        }

        v.setOnLongClickListener{
            if (actionMode != null)
            {
                false
            } else{
                v.isSelected = true
                val position: Int = holder.adapterPosition

                listenerLong(position)
                true
            }

        }

        return holder
    }

    override fun getItemCount(): Int {
        return films.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, context )
    }
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var title: TextView
        var director: TextView
        var poster: ImageView



        fun bind(pos: Int, context: Context) {
            val film = FilmDataSource.films[pos]
            title.text = film.title
            director.text = film.director

            film.convertImageDrawableToBitmap(context)
            poster.setImageBitmap(film.imageBitmap)
        }

        init{
            title = v.findViewById(R.id.title)
            director = v.findViewById(R.id.director)
            poster = v.findViewById(R.id.itemImg)
        }
    }
}