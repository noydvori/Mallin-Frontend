package com.example.ex3.api;

import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryAPI {
    private static CategoryAPI instance;
    private final WebServiceAPI webServiceAPI;

    private CategoryAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.153.1:5000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static CategoryAPI getInstance() {
        if (instance == null) {
            instance = new CategoryAPI();
        }
        return instance;
    }

    public CompletableFuture<Category> getStoresByType(String token,String type) {
        Call<Category> call = this.webServiceAPI.getStoresByType(token,type,"Azrieli TLV");
        CompletableFuture<Category> future = new CompletableFuture<>();

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Category category = new Category(type);
                    Category responseList = response.body();
                    assert responseList != null;
                    List<Store> storesList = responseList.getStoresList();
                    for (Store responseStore : storesList) {
                        String storeName = responseStore.getStoreName();
                        String workingHours = responseStore.getWorkingHours();
                        String floorNumber = responseStore.getFloor();
                        String logoUrl = responseStore.getLogoUrl();
                        Store storeItem = new Store(storeName, workingHours, floorNumber, logoUrl, type);
                        category.addStore(storeItem);
                    }
                    future.complete(category);
                } else {
                    future.completeExceptionally(new Error("Invalid token"));
                    System.out.println("invalid token");
                }
            }
            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public CompletableFuture<List<Store>> getStoresByName(String token, String storeName) {
        Call<List<Store>> call = this.webServiceAPI.getStoresByName(token, storeName, "Azrieli TLV");
        CompletableFuture<List<Store>> future = new CompletableFuture<>();

        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                if (response.isSuccessful()) {
                    List<Store> storesList = response.body();
                    future.complete(storesList);
                } else {
                    future.completeExceptionally(new Error("Failed to fetch stores by name"));
                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}