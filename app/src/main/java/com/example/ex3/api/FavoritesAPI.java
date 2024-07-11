package com.example.ex3.api;

import static com.example.ex3.MyApplication.context;

import androidx.annotation.NonNull;

import com.example.ex3.R;
import com.example.ex3.utils.UserPreferencesUtils;
import com.example.ex3.entities.Store;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoritesAPI {
    private static FavoritesAPI instance;
    private final WebServiceAPI webServiceAPI;

    private FavoritesAPI() {
        String baseUrl = context.getString(R.string.BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static FavoritesAPI getInstance() {
        if (instance == null) {
            instance = new FavoritesAPI();
        }
        return instance;
    }

    public CompletableFuture<List<Store>> getFavorites(String token) {
        Call<List<Store>> call = webServiceAPI.getFavorites(token);
        CompletableFuture<List<Store>> future = new CompletableFuture<>();

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(@NonNull Call<List<Store>> call, @NonNull Response<List<Store>> response) {
                if (response.isSuccessful()) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new Exception("Failed to fetch favorites"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Store>> call, @NonNull Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public CompletableFuture<Void> addToFavorites(String token, Store store) {
        Call<Void> call = webServiceAPI.addToFavorites(token, store);
        CompletableFuture<Void> future = new CompletableFuture<>();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new Exception("Failed to add to favorites"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public CompletableFuture<Void> removeFromFavorites(String token, Store store) {
        Call<Void> call = webServiceAPI.removeFromFavorites(token, store);
        CompletableFuture<Void> future = new CompletableFuture<>();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new Exception("Failed to remove from favorites"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}
