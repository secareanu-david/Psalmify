package com.example.psalmify

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.psalmify.MainActivity.Companion.PREFS_NAME
import com.example.psalmify.MainActivity.Companion.THEME_KEY
import com.example.psalmify.SyncManager.Companion.isGuest

class HomeLogin : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        SyncManager.loadTheme(this)
        sharedPreferences = this.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_login)

    }

    fun login(view: View?) {
        if(sharedPreferences.getBoolean("rememberMe",false)){
            val email = sharedPreferences.getString("email","")
            val password = sharedPreferences.getString("password","")
            if (email != null && password != null) {
                Login.loginFunction(this,email,password)
            }
        }
        else {
            startActivity(Intent(applicationContext, Login::class.java))
            finish()
        }

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
