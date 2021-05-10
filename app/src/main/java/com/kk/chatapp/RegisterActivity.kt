package com.kk.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        //Toolbar Oluşturmak    +++

        val toolbar: Toolbar =
            findViewById(R.id.toolbar_register)  //toolbar'ın uzerıne oluşacağı yer
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Register" // toolbar başlık
        //Ekran ustunde geri butonu çıkarmak ve tıklanıldığında işlem yapmak
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        //Kayıt ol butonuna tıklanıldıktan sonra
        register_btn.setOnClickListener {
            registerUser()
        }
    }


    private fun registerUser() {
        val username: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = password_register.text.toString()

        if (username==""){
            Toast.makeText(this@RegisterActivity, "Please write username", Toast.LENGTH_SHORT).show()
        }
        else if(password=="") {
            Toast.makeText(this@RegisterActivity, "Please write password", Toast.LENGTH_SHORT).show()
        }
        else if(email=="") {
            Toast.makeText(this@RegisterActivity, "Please write email", Toast.LENGTH_SHORT).show()

        }
        //Hiçbir değer null değilse;
        else{

            mAuth.createUserWithEmailAndPassword(email,password)    //email ve şifreyi kullanarak kullanıcı olusturuldu
                .addOnCompleteListener { task->
                    if(task.isSuccessful){

                    firebaseUserID=mAuth.currentUser!!.uid  //veritabanının altında kişiye özel blok açar
                        refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                        val userHasMap=HashMap<String,Any>()    //hashmap'te default bilgileri tanıtıp içine attığımız herşeyidirekt veritabanına göndericez
                        userHasMap["uid"]=firebaseUserID
                        userHasMap["username"]=username
                        userHasMap["profile"]="https://firebasestorage.googleapis.com/v0/b/chatapp-ef168.appspot.com/o/ic_profile.png?alt=media&token=4d533423-ba87-421e-8b1b-8bb4d0b858f3"
                        userHasMap["cover"]="https://firebasestorage.googleapis.com/v0/b/chatapp-ef168.appspot.com/o/cover.jpg?alt=media&token=d974ae09-b56d-4367-b2ee-767f9e794c22"
                        userHasMap["status"]="offline"
                        userHasMap["search"]=username.toLowerCase()
                        userHasMap["facebook"]="https://m.facebook.com"
                        userHasMap["instagram"]="https://m.instagram.com"
                        userHasMap["website"]="https://m.google.com"

                        refUsers.updateChildren(userHasMap) //yukardakı default bılgılerı ekler
                            .addOnCompleteListener { task->
                            if (task.isSuccessful){
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
                    else{
                        Toast.makeText(this@RegisterActivity, "Error Message:"+task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


}