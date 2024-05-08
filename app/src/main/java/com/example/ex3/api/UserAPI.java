package com.example.ex3.api;

import com.example.ex3.objects.User;
import com.example.ex3.objects.UserInfo;
import com.example.ex3.R;
import com.example.ex3.viewModels.MyApplication;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAPI {
    private static UserAPI instance;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;

    private UserAPI() {
        System.out.println("2");

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.153.1:5000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        System.out.println("3");

        webServiceAPI = retrofit.create(WebServiceAPI.class);
        System.out.println("4");

    }

    public static UserAPI getInstance() {
        if (instance == null) {
            instance = new UserAPI();
        }
        return instance;
    }

    public void setRetrofit(String url){
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public CompletableFuture<String> registerUser(String username, String password, String nickname) {
        System.out.println("5");

        User user = new User(username, password, nickname);
        Call<Void> call = this.webServiceAPI.registerUser(user);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        System.out.println("6");

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 409) {
                    completableFuture.complete("This user is already registered");
                    System.out.println("This user is already registered");

                } else if (!response.isSuccessful()) {
                    completableFuture.complete("Something went wrong, try again");
                    System.out.println("Something went wrong, try again");

                } else {
                    completableFuture.complete("ok");
                    System.out.println("ok");

                }
                System.out.println("7");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                completableFuture.completeExceptionally(t);
                System.out.println("8");
                System.out.println(t);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<UserInfo> getUser(String token) {
        Call<UserInfo> call = this.webServiceAPI.getUser(token);
        CompletableFuture<UserInfo> future = new CompletableFuture<>();
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    UserInfo userInfo = new UserInfo(response.body().getUsername(), response.body().getDisplayName(), response.body().getProfilePic());
                    future.complete(userInfo);
                } else if (response.code() == 404) {
                    future.completeExceptionally(new Error("User was not found"));
                } else {
                    future.completeExceptionally(new Error("Invalid token"));
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {}
        });
        return future;
    }
}