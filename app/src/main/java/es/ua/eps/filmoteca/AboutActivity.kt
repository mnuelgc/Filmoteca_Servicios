package es.ua.eps.filmoteca

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intentWeb = Intent(Intent.ACTION_VIEW,
            Uri.parse("http://www.ua.es"))
        val buttonWeb = binding.button
        buttonWeb.setOnClickListener {
            if (intentWeb.resolveActivity(packageManager) != null) {
                startActivity(intentWeb)
            }
        }

        val intentMail = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:manuelgc_12@hotmail.com"))
        val buttonSup = binding.button2
        buttonSup.setOnClickListener {
            if (intentMail.resolveActivity(packageManager) != null) {
                startActivity(intentMail)
            }
        }

        val buttonBack = binding.button3
        buttonBack.setOnClickListener {
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