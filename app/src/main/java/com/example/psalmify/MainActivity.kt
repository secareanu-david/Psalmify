package com.example.psalmify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainActivity : AppCompatActivity() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }

    companion object {
        const val PREFS_NAME = "app_prefs"
        const val THEME_KEY = "theme"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SyncManager.loadTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home2 -> {
                    bottomNavigationView.menu.findItem(R.id.home).isChecked = true
                }
                R.id.favorite -> {
                    bottomNavigationView.menu.findItem(R.id.favorites).isChecked = true
                }
                R.id.settings2 -> {
                    bottomNavigationView.menu.findItem(R.id.settings).isChecked = true
                }
            }
        }
        bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home ->{
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.favorite){
                        navHostFragment.findNavController().navigate(R.id.action_favorite_to_home2)
                    }
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.settings2){
                        navHostFragment.findNavController().navigate(R.id.action_settings2_to_home2)
                    }
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.detailsFragment){
                        navHostFragment.findNavController().navigate(R.id.action_detailsFragment_to_home2)
                    }
                }


                R.id.favorites -> {
                    if(SyncManager.isGuest){
                        Toast.makeText(this, "Trebuie să fii logat pentru a vedea favoritele!!!", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener false
                    }

                    if(navHostFragment.findNavController().currentDestination?.id == R.id.home2){
                        navHostFragment.findNavController().navigate(R.id.action_home2_to_favorite)
                    }
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.settings2){
                        navHostFragment.findNavController().navigate(R.id.action_settings2_to_favorite)
                    }
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.detailsFragment){
                        navHostFragment.findNavController().navigate(R.id.action_detailsFragment_to_favorite)
                    }
                }
                R.id.settings -> {
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.home2){
                        navHostFragment.findNavController().navigate(R.id.action_home2_to_settings2)
                    }
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.favorite){
                        navHostFragment.findNavController().navigate(R.id.action_favorite_to_settings2)
                    }
                    if(navHostFragment.findNavController().currentDestination?.id == R.id.detailsFragment){
                        navHostFragment.findNavController().navigate(R.id.action_detailsFragment_to_settings2)
                    }
                }
                else -> {

                }
            }
            true
        }
    }

    fun logout(view: View?) {
        if(!SyncManager.isGuest)
            FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext, Login::class.java))
        finish()
    }
    override fun onDestroy(){
        super.onDestroy()
        SyncManager.isGuest = false
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }



}
