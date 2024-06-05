package com.example.psalmify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class HomeLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
