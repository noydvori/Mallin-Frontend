package com.example.ex3.api;

import android.content.SharedPreferences;

import com.example.ex3.entities.Token;
import com.example.ex3.objects.BodyToken;
import com.example.ex3.objects.TokenResponse;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TokenServiceAPI {
    @POST("Tokens")
    Call<TokenResponse> getToken(@Body BodyToken token);

}
