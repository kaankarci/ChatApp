package com.kk.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class WelcomeActivity : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


    }

    override fun onStart() {
        super.onStart()
        firebaseUser = FirebaseAuth.getInstance().currentUser   //varsa halihazırda giriş yapılmış olan kullanıcı eşitlenir
        if (firebaseUser != null) {
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
    fun register_welcome_btn(view: View) {
        val intent = Intent(this@WelcomeActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun login_welcome_btn(view: View) {
        val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}