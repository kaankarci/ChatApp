package com.kk.chatapp

import ViewPagerAdapter
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kk.chatapp.Fragments.ChatsFragment
import com.kk.chatapp.Fragments.SearchFragment
import com.kk.chatapp.Fragments.SettingsFragment
import com.kk.chatapp.ModelClasses.Users
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //toolbar Start
        setSupportActionBar(findViewById(R.id.toolbar_main))

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "" // başlık

        val tabLayout: TabLayout =findViewById(R.id.tabLayout)             //Üst kısımda chat|search|settings kısmının olduğu yer
        val viewPager: ViewPager = findViewById(R.id.view_pager)            //İçerik kısmı
        val viewPagerAdapter =ViewPagerAdapter(supportFragmentManager)      //İçerik kısmı için oluşturulan fragmentler için adapter

        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")         //"Chats" Başlığıyla, içerik kısmına ChatsFragment'te olan gelecek
        viewPagerAdapter.addFragment(SearchFragment(), "Search")
        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")
        tabLayout.setupWithViewPager(viewPager)                             //ViewPager'e başlıkla gelen tabLayout'a eklenecek
        viewPager.adapter = viewPagerAdapter
        //Toolbar End



        firebaseUser = FirebaseAuth.getInstance().currentUser   //şuanki kullanıcı
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)    //şuanki kullanıcının referans kodu

        //Toolbar üstündeki Username ve profil fotografı
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: Users? =snapshot.getValue(Users::class.java) //Users data class olusturduk, firebase den gelenleri duzenlemek icin
                    user_name.text = user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_profile).into(profile_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    //Üst üç noktalı menü
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // üst menüden seçenek seçildiğine;
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {       //çıkış yapma işlemleri
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

        }
        return false
    }


}