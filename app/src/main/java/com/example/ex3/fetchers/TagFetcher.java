package com.example.ex3.fetchers;

import com.example.ex3.api.CategoryAPI;
import com.example.ex3.api.TagAPI;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TagFetcher {
    public interface FetchTagsCallback {
        void onSuccess(List<String> tags);

        void onError(Throwable throwable);
    }


    public void fetchTags(String token, FetchTagsCallback callback) {
        TagAPI tagAPI = TagAPI.getInstance();

        CompletableFuture<List<String>> future = tagAPI.getTypes(token);


        future.thenAccept(list -> {
            callback.onSuccess(list);
        }).exceptionally(ex -> {
            callback.onError(ex);
            return null;
        });
    }
}


