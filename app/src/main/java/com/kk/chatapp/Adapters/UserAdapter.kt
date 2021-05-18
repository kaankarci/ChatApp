package com.kk.chatapp.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kk.chatapp.MessageChatActivity
import com.kk.chatapp.ModelClasses.Chat
import com.kk.chatapp.ModelClasses.User
import com.kk.chatapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    //Veri Olarak Aktif Kullanılacak olanlar
    private val mContext: Context,       //Context tanımladık adapter için
    private val mUsers: List<User>,     //gelecek veriler ModelClass içindeki Users de tanımlı
    private var isChatCheck: Boolean,    //extra buna da ihtiyaç vardı tanımladık
    var lastMsg:String=""
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        //Kodlarken kullanacağımız biçim, burda tanımlanıyoruz
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineImage: CircleImageView
        var offlineImage: CircleImageView
        var lastMessageTxt: TextView

        init {
            //Kullandığımız biçimin, etki edeceği yerleri burdan tanımlıyoruz
            userNameTxt = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImage = itemView.findViewById(R.id.image_online)
            offlineImage = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
        }

    }
    //Görünümün nereye oluşturulacağını tanımlıyoruz
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        val user: User = mUsers[i]
        holder.userNameTxt.text = user!!.username
        Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(holder.profileImageView)

        if (isChatCheck)
        {
            retriveLastMessage(user.uid,holder.lastMessageTxt)

        }
        else
        {
            holder.lastMessageTxt.visibility=View.GONE
        }

        if (isChatCheck)
        {
            if (user.status=="online")
            {
                holder.onlineImage.visibility=View.VISIBLE
                holder.offlineImage.visibility=View.GONE
            }
            else
            {
                holder.onlineImage.visibility=View.GONE
                holder.offlineImage.visibility=View.VISIBLE
            }
        }
        else
        {
            holder.onlineImage.visibility=View.GONE
            holder.offlineImage.visibility=View.GONE
        }

        //kişiye tıklanılınca yapılacak işlemler
        holder.itemView.setOnClickListener {


                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.uid)
                    mContext.startActivity(intent)

        }
    }



    //Oluşturulacak görünümün ne kadar olacağı (user sayısı kadar) tanımlanıyor
    override fun getItemCount(): Int {
        return mUsers.size
    }
    private fun retriveLastMessage(ChatUserId: String, lastMessageTxt: TextView)
    {
    lastMsg="defaultMsg"
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refrence = FirebaseDatabase.getInstance().reference.child("Chats")

        refrence.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children)
                {
                    val chat : Chat?=dataSnapshot.getValue(Chat::class.java)

                    if (firebaseUser!=null && chat !=null)
                    {
                        if (chat.reciever==firebaseUser!!.uid && chat.sender==ChatUserId || chat.reciever==ChatUserId && chat.sender==firebaseUser!!.uid)
                        {
                            lastMsg=chat.message
                        }
                    }
                }
                when(lastMsg)
                {
                    "defaultMsg" -> lastMessageTxt.text = "No Message"
                    "sent you an image." -> lastMessageTxt.text = "Image Sent."
                    else-> lastMessageTxt.text = lastMsg
                }
                lastMsg=="defaultMsg"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}