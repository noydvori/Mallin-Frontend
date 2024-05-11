package com.example.ex3.api;


import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;

import java.io.Console;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryAPI {
    private static CategoryAPI instance;
    private Retrofit retrofit;
    private WebServiceAPI webServiceAPI;

    private CategoryAPI() {
        retrofit = new Retrofit.Builder()
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

    public void setRetrofit(String url){
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
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
                        String workingHoures = responseStore.getWorkingHours();
                        String floorNumber = responseStore.getFloor();
                        String logoUrl = responseStore.getLogoUrl();
                        System.out.println(storeName);
                        System.out.println(workingHoures);
                        System.out.println(floorNumber);
                        System.out.println(logoUrl);

                        // Load and display image using Glide
                        // Glide.with(context).load(logoUrl).into(imageView);

                        Store storeItem = new Store(storeName, workingHoures, floorNumber, logoUrl, type);
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

            }
        });
        return future;
    }

    //public CompletableFuture<Contact> addNewContact(String token, String username) {
    //    Username usernameObj = new Username(username);
    //    Call<ContactNoMsg> call = this.webServiceAPI.addNewContact(token, usernameObj);
    //    CompletableFuture<Contact> future = new CompletableFuture<>();
    //    call.enqueue(new Callback<ContactNoMsg>() {
    //        @Override
    //        public void onResponse(Call<ContactNoMsg> call, Response<ContactNoMsg> response) {
    //            if (response.isSuccessful()) {
    //                UserInfo userInfo = new UserInfo(response.body().getUserInfo().getUsername(), response.body().getUserInfo().getDisplayName(), response.body().getUserInfo().getProfilePic());
    //                Contact newContact = new Contact(response.body().getId(), userInfo, null);
    //                future.complete(newContact); // extract the new contact from the server's response
    //            } else if (response.code() == 403) {
    //                future.completeExceptionally(new Error("Wrong username"));
    //            } else if (response.code() == 404) {
    //                future.completeExceptionally(new Error("There is no such user"));
    //            } else {
    //                future.completeExceptionally(new Error("invalid_token"));
    //            }
    //        }
//
    //        @Override
    //        public void onFailure(Call<ContactNoMsg> call, Throwable t) {
    //            t.printStackTrace(); // Complete the CompletableFuture exceptionally with the thrown Throwable
    //        }
    //    });
    //    return future;
    //}
//
    //public CompletableFuture<String> deleteContact(String token, int id) {
    //    Call<Void> call = this.webServiceAPI.deleteContact(id, token);
    //    CompletableFuture<String> future = new CompletableFuture<>();
    //    call.enqueue(new Callback<Void>() {
    //        @Override
    //        public void onResponse(Call<Void> call, Response<Void> response) {
    //            if (response.isSuccessful()) {
    //                future.complete("ok");
    //            } else if (response.code() == 404) {
    //                future.completeExceptionally(new Error("There is no chat with this contact"));
    //            } else {
    //                future.completeExceptionally(new Error("invalid token"));
    //            }
    //        }
//
    //        @Override
    //        public void onFailure(Call<Void> call, Throwable t) {
    //            t.printStackTrace(); // Complete the CompletableFuture exceptionally with the thrown Throwable
    //        }
    //    });
    //    return future;
    //}
}