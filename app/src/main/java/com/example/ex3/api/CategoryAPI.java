package com.example.ex3.api;

import static com.example.ex3.MyApplication.context;

import com.example.ex3.R;
import com.example.ex3.entities.Store;
import com.example.ex3.entities.Category;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.ex3.utils.UserPreferencesUtils;


public class CategoryAPI {
    private static CategoryAPI instance;
    private final WebServiceAPI webServiceAPI;

    private CategoryAPI() {
        String baseUrl = context.getString(R.string.BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
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

    public CompletableFuture<Category> getStoresByType(String token,String storeType) {
        String mallName = UserPreferencesUtils.getMallName(context);

        Call<Category> call = this.webServiceAPI.getStoresByType(token,storeType,mallName);
        CompletableFuture<Category> future = new CompletableFuture<>();

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    Category category = new Category(storeType);
                    Category responseList = response.body();
                    assert responseList != null;
                    List<Store> storesList = responseList.getStoresList();
                    for (Store responseStore : storesList) {
                        String storeName = responseStore.getStoreName();
                        String workingHours = responseStore.getWorkingHours();
                        String floorNumber = responseStore.getFloor();
                        String logoUrl = responseStore.getLogoUrl();
                        String type = responseStore.getStoreType();
                        Store storeItem = new Store(storeName, workingHours, floorNumber, logoUrl, type,false);
                        category.addStore(storeItem);
                    }
                    future.complete(category);
                } else {
                    future.completeExceptionally(new Error("Invalid token"));
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