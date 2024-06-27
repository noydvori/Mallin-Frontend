package com.example.ex3.api;



import java.util.List;
import java.util.concurrent.CompletableFuture;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TagAPI {
    private static TagAPI instance;
    private final WebServiceAPI webServiceAPI;

    private TagAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.153.1:5000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static TagAPI getInstance() {
        if (instance == null) {
            instance = new TagAPI(); // Initialize instance if null
        }
        return instance;
    }

    public CompletableFuture<List<String>> getTypes(String token) {
        Call<List<String>> call = this.webServiceAPI.getTypes(token, "Azrieli TLV");
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        System.out.println("1");

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> tagsList = response.body();
                    System.out.println(tagsList);
                    future.complete(tagsList);
                } else {
                    System.out.println("2");
                    future.completeExceptionally(new Error("Failed to fetch stores by name"));

                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}


