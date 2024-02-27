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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.ua.eps.filmoteca.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val account : GoogleSignInAccount?= GoogleSignIn
            .getLastSignedInAccount(this)

        ///Log(account.givenName.displayName)

        var nameTextV = binding.nameValue
        var emailTextV = binding.emailValue

        nameTextV?.text = account?.displayName
        emailTextV?.text = account?.email

        val buttonSingOut = binding.singOut
        buttonSingOut?.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
           val gsc = GoogleSignIn.getClient(this, gso)

            gsc.signOut()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if(id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)

    }
}