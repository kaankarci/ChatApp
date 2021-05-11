package com.kk.chatapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.kk.chatapp.Adapters.ChatsAdapter
import com.kk.chatapp.ModelClasses.Chat
import com.kk.chatapp.ModelClasses.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {
    var userIdVisit: String? = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter:ChatsAdapter?=null
    var mChatList:List<Chat>?=null
    lateinit var recycler_view_chats:RecyclerView
    var reference : DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)


        val toolbar: androidx.appcompat.widget.Toolbar =findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@MessageChatActivity, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")    // mesaj atılan kişi
        firebaseUser = FirebaseAuth.getInstance().currentUser   //giriş yapan kişi

        recycler_view_chats=findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        var linearLayoutManager=LinearLayoutManager(applicationContext)
        recycler_view_chats.layoutManager=linearLayoutManager

        val reference = FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)

                username_mchat.text = user!!.username
                Picasso.get().load(user.profile).into(profile_image_mchat)

                retrieveMessages(firebaseUser!!.uid,userIdVisit!!,user.profile)
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
        seenMessage(userIdVisit!!)
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
    private fun retrieveMessages(senderId: String, receiverId: String, receiverImageUrl: String)
    {
        mChatList=ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children)
                {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.reciever.equals(senderId) && chat.sender.equals(receiverId) || chat.reciever.equals(receiverId) && chat.sender.equals(senderId) )
                    {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(this@MessageChatActivity, mChatList!!, receiverImageUrl!!)
                    recycler_view_chats.adapter=chatsAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    var seenListener:ValueEventListener?=null
    private fun seenMessage(userId:String)
    {
         reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                for (dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.reciever.equals(firebaseUser!!.uid) && chat!!.sender.equals(userId))
                    {
                        val hashMap = HashMap<String,Any>()
                        hashMap["isseen"]=true
                        dataSnapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}