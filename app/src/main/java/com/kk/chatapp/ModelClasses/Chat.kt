package com.kk.chatapp.ModelClasses

data class Chat(
    var sender:String="",
    var message:String="",
    var reciever:String="",
    var isseen:Boolean=false,
    var url:String="",
    var messageId:String=""

) {}