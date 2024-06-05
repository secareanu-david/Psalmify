package com.example.psalmify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.example.psalmify.UserDao

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(Home())
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home -> replaceFragment(Home())
                R.id.favorites -> replaceFragment(Favorite())
                R.id.settings -> replaceFragment(Settings())
                else -> {

                }
            }
            true
        }
    }

    fun logout(view: View?) {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext, Login::class.java))
        finish()
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }



}
