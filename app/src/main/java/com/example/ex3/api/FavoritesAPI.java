package com.example.ex3.api;

import static com.example.ex3.MyApplication.context;
import com.example.ex3.R;
import com.example.ex3.utils.UserPreferencesUtils;
import com.example.ex3.entities.Store;
import java.util.ArrayList;
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
        String mallName = UserPreferencesUtils.getMallName(context);
        Call<List<Store>> call = this.webServiceAPI.getFavorites(token,mallName);
        CompletableFuture<List<Store>> future = new CompletableFuture<>();
        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                if (response.isSuccessful()) {
                    List<Store> storeList = new ArrayList<>();
                    List<Store> responseList = response.body();
                    assert responseList != null;
                    for (Store responseStore : responseList) {
                        String storeName = responseStore.getStoreName();
                        String workingHours = responseStore.getWorkingHours();
                        String floorNumber = responseStore.getFloor();
                        String logoUrl = responseStore.getLogoUrl();
                        String storeType = responseStore.getStoreType();
                        Store storeItem = new Store(storeName, workingHours, floorNumber, logoUrl, storeType,false);
                        storeList.add(storeItem);
                    }
                    future.complete(storeList);
                } else {
                    future.completeExceptionally(new Error("Invalid token"));
                    System.out.println("invalid token");
                }
            }
            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
    public CompletableFuture<String> addToFavorites(String token, Store store) {
        Call<Void> call = this.webServiceAPI.addToFavorites(token, store);
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 409) {
                    completableFuture.complete("This store is already favorite");

                } else if (!response.isSuccessful()) {
                    completableFuture.complete("Something went wrong, try again");

                } else {
                    completableFuture.complete("ok");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                completableFuture.completeExceptionally(t);
            }
        });
        return completableFuture;
    }
        public CompletableFuture<String> removeFromFavorites(String token, Store store) {
            Call<Void> call = this.webServiceAPI.removeFromFavorites(token, store);
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 409) {
                        completableFuture.complete("This store is not favorite");

                    } else if (!response.isSuccessful()) {
                        completableFuture.complete("Something went wrong, try again");

                    } else {
                        completableFuture.complete("ok");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                    completableFuture.completeExceptionally(t);
                }
            });

        return completableFuture;
    }
}
