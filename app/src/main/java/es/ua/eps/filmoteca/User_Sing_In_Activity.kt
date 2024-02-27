package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
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

    private val SINGIN_RESULT = 1000

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult -> onActivityResult(SINGIN_RESULT, result.resultCode, result.data) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_sing_in)

        val actionBar: ActionBar? = (this as AppCompatActivity?)!!.supportActionBar
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        googleSingIn = findViewById<Button>(R.id.sing_in_Google)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(this, gso)

        val account : GoogleSignInAccount ?= GoogleSignIn
            .getLastSignedInAccount(this)

        if (account != null)
        {
            Toast.makeText(this,  account.displayName, Toast.LENGTH_SHORT).show()
            goUserInfo()

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
                    goUserInfo()
                }catch (e: java.lang.Exception)
                {
                    Toast.makeText(this, "ERROR " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goUserInfo() {

        val intent = Intent(this, UserInfoActivity::class.java)
        startActivity(intent)

        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)

    }
}