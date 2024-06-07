package com.example.psalmify

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.psalmify.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncManager : Application() {

    override fun onCreate() {
        super.onCreate()

        // Sync data from Firebase Firestore to Room database when the app starts
        syncDataFromFirestoreToRoom()
    }

    override fun onTerminate() {
        super.onTerminate()

        // Sync data from Room database to Firebase Firestore when the app is closed
        syncDataFromRoomToFirestore()
    }

    private fun syncDataFromFirestoreToRoom() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val userId = document.id
                    val userName = document.data["name"] as String
                    val favoritePsalms = document.data["favoritePsalms"] as? String ?: ""
                    val userDao = AppDatabase.getDatabase(applicationContext,CoroutineScope(Dispatchers.IO)).userDao()
                    // Update or insert user data into Room database
                    CoroutineScope(Dispatchers.IO).launch {
                        val user = User(userId, userName, favoritePsalms)
                        userDao.insert(user)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun syncDataFromRoomToFirestore() {
        // Retrieve all users from the Room database;
        val userDao = AppDatabase.getDatabase(applicationContext,CoroutineScope(Dispatchers.IO)).userDao()
        CoroutineScope(Dispatchers.IO).launch {
            val users = userDao.getAllUsers()
            // Update corresponding documents in Firestore collection
            val db = FirebaseFirestore.getInstance()
            val userCollection = db.collection("users")

            users.forEach { user ->
                userCollection.document(user.id).set(user)
                    .addOnSuccessListener {
                        Log.d(TAG, "Document ${user.id} successfully updated in Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document ${user.id} in Firestore", e)
                    }
            }
        }


    }
}