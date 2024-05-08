package com.example.ex3.api;


import com.example.ex3.objects.ChatServer;
import com.example.ex3.objects.CreateChatBody;
import com.example.ex3.objects.MsgBody;
import com.example.ex3.objects.MsgSmall;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatServiceAPI {

    @GET("Chats")
    Call<List<ChatServer>> getChats(@Header("Authorization") String access_token);

    @POST("Chats")
    Call<ChatServer> createChat(@Body CreateChatBody createChatBody, @Header("Authorization") String access_token);

    @GET("Chats/{id}/Messages")
    Call<List<MsgSmall>> getMsg(@Path("id") int id, @Header("Authorization") String access_token);

    @POST("Chats/{id}/Messages")
    Call<List<MsgSmall>> createMsg(@Path("id") int id, @Header("Authorization") String access_token, @Body MsgBody msgBody);

}
