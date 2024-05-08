package com.example.ex3.api;

import com.example.ex3.entities.Chat;
import com.example.ex3.entities.User;
import com.example.ex3.objects.UserFull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserServiceAPI {

    @POST("Users")
    Call<UserFull> createUser(@Body UserFull userFull);

    @GET("Users/{username}")
    Call<UserFull> getUser(@Path(("username")) String username, @Header("Authorization") String access_token);

}
