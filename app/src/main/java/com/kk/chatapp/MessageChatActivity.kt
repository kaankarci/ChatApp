package com.kk.chatapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.kk.chatapp.ModelClasses.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {
    var userIdVisit: String? = ""
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")    // mesaj atılan kişi
        firebaseUser = FirebaseAuth.getInstance().currentUser   //giriş yapan kişi

        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: Users? = snapshot.getValue(Users::class.java)

                username_mchat.text = user!!.getUserName()
             //   Picasso.get().load(user.getProfile()).into(profile_image_mchat)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        send_message_button.setOnClickListener {
            val message = text_message.text.toString()
            if (message == "")
            {
                Toast.makeText(this@MessageChatActivity,"Please write a message....",Toast.LENGTH_SHORT).show()
            }
            else
            {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }
        attact_image_file.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }
    }

    private fun sendMessageToUser(senderId: String, recieverId: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key   //mesaj için key oluşturulur

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["reciever"] = recieverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHashMap).addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val chatsListReference =FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid).child(userIdVisit!!)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot)
                        {
                            if (!snapshot.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatListRecieverRef =FirebaseDatabase.getInstance().reference.child("ChatList").child(userIdVisit!!).child(firebaseUser!!.uid)

                            chatListRecieverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(error: DatabaseError)
                        {
                        }
                    })



                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 438 && resultCode == RESULT_OK && data!=null && data!!.data != null) {

            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Please wait, image is sending...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("${messageId}.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (task.isSuccessful) {
                    task.exception?.let { throw it }

                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "send you an image."
                    messageHashMap["reciever"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId
                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                    progressBar.cancel()
                }
            }
        }
    }

}