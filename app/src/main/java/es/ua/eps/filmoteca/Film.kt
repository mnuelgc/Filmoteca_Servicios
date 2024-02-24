package es.ua.eps.filmoteca

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream

class Film(context: Context?) {
    private val baos = ByteArrayOutputStream()
    val PREFERRED_IMAGE_SIZE = 400  //400kb
    val ONE_MB_TO_KB = 1024

    var imagesResId = R.mipmap.ic_launcher // Propiedades de la clase
    var title: String? = ""
    var director: String? = ""
    var year = 0
    var genre = 0
    var format = 0
    var imdbUrl: String? = "http://google.com"
    var comments: String? = null
    public lateinit var imageBitmap: Bitmap

    init{
        if(context != null){
            convertImageDrawableToBitmap(context)
        }
    }
    override fun toString(): String {
        return title ?: "<Sin titulo>"
    } //Al convertir a cadena mostramos su título

    companion object {
        const val FORMAT_DVD = 0 // Formatos
        const val FORMAT_BLURAY = 1
        const val FORMAT_DIGITAL = 2
        const val FORMAT_ACTION = 0 // Generos
        const val FORMAT_COMEDY = 1
        const val FORMAT_DRAMA = 2
        const val FORMAT_SCIFI = 3
        const val FORMAT_HORROR = 4

    }

    public fun convertImageDrawableToBitmap(context: Context) {

        if(!this::imageBitmap.isInitialized){

            imageBitmap=  ContextCompat.getDrawable(context, imagesResId)!!.toBitmap()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)

            //if compressed picture is greater than 400kb, than to reduce size
            if (baos.toByteArray().size / ONE_MB_TO_KB > PREFERRED_IMAGE_SIZE) {

                //resize photo & set Image in imageview In UI
                imageBitmap = Utils.resizePhoto(imageBitmap)
            }
        }
    }
}