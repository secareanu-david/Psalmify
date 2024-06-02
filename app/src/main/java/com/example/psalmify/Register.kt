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

class Register : AppCompatActivity() {
    var mFullName: EditText? = null
    var mEmail: EditText? = null
    var mPassword: EditText? = null
    var mPhone: EditText? = null
    var mRegisterBtn: Button? = null
    var mLoginBtn: TextView? = null
    var mHomeBtn: TextView? = null
    var fAuth: FirebaseAuth? = null
    var userID: String? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mFullName = findViewById(R.id.fullName)
        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        mPhone = findViewById(R.id.phone)
        mRegisterBtn = findViewById(R.id.btnLogin)
        mLoginBtn = findViewById(R.id.textRegister)
        mHomeBtn = findViewById(R.id.homeText)

        fAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        if (fAuth?.currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

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
                    Toast.makeText(this@Register, "User Created.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
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
                    Home::class.java
                )
            )
        })
    }
}
