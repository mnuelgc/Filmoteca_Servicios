package es.ua.eps.filmoteca

import android.health.connect.changelog.ChangeLogsResponse.DeletedLog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap


    companion object{
        const val EXTRA_FILM_NAME = "EXTRA_FILM_NAME"
        const val EXTRA_DIRECTOR_NAME = "EXTRA_DIRECTOR_NAME"
        const val EXTRA_YEAR = "EXTRA_YEAR"
        const val EXTRA_LONGITUDE_ID = "EXTRA_LONGITUDE_ID"
        const val EXTRA_LATTITUDE_ID = "EXTRA_LATTITUDE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val extraIntent = intent
        val title = extraIntent.getStringExtra(EXTRA_FILM_NAME)
        val director = extraIntent.getStringExtra(EXTRA_DIRECTOR_NAME)
        val year = extraIntent.getIntExtra(EXTRA_YEAR,0)
        val longitude = extraIntent.getDoubleExtra(EXTRA_LONGITUDE_ID,0.0)
        val lattitude = extraIntent.getDoubleExtra(EXTRA_LATTITUDE_ID,0.0)

        Log.d("longitude", longitude.toString())
        Log.d("lattitude", lattitude.toString())


        val filmPosition = LatLng(lattitude, longitude)
        val filmMarker = mMap.addMarker(
            MarkerOptions()
                .position(filmPosition)
                .title(title)
                .snippet("Director: $director" + " Año: $year")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(filmPosition))
        filmMarker?.showInfoWindow()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if(id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

