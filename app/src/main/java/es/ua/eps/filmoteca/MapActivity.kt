package es.ua.eps.filmoteca

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.health.connect.changelog.ChangeLogsResponse.DeletedLog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient


    companion object{
        const val EXTRA_FILM_NAME = "EXTRA_FILM_NAME"
        const val EXTRA_DIRECTOR_NAME = "EXTRA_DIRECTOR_NAME"
        const val EXTRA_YEAR = "EXTRA_YEAR"
        const val EXTRA_LONGITUDE_ID = "EXTRA_LONGITUDE_ID"
        const val EXTRA_LATTITUDE_ID = "EXTRA_LATTITUDE_ID"
        const val EXTRA_HAS_FENCE_ID = "EXTRA_HAS_FENCE_ID"
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

        geofencingClient = LocationServices.getGeofencingClient(this)

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) { return }


        val extraIntent = intent
        val title = extraIntent.getStringExtra(EXTRA_FILM_NAME)
        val director = extraIntent.getStringExtra(EXTRA_DIRECTOR_NAME)
        val year = extraIntent.getIntExtra(EXTRA_YEAR, 0)
        val longitude = extraIntent.getDoubleExtra(EXTRA_LONGITUDE_ID, 0.0)
        val lattitude = extraIntent.getDoubleExtra(EXTRA_LATTITUDE_ID, 0.0)
        val hasFence = extraIntent.getBooleanExtra(EXTRA_HAS_FENCE_ID, false)

        Log.d("longitude", longitude.toString())
        Log.d("lattitude", lattitude.toString())

        mMap.clear()

        val filmPosition = LatLng(lattitude, longitude)
        val filmMarker = mMap.addMarker(
            MarkerOptions()
                .position(filmPosition)
                .title(title)
                .snippet("Director: $director" + " Año: $year")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(filmPosition))
        filmMarker?.showInfoWindow()

        mMap.isMyLocationEnabled = true
        if (hasFence == true) {
            createFence(title!!, lattitude, longitude, 500.0f)
            drawCircle(filmPosition, 500.0)
        }
    }

    private fun drawCircle(pos: LatLng, radius: Double) {
        val circleOptions = CircleOptions()
            .center(pos)
            .radius(radius)
            .strokeColor(Color.argb(255, 125, 125, 0))
            .fillColor(Color.argb(120, 255, 255, 0))
            .strokeWidth(4F)
        mMap.addCircle(circleOptions)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if(id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createFence(geoId : String, latt : Double, long : Double, radius : Float )
    {
        val geofence = Geofence.Builder()
            .setRequestId(geoId)
            .setCircularRegion(
                latt,
                long,
                radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setLoiteringDelay(5000)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 999)
            } else {
                geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        Log.d("MapActivity", "GEOCERCADO AÑADIDO")
                    }
                    addOnFailureListener {
                        Log.d("MapActivity", "GEOCERCADO NO SE PUEDE AÑADIR")

                    }
                }
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    Log.d("MapActivity", "ALT GEOCERCADO AÑADIDO")
                }
                addOnFailureListener {
                    Log.d("MapActivity", "ALT GEOCERCADO NO SE PUEDE AÑADIR")

                }
            }
        }

    }

    private val geofencePendingIntent: PendingIntent by lazy {
        Log.d("MapActivity", "insidependingintent")

        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        val flags : Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        PendingIntent.getBroadcast(this, 0, intent, flags)
    }
}

