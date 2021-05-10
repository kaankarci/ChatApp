package com.kk.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Toolbar Oluşturmak    +++

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login" // başlık

        //Ekran ustunde geri butonu çıkarmak ve tıklanıldığında işlem yapmak ++
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        mAuth = FirebaseAuth.getInstance()

        login_btn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {

        val email: String = email_login.text.toString()
        val password: String = password_login.text.toString()
        if (email==""){
            Toast.makeText(this@LoginActivity, "Please write username", Toast.LENGTH_SHORT).show()
        }
        else if(password=="") {
            Toast.makeText(this@LoginActivity, "Please write password", Toast.LENGTH_SHORT).show()
        }
        //veriler null değilse
        else{
            mAuth.signInWithEmailAndPassword(email,password)    //email ve şifreyle girişi yapar;
                .addOnCompleteListener { task->
                if (task.isSuccessful){     //giriş başarılıysa
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this@LoginActivity, "Error Message:"+task.exception?.message, Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}