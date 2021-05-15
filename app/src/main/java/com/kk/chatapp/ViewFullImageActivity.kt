package com.kk.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_full_image.*

class ViewFullImageActivity : AppCompatActivity() {
    private  var image_viewer : ImageView?=null
    private var imageUrl:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        imageUrl=intent.getStringExtra("url")
        image_viewer = findViewById(R.id.image_viewer)

        Picasso.get().load(imageUrl).into(image_viewer)

    }
}