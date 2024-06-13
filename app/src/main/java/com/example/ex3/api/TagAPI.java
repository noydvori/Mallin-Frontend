package com.example.ex3.api;



import static com.example.ex3.MyApplication.context;

import com.example.ex3.R;
import com.example.ex3.utils.UserPreferencesUtils;

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
        String baseUrl = context.getString(R.string.BASE_URL);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    public static TagAPI getInstance() {
        if (instance == null) {
            instance = new TagAPI();
        }
        return instance;
    }

    public CompletableFuture<List<String>> getTypes(String token) {
        String mallName = UserPreferencesUtils.getMallName(context);

        Call<List<String>> call = this.webServiceAPI.getTypes(token, mallName);
        CompletableFuture<List<String>> future = new CompletableFuture<>();


        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    List<String> tagsList = response.body();
                    System.out.println(tagsList);
                    future.complete(tagsList);
                } else {
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


