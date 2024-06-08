package com.example.psalmify

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    var mName: EditText? = null
    var mEmail: EditText? = null
    var mPassword: EditText? = null
    var mRegisterBtn: Button? = null
    var mLoginBtn: TextView? = null
    var mHomeBtn: TextView? = null
    var fAuth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null

    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mName = findViewById(R.id.name)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mRegisterBtn = findViewById(R.id.btnLogin)
        mLoginBtn = findViewById(R.id.textRegister)
        mHomeBtn = findViewById(R.id.homeText)

        fAuth = FirebaseAuth.getInstance()

        mRegisterBtn?.setOnClickListener(View.OnClickListener {
            val email = mEmail?.text.toString().trim { it <= ' ' }
            val password = mPassword?.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email)) {
                mEmail?.error = "Email is required."
                return@OnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                mPassword?.error = "Password is required."
                return@OnClickListener
            }

            if (password.length < 6) {
                mPassword?.error = "Password must be greater or equals to 6 characters."
            }

            progressBar?.visibility = View.VISIBLE

            fAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    fAuth!!.currentUser?.let { firebaseUser ->
                        val userId = firebaseUser.uid
                        val userName = mName?.text.toString()

                        val user = User(userId, userName, "")

                        // Save user to Firestore
                        db.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "User Created.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error! ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        // Save user to Room database
                        CoroutineScope(Dispatchers.IO).launch {
                            val userDao = AppDatabase.getDatabase(applicationContext,CoroutineScope(Dispatchers.IO)).userDao()
                            userDao.insert(user)
                        }

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    }
                } else {
                    Toast.makeText(
                        this@Register,
                        "Error ! " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar?.visibility = View.GONE
                }
            }
        })


        mLoginBtn?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    Login::class.java
                )
            )
        })
        mHomeBtn?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    HomeLogin::class.java
                )
            )
        })
    }
}
