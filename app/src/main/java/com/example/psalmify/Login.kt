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

class Login : AppCompatActivity() {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mLoginBtn: Button? = null
    private var mCreateBtn: TextView? = null
    private var mHomeBtn: TextView? = null
    private var progressBar: ProgressBar? = null
    private var fAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        progressBar = findViewById(R.id.progressBar)
        fAuth = FirebaseAuth.getInstance()
        mLoginBtn = findViewById(R.id.btnLogin)
        mCreateBtn = findViewById(R.id.textRegister)
        mHomeBtn = findViewById(R.id.txtHome)

        mLoginBtn?.setOnClickListener(View.OnClickListener {
            val email = mEmail?.text.toString().trim { it <= ' ' }
            val password = mPassword?.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email)) {
                mEmail?.error = "Email is Required."
                return@OnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                mPassword?.error = "Password is Required."
                return@OnClickListener
            }

            if (password.length < 6) {
                mPassword?.error = "Password Must be >= 6 Characters"
                return@OnClickListener
            }

            progressBar?.visibility = View.VISIBLE

            // authenticate the user
            fAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@Login, "Successfully Logged In", Toast.LENGTH_LONG).show()
                    //                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@Login,
                        "Error ! " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar?.visibility = View.GONE
                }
            }
        })

        mCreateBtn?.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    Register::class.java
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