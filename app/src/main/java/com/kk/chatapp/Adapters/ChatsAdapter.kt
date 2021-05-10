package com.kk.chatapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kk.chatapp.ModelClasses.Chat
import com.kk.chatapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*

class ChatsAdapter(
    private val mContext: Context,       //Context tanımladık adapter için
    private val mChatList: List<Chat>,
    private val imageUrl: String,
    var firebaseUser: FirebaseUser=FirebaseAuth.getInstance().currentUser!!
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var right_image_view: ImageView? = null
        var text_seen: TextView? = null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            right_image_view = itemView.findViewById(R.id.right_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return if (position == 1) {
            val view: View = LayoutInflater.from(mContext)
                .inflate(com.kk.chatapp.R.layout.message_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(mContext)
                .inflate(com.kk.chatapp.R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChatList[position]
        Picasso.get().load(imageUrl).into(holder.profile_image)
     //Image Messages
        if (chat.message == "send you an image." && chat.url !="") {
        //image message - right side
            if (chat.sender.equals(firebaseUser!!.uid)) {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.right_image_view)
            }
        //image message - left side
            else if (!chat.sender.equals(firebaseUser!!.uid)) {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.url).into(holder.left_image_view)
            }
        }
    //Text Messages
        else
        {
            holder.show_text_message!!.text=chat.message
        }
    //sent and seen message
        if (position==mChatList.size-1)
        {
            if (chat.isseen)
            {
                holder.text_seen!!.text="Seen"
                if (chat.message.equals("send you an image.") && chat.url.equals(""))
                {
                    val lp:RelativeLayout.LayoutParams?=holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams=lp
                }
            }
            else{
                holder.text_seen!!.text="Sent"
                if (chat.message.equals("send you an image.") && chat.url.equals(""))
                {
                    val lp:RelativeLayout.LayoutParams?=holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams=lp
                }
            }
        }
        else
        {
            holder.text_seen!!.visibility=View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun getItemViewType(position: Int): Int
    {
        //Eğer yazan kişi bensem 0, karşı tarafsa 1 değeri döndürelecek
        return if (mChatList[position].sender.equals(firebaseUser!!.uid)) {
            1
        } else {
            0
        }
    }
}