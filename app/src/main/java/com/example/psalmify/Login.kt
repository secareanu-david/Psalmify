package com.example.psalmify

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class Login : AppCompatActivity() {
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mLoginBtn: Button? = null
    private var mCreateBtn: TextView? = null
    private var mHomeBtn: TextView? = null
    private var mRememberMe: CheckBox? = null
    private lateinit var sharedPreferences: SharedPreferences

    companion object {

        var fAuth: FirebaseAuth? = FirebaseAuth.getInstance()

        fun loginFunction(loginContext: AppCompatActivity, emailValue: String, passwordValue: String) {
            val email = emailValue.trim { it <= ' ' }
            val password = passwordValue.trim { it <= ' ' }

            // authenticate the user
            fAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    SyncManager.isGuest = false

                    var alias : String? = null
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(fAuth?.currentUser!!.uid)
                    userRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                alias = document.getString("name")
                                Toast.makeText(loginContext, "Dumnezeu sa te binecuvanteze, "+alias+"!", Toast.LENGTH_LONG).show()
                            }
                        }
                    val intent = Intent(loginContext, MainActivity::class.java)
                    loginContext.startActivity(intent)
                } else {
                    Toast.makeText(
                        loginContext,
                        "Eroare!Nu exista acest utilizator ! " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        SyncManager.loadTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mEmail = findViewById(R.id.email)
        mPassword = findViewById(R.id.password)
        fAuth = FirebaseAuth.getInstance()
        mLoginBtn = findViewById(R.id.btnLogin)
        mCreateBtn = findViewById(R.id.textRegister)
        mHomeBtn = findViewById(R.id.txtHome)
        mRememberMe = findViewById(R.id.checkBox)

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)


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

            // authenticate the user
            fAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //remember
                    if (mRememberMe?.isChecked == true) {
                        savePreferences(email, password)
                    } else {
                        clearPreferences()
                    }

                    SyncManager.isGuest = false

                    var alias : String? = null
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(fAuth?.currentUser!!.uid)
                    userRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                alias = document.getString("name")
                                Toast.makeText(this@Login, "Dumnezeu sa te binecuvanteze, "+alias+"!", Toast.LENGTH_LONG).show()
                            }
                        }
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@Login,
                        "Eroare!Nu exista acest utilizator ! " + task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        loadPreferences()

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
                    HomeLogin::class.java
                )
            )
        })
        mRememberMe?.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("rememberMe", isChecked)
            editor.apply()
            if(!isChecked)
                clearPreferences()
        }
    }
    private fun savePreferences(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("rememberMe", true)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    private fun loadPreferences() {
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)
        val email = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")

        mRememberMe?.isChecked = rememberMe
        if (rememberMe) {
            mEmail?.setText(email)
            mPassword?.setText(password)
        }
    }

    private fun clearPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}