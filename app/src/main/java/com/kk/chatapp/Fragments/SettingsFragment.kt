package com.kk.chatapp.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.kk.chatapp.ModelClasses.User
import com.kk.chatapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    var usersRefrance: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser   //geçerli kullanıcı
        usersRefrance =FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)    //Giriş yapılmış olan kullanıcının linkini verir bu linkte kullanıcının veri bloğu tekil haldedir
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")      //User Images klasörü ne girer (firebase içinde yoksa oluşturur)



        usersRefrance!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())//snapshout da veri varsa true yoksa false dondurur
                {
                    val user: User? = snapshot.getValue(User::class.java) //giriş yapmış olan userın (getValue oldugu ıcın) key olmadan değerlerini alır Users'deki yerlerıne koyar


                    view.username_settings.text = user!!.username
                    Picasso.get().load(user.profile).into(profile_image_settings)
                    Picasso.get().load(user.cover).into(cover_image_settings)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        //profil fotografı ve arkaplan fotografı fonksiyonları
        view.profile_image_settings.setOnClickListener {
            pickImage()
        }
        view.cover_image_settings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        //sosyal medya hesapları düzenleme fonksiyonları
        view.set_facebook_settings.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }
        view.set_instagram_settings.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }
        view.set_website_settings.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }


        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder =AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)   //Alert Dialog çıkar ekrana
        if (socialChecker == "website") {
            builder.setTitle("Write URL:")
        }
        else {
            builder.setTitle("Write username:")
        }

        val editText = EditText(context)
        if (socialChecker == "website") {
            editText.hint = "e.g www.google.com"
        }
        else {
            editText.hint = "e.g kaankarci"
        }
        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
            val str = editText.text.toString()
            if (str == "") {
                Toast.makeText(context, "Please write something...", Toast.LENGTH_SHORT).show()
            } else {
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Decline", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {

        val mapSocial =HashMap<String,Any>()
        when(socialChecker){
            "facebook"-> {
                mapSocial["facebook"] = "https://m.facebook.com/${str}"
            }
            "instagram"->
            {
                mapSocial["instagram"]="https://m.facebook.com/${str}"
            }
            "website"->
            {
                mapSocial["website"]="https://${str}"
            }
        }
        usersRefrance!!.updateChildren(mapSocial).addOnCompleteListener { task->
            if (task.isSuccessful)
            {
                Toast.makeText(context, "Saved Successfully", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            Toast.makeText(context, "uploading...", Toast.LENGTH_LONG).show()
            uploadingImageDatabase()
        }
    }

    private fun uploadingImageDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("image is uploading, please wait...")
        progressBar.show()
        if (imageUri != null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (task.isSuccessful) {
                    task.exception?.let { throw it }

                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if (coverChecker == "cover") {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        usersRefrance!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    } else {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        usersRefrance!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

}