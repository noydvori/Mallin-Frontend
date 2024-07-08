package com.example.ex3.api;

import static com.example.ex3.MyApplication.context;

import androidx.annotation.NonNull;

import com.example.ex3.R;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.LocationAndPath;
import com.example.ex3.objects.Paths;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavigationAPI {
    private static NavigationAPI instance;
    private final WebServiceAPI webServiceAPI;

    private NavigationAPI() {
        String baseUrl = context.getString(R.string.BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }
    public static NavigationAPI getInstance() {
        if (instance == null) {
            instance = new NavigationAPI();
        }
        return instance;
    }
    public CompletableFuture<List<GraphNode>> getOrderedRout(String token, Store store, List<Store> stores) {
        LocationAndPath locationAndPath = new LocationAndPath(store,stores);
        Call<List<GraphNode>> call = webServiceAPI.getOrderedRout(token, locationAndPath);

        CompletableFuture<List<GraphNode>> future = new CompletableFuture<>();

        call.enqueue(new Callback<List<GraphNode>>() {
            @Override
            public void onResponse(@NonNull Call<List<GraphNode>> call, @NonNull Response<List<GraphNode>> response) {
                if (response.isSuccessful()) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new Exception("Failed to get closest stores"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GraphNode>> call, @NonNull Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
    public CompletableFuture<Paths> getRout(String token, Store store, List<Store> stores) {
        Call<Paths> call = webServiceAPI.getRout(token, store, stores);

        CompletableFuture<Paths> future = new CompletableFuture<>();

        call.enqueue(new Callback<Paths>() {
            @Override
            public void onResponse(@NonNull Call<Paths> call, @NonNull Response<Paths> response) {
                if (response.isSuccessful()) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new Exception("Failed to get closest stores"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Paths> call, @NonNull Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}
