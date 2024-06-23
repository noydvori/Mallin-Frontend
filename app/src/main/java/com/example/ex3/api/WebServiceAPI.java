package com.example.ex3.api;

import com.example.ex3.entities.Category;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.NameAndPassword;
import com.example.ex3.objects.User;
import com.example.ex3.objects.UserInfo;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("AzrieliStore/mallname/{mallname}")
    Call<List<String>> getTypes(
            @Header("Authorization") String token,
            @Path("mallname") String mallname
    );

    @GET("Users")
    Call<List<Store>> getFavorites(@Header("Authorization") String token);

    @GET("AzrieliStore/type/{storeType}/paged")
    Call<List<Store>> getStoresByTypePaged(
            @Header("Authorization") String token,
            @Path("storeType") String storeType,
            @Query("mallname") String mallname,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @PUT("Users/favorites/add")
    Call<Void> addToFavorites(@Header("Authorization") String token, @Body Store store);

    @PUT("Users/favorites/remove")
    Call<Void> removeFromFavorites(@Header("Authorization") String token, @Body Store store);
}
