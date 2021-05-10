package com.kk.chatapp.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.kk.chatapp.Adapters.UserAdapter
import com.kk.chatapp.ModelClasses.User
import com.kk.chatapp.R


class SearchFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<User>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.searchUsersET)


        mUsers = ArrayList()
        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })  //yazılan yazıyı ızler ona gore ıslem yapar
        return view
    }

    //Bütün Kullanıcıları getirir
    private fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {

               (mUsers as ArrayList<User>).clear()

                if (searchEditText!!.text.toString() == "")
               {
                   var maps: Map<String,User?>? = p0.getValue<Map<String, User?>>()      //maps'e "key=> String", "User=>value" değerleri olarak tanımladık

                    for (snapshot in maps ?: hashMapOf())       //maps null değilse anlamına geliyor
                    {
                           var user: User? = snapshot.value     //user'i User modeli olarak gör, içine snapshot'dan gelen value değerlerini gir

                       if (!(user!!.uid).equals(firebaseUserID))    //kendini göstermemesi için yani gelen uid, giriş yapılana eşit değilse
                        {
                            (mUsers as ArrayList<User>).add(user)
                        }
                    }
                   userAdapter = UserAdapter(context!!, mUsers!!, false)
                   recyclerView!!.adapter = userAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun searchForUsers(str: String) {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers =FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search").startAt(str).endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<User>).clear()

                var maps: Map<String,User?>? = p0.getValue<Map<String, User?>>()

                if (maps != null) {
                    for (snapshot in maps) {
                        val user: User? = snapshot.value
                        if (!(user!!.uid).equals(firebaseUserID)) //kendini göstermemesi için yani gelen uid, giriş yapılana eşit değilse
                        {
                            (mUsers as ArrayList<User>).add(user)

                        }
                    }
                }

                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}