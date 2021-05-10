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
import com.kk.chatapp.MessageChatActivity
import com.kk.chatapp.ModelClasses.User
import com.kk.chatapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    //Veri Olarak Aktif Kullanılacak olanlar
    private val mContext: Context,       //Context tanımladık adapter için
    private val mUsers: List<User>,     //gelecek veriler ModelClass içindeki Users de tanımlı
    private var isChatCheck: Boolean,    //extra buna da ihtiyaç vardı tanımladık
) : RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        //Kodlarken kullanacağımız biçim, burda tanımlanıyoruz
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineTxt: CircleImageView
        var offlineTxt: CircleImageView
        var lastMessageTxt: TextView

        init {
            //Kullandığımız biçimin, etki edeceği yerleri burdan tanımlıyoruz
            userNameTxt = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineTxt = itemView.findViewById(R.id.image_online)
            offlineTxt = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
        }

    }
    //Görünümün nereye oluşturulacağını tanımlıyoruz
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        //BURADA BI SIKINTI VAR AMA NE?

        val user: User = mUsers[i]
        holder.userNameTxt.text = user!!.username
        println("${i} Name: ${user.username}")
        println("user: "+ mUsers[i].username)
        Picasso.get().load(user.profile).placeholder(R.drawable.ic_profile).into(holder.profileImageView)

        //kişiye tıklanılınca yapılacak işlemler
        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>("Send Message","Visit Profile")
            val builder: AlertDialog.Builder=AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, position ->
                if (position==0)//1. secenek (send message) seçildiğinde
                {
                    val intent = Intent(mContext, MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.uid)
                    mContext.startActivity(intent)

                }
                if (position==1)//2. secenek (visit profile) secildiğinde
                {

                }
            })
            builder.show()
        }
    }

    //Oluşturulacak görünümün ne kadar olacağı (user sayısı kadar) tanımlanıyor
    override fun getItemCount(): Int {
        return mUsers.size
    }

}