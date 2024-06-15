package com.example.ex3.api;

import static com.example.ex3.MyApplication.context;

import androidx.annotation.NonNull;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.User;
import com.example.ex3.objects.UserInfo;

import java.util.List;
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
        String baseUrl = context.getString(R.string.BASE_URL);
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static UserAPI getInstance() {
        if (instance == null) {
            instance = new UserAPI();
        }
        return instance;
    }

    public CompletableFuture<String> registerUser(String username, String password, String nickname) {
        User user = new User(username, password, nickname);
        Call<Void> call = this.webServiceAPI.registerUser(user);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 409) {
                    completableFuture.complete("This user is already registered");

                } else if (!response.isSuccessful()) {
                    completableFuture.complete("Something went wrong, try again");

                } else {
                    completableFuture.complete("ok");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                completableFuture.completeExceptionally(t);
            }
        });

        return completableFuture;
    }

    public CompletableFuture<UserInfo> getUser(String token) {
        Call<UserInfo> call = this.webServiceAPI.getUser(token);
        CompletableFuture<UserInfo> future = new CompletableFuture<>();
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(@NonNull Call<UserInfo> call, @NonNull Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    UserInfo userInfo = new UserInfo(response.body().getUsername(), response.body().getDisplayName());
                    future.complete(userInfo);
                } else if (response.code() == 404) {
                    future.completeExceptionally(new Error("User was not found"));
                } else {
                    future.completeExceptionally(new Error("Invalid token"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfo> call, @NonNull Throwable t) {}
        });
        return future;
    }
}
