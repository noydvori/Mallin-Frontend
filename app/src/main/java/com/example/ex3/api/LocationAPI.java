package com.example.ex3.api;

import static com.example.ex3.MyApplication.context;

import android.net.wifi.ScanResult;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ex3.R;
import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.WifiScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationAPI {
    private static LocationAPI instance;
    private final WebServiceAPI webServiceAPI;

    private LocationAPI() {
        String baseUrl = context.getString(R.string.LIVE_LOCATION_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }
    public static LocationAPI getInstance() {
        if (instance == null) {
            instance = new LocationAPI();
        }
        return instance;
    }
    public CompletableFuture<List<Store>> getClosestStores(String token, ArrayList<WifiScanResult> scanResults) {
        Call<List<Store>> call = webServiceAPI.getClosestStores(token, scanResults);
        CompletableFuture<List<Store>> future = new CompletableFuture<>();

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(@NonNull Call<List<Store>> call, @NonNull Response<List<Store>> response) {
                if (response.isSuccessful()) {
                    future.complete(response.body());
                } else {
                    future.completeExceptionally(new Exception("Failed to get closest stores"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Store>> call, @NonNull Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
    public CompletableFuture<GraphNode> getLiveLocation(String token, ArrayList<WifiScanResult> scanResults) {
        Call<GraphNode> call = webServiceAPI.getLiveLocation(token, scanResults);
        CompletableFuture<GraphNode> future = new CompletableFuture<>();
        Log.d("LocationAPI", "sendx response");

        call.enqueue(new Callback<GraphNode>() {
            @Override
            public void onResponse(@NonNull Call<GraphNode> call, @NonNull Response<GraphNode> response) {
                if (response.isSuccessful()) {
                    Log.d("LocationAPI", "sucess response");
                    future.complete(response.body());
                } else {
                    Log.d("LocationAPI", "failed response");

                    future.completeExceptionally(new Exception("Failed to get closest stores"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GraphNode> call, @NonNull Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

}
