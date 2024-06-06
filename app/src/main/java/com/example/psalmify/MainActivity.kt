package com.example.psalmify

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //navigation
        //val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        //val navController = navHostFragment.navController

        //replaceFragment(Home())


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
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext, Login::class.java))
        finish()
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }



}
