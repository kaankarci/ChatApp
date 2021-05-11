package com.kk.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kk.chatapp.Adapters.UserAdapter
import com.kk.chatapp.ModelClasses.ChatList
import com.kk.chatapp.ModelClasses.User
import com.kk.chatapp.R


class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<User>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recycler_view_chatlist:RecyclerView
    private var firebaseUser: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chatlist=view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager=LinearLayoutManager(context)

        firebaseUser=FirebaseAuth.getInstance().currentUser

        usersChatList=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                (usersChatList as ArrayList).clear()
                for (datasnapshot in p0.children)
                {
                    println("\n data snap: "+datasnapshot)
                    val chatlist = datasnapshot.getValue(ChatList::class.java)
                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return view
    }

    private fun retrieveChatList()
    {

        mUsers=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                (mUsers as ArrayList).clear()
                for (datasnapshot in p0.children)
                {
                    val user=datasnapshot.getValue(User::class.java)
                    for (eachChatList in usersChatList!!)
                    {
                        if(user!!.uid.equals(eachChatList.id))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdapter= UserAdapter(context!!,(mUsers as ArrayList<User>),true)
                recycler_view_chatlist.adapter=userAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}