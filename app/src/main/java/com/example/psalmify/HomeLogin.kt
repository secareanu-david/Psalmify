package com.example.psalmify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.psalmify.MainActivity.Companion.PREFS_NAME
import com.example.psalmify.MainActivity.Companion.THEME_KEY
import com.example.psalmify.SyncManager.Companion.isGuest

class HomeLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SyncManager.loadTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_login)

    }

    fun login(view: View?) {
        startActivity(Intent(applicationContext, Login::class.java))
        finish()
    }
    fun guestLogin(view: View?) {
        isGuest = true
        Toast.makeText(this, "Dumnezeu sa te binecuvanteze, draga oaspete!!", Toast.LENGTH_LONG).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }

    fun register(view: View?) {
        startActivity(Intent(applicationContext, Register::class.java))
        finish()
    }
}
