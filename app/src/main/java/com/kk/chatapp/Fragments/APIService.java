package com.kk.chatapp.Fragments;

import com.kk.chatapp.ModelClasses.Sender;
import com.kk.chatapp.Notifications.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA6YU5wak:APA91bEAx_B81gXI4mdOQb6qQGB5TfC69WBnBHspSQrIt9FD2McU4Ouf-fO-J6kFMYG2QInTz1efnwnUNkaB460_tsEAFIxF5a8-e46UlG6OJgCL8ZVv7NvV0ln0KNl-vZ2Lv0PzJ26X"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
