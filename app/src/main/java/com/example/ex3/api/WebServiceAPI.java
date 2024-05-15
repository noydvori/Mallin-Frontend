package com.example.ex3.api;

import com.example.ex3.entities.Store;
import com.example.ex3.objects.Category;
import com.example.ex3.objects.NameAndPassword;
import com.example.ex3.objects.User;
import com.example.ex3.objects.UserInfo;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceAPI {
    @GET("Users")
    Call<UserInfo> getUser(@Header("Authorization") String token);

    @POST("Users")
    Call<Void> registerUser(@Body User user);

    @POST("Tokens")
    Call<String> createToken(@Body NameAndPassword nameAndPassword);

    @GET("AzrieliStore/type/{storeType}")
    Call<Category> getStoresByType(
            @Header("Authorization") String token,
            @Path("storeType") String storeType,
            @Query("mallname") String mallname
    );
    @GET("AzrieliStore/name/{storeName}")
    Call<List<Store>> getStoresByName(
            @Header("Authorization") String token,
            @Path("storeName") String storeName,
            @Query("mallname") String mallname
    );

}