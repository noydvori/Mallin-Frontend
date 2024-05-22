package com.example.ex3.fetchers;

import com.example.ex3.api.CategoryAPI;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StoreFetcher {
    public interface FetchStoresCallback {
        void onSuccess(Category category);

        void onError(Throwable throwable);
    }

    public interface FetchSearchStoresCallback {
        void onSuccess(List<Category> category);

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

    public void fetchStoresByName(String token, String storeName, FetchSearchStoresCallback callback) {
        CategoryAPI categoryAPI = CategoryAPI.getInstance();

        CompletableFuture<List<Store>> future = categoryAPI.getStoresByName(token, storeName);

        future.thenAccept(storeList -> {
            // Create a set to store unique store types
            Set<String> uniqueStoreTypes = new HashSet<>();
            for (Store store : storeList) {
                uniqueStoreTypes.add(store.getStoreType());
            }

            // Create a list of categories
            List<Category> categories = new ArrayList<>();
            for (String storeType : uniqueStoreTypes) {
                List<Store> storesOfType = storeList.stream()
                        .filter(store -> storeType.equals(store.getStoreType()))
                        .collect(Collectors.toList());
                Category category = new Category(storeType, storesOfType);
                categories.add(category);
            }
            // Call onSuccess with the list of categories
            callback.onSuccess(categories);
        }).exceptionally(ex -> {
            callback.onError(ex);
            return null;
        });
    }
}


