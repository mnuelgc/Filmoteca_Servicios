package es.ua.eps.filmoteca

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import es.ua.eps.filmoteca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FilmListFragment.OnItemSelectedListener {
    lateinit var  prefs : SharedPreferences
    private val firebaseService : MyFirebaseMessagingService = MyFirebaseMessagingService()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs  = getSharedPreferences("firebasePrefs", Context.MODE_PRIVATE)


        if (savedInstanceState != null) return
        firebaseService.setContext(this)
        registrarDispositivo()
        askNotificationPermission();
        // Comprueba si estamos usando el layout dinámico
        if (findViewById<View?>(R.id.fragment_container) != null) {
            // Si se está restaurando, no hace falta cargar el fragmento
            if (savedInstanceState != null) return

            // Creamos el fragmento
            val listFragment = FilmListFragment()

            // Pasamos los extras del intent al fragmento
            listFragment.arguments = intent.extras

            // Añadimos el fragmento al contenedor
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, listFragment).commit()
        }
    }

    override fun onItemSelected(position: Int) {
        var dataFragmet = supportFragmentManager
            .findFragmentById(R.id.film_data_fragment) as FilmDataFragment?

        if (dataFragmet != null) {

            var listFragment = supportFragmentManager
                .findFragmentById(R.id.films_list_fragment) as FilmListFragment?
            dataFragmet.setFilmItem(position, listFragment)
        } else {
            dataFragmet = FilmDataFragment()
            val args = Bundle()
            args.putInt(FilmDataFragment.PARAM_POSICION, position)
            dataFragmet.arguments = args

            val t = supportFragmentManager.beginTransaction()
            t.replace(R.id.fragment_container, dataFragmet)
            t.addToBackStack(null)
            t.commit()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun registrarDispositivo()
    {
        var tokenSaved : String? = prefs.getString("tokenFirebase", "none")
        var suscribedToFilmTopic : Boolean = prefs.getBoolean("suscribedToFilmTopic", false)
        Log.d(TAGSP, "Token SharedPR: " + tokenSaved!!)
        Log.d(TAGSP, "Suscribe to film: " + suscribedToFilmTopic!!)
        if (tokenSaved.equals( "none")) {
            Log.d(TAGSP, "Inside")

            val editor = prefs.edit()
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                firebaseService.onNewToken(token)

                editor.putString("tokenFirebase", token)
                editor.apply()
            })
        }
        if(!suscribedToFilmTopic)
        {
            Firebase.messaging.subscribeToTopic("films")
                .addOnCompleteListener{task ->
                    var msg = "Suscribed"
                    if(!task.isSuccessful){
                        msg = "Suscribe failed"
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext,msg,Toast.LENGTH_SHORT).show()
                }
            val editor = prefs.edit()
            editor.putBoolean("suscribedToFilmTopic", true)
            editor.apply()


        }
        tokenSaved = prefs.getString("tokenFirebase", "none")
        Log.d(TAGSP, tokenSaved!!)
    }

    companion object{
        val TAGSP = "SHARED PREFERENCES"
    }

}