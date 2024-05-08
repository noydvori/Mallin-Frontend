package com.example.ex3.fetchers;

import com.example.ex3.api.CategoryAPI;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StoreFetcher {
    public interface FetchStoresCallback {
        void onSuccess(Category category);
        void onError(Throwable throwable);
    }

    public void fetchStores(String token, String storeType, FetchStoresCallback callback) {
        CategoryAPI categoryAPI = CategoryAPI.getInstance();

        CompletableFuture<Category> future = categoryAPI.getStoresByType(token, storeType);

        future.thenAccept(category -> {
            callback.onSuccess(category);
        }).exceptionally(ex -> {
            callback.onError(ex);
            return null;
        });
    }
}
