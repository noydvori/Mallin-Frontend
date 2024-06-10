package com.example.ex3.fetchers;

import com.example.ex3.api.TagAPI;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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


