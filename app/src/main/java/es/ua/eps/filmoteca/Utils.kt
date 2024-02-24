package es.ua.eps.filmoteca

import android.graphics.Bitmap

object Utils {
    fun resizePhoto(bitmap: Bitmap): Bitmap {

        val w = bitmap.width.toFloat()
        val h = bitmap.height.toFloat()
        val aspRat : Float = (h / w)
        val W = 500
        val H = 850
        val b = Bitmap.createScaledBitmap(bitmap, W, H, false)

        return b
    }
}