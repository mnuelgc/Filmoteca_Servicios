package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class User_Sing_In_Activity : AppCompatActivity() {

    private  lateinit var  googleSingIn :Button
    private  lateinit var  gso : GoogleSignInOptions
    private lateinit var gsc : GoogleSignInClient

    private var mInterstitialAd: InterstitialAd? = null

    private val SINGIN_RESULT = 1000
    private final val TAG = "AD"


    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult -> onActivityResult(SINGIN_RESULT, result.resultCode, result.data) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sing_in)

        MobileAds.initialize(this) {}


        googleSingIn = findViewById<Button>(R.id.sing_in_Google)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)


        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.toString()!!)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })





        val account : GoogleSignInAccount ?= GoogleSignIn
            .getLastSignedInAccount(this)

        if (account != null)
        {
            Toast.makeText(this,  account.displayName, Toast.LENGTH_SHORT).show()
            goMain()

        }

        googleSingIn.setOnClickListener{
            gotToSignIn()
        }

    }

    private fun gotToSignIn() {

        val signInIntent = gsc.signInIntent

        if (Build.VERSION.SDK_INT >= 30) {
            startForResult.launch(signInIntent)
        } else {
            @Suppress("DEPRECATION")
            startActivityForResult(signInIntent, SINGIN_RESULT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SINGIN_RESULT ->{
                val task: Task<GoogleSignInAccount> = GoogleSignIn
                    .getSignedInAccountFromIntent(data)

                try {
                    task.getResult((ApiException::class.java))
                    goMain()
                }catch (e: java.lang.Exception)
                {
                    Toast.makeText(this, "ERROR " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goMain() {

        if (mInterstitialAd != null) {
            Log.d(TAG, "AD to show")

            mInterstitialAd?.show(this)

            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    val intent = Intent(this@User_Sing_In_Activity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    mInterstitialAd = null

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }


        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }


      //  val intent = Intent(this, MainActivity::class.java)
       // startActivity(intent)
      //  finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)

    }
}