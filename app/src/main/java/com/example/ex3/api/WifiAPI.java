package com.example.ex3.api;

import android.util.Log;

import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.Node;
import com.example.ex3.objects.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WifiAPI {
    private static WifiAPI instance;
    private final WebServiceAPI webServiceAPI;

    private WifiAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.0.7:5231/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static WifiAPI getInstance() {
        if (instance == null) {
            instance = new WifiAPI();
        }
        return instance;
    }

    public CompletableFuture<> getLocation() {
        Call<List<Node>> call = this.webServiceAPI.get
        CompletableFuture<Category> future = new CompletableFuture<>();

        call.enqueue(new Callback<List<Node>>() {
            @Override
            public void onResponse(Call<List<Node>> call, Response<List<Node>> response) {
                if (response.isSuccessful()) {
                    future.complete();
                } else {
                    future.completeExceptionally(new Error("Invalid token"));
                    System.out.println("invalid token");
                }
            }
            @Override
            public void onFailure(Call<List<Node>> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public CompletableFuture<String> postWifiList(List<Wifi> wifiList) {
        Call<Void> call = this.webServiceAPI.createWifiBatch(wifiList);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {

                    completableFuture.complete("Something went wrong, try again");
                    Log.d("WIFIAPI", "Something went wrong please try again: " +  response.message());
                } else {
                    Log.d("WIFIAPI", "ok");

                    completableFuture.complete("ok");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Log.d("WIFIAPI", "failure");

                completableFuture.completeExceptionally(t);
            }
        });

        return completableFuture;
    }

}
