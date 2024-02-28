package es.ua.eps.filmoteca

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.ua.eps.filmoteca.databinding.ActivityUserInfoBinding
import kotlin.system.exitProcess

class UserInfoActivity : AppCompatActivity() {

    lateinit var gso : GoogleSignInOptions
    lateinit var gsc : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        val account : GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)

        ///Log(account.givenName.displayName)

        val idTextV = binding.idValue
        val nameTextV = binding.nameValue
        val emailTextV = binding.emailValue

        val image = binding.imageView

        if (account != null) {

            idTextV?.text = account.id
            nameTextV.text = account.displayName
            emailTextV.text = account.email

            Glide.with(applicationContext).load(account.photoUrl).into(image)
        }

        val buttonSingOut = binding.singOut
        buttonSingOut?.setOnClickListener {
            gsc.signOut()
            goSingIn()
        }

        val buttonDisconnect = binding.disconnect
        buttonDisconnect?.setOnClickListener {
            gsc.revokeAccess()
            finish()
            finishAffinity()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if(id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goSingIn() {

        val intent = Intent(this, User_Sing_In_Activity::class.java)
        startActivity(intent)

        finish()
    }
}