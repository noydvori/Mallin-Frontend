package com.example.ex3.api;

import static com.example.ex3.MyApplication.context;

import com.example.ex3.R;
import com.example.ex3.objects.NameAndPassword;
import java.util.concurrent.CompletableFuture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAPI {
    private static TokenAPI instance;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;

    private TokenAPI() {
        String baseUrl = context.getString(R.string.BASE_URL);
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static TokenAPI getInstance() {
        if (instance == null) {
            instance = new TokenAPI();
        }
        return instance;
    }

    public CompletableFuture<String> createToken(String username, String password) {
        NameAndPassword user = new NameAndPassword(username, password);
        Call<String> call = this.webServiceAPI.createToken(user);
        CompletableFuture<String> future = new CompletableFuture<>();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new Error("Incorrect username or password"));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return future;
    }
}