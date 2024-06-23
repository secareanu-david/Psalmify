package com.example.psalmify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.psalmify.MainActivity.Companion.PREFS_NAME
import com.example.psalmify.MainActivity.Companion.THEME_KEY

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

    fun register(view: View?) {
        startActivity(Intent(applicationContext, Register::class.java))
        finish()
    }
}
