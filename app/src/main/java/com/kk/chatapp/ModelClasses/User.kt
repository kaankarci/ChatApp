package com.kk.chatapp.ModelClasses

import com.google.firebase.database.Exclude

data class User(
    var uid:String="",
    var username: String = "",
    var profile: String = "",
    var cover: String = "",
    var status: String = "",
    var facebook: String = "",
    var instagram: String = "",
    var website: String = "",

) {}