package com.example.ex3.utils;

import static com.example.ex3.MyApplication.context;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Store;
import com.example.ex3.objects.Paths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserPreferencesUtils {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String MALL_NAME_KEY = "MALL_NAME";
    private static final String TOKEN_KEY = "TOKEN";
    private static final String CHOSEN_STORES_KEY = "CHOSEN_STORES";
    private static final String FAVORITE_STORES_KEY = "FAVORITE_STORES";

    private static final Gson gson = new Gson();
    private static final String NODES_PATH_KEY = "NODES_PATH";
    private static final String STORES_PATH_KEY ="STORES_PATH" ;
    private static final String LOCATION_KEY ="LOCATION" ;
    private static final String CLOSEST_STORES_KEY ="CLOSEST_STORES" ;


    public static void setMallName(Context context, String mallName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(MALL_NAME_KEY, mallName);
        editor.apply();
    }

    public static void setToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    public static void setChosenStores(Context context, List<Store> chosenStores) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(chosenStores);
        editor.putString(CHOSEN_STORES_KEY, json);
        editor.apply();
    }

    public static void setFavoriteStores(Context context, List<Store> favoriteStores) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(favoriteStores);
        editor.putString(FAVORITE_STORES_KEY, json);
        editor.apply();
    }
    public static void setPaths(Context context, Paths paths) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json1 = gson.toJson(paths.getNodes());
        editor.putString(NODES_PATH_KEY, json1);
        String json2 = gson.toJson(paths.getStores());
        editor.putString(STORES_PATH_KEY, json2);
        editor.apply();
    }
    public static void setNodes(Context context, List<GraphNode> nodes) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json1 = gson.toJson(nodes);
        editor.putString(NODES_PATH_KEY, json1);
        editor.apply();
    }
    public static List<Store> getStores(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(STORES_PATH_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Store>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static String getMallName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(MALL_NAME_KEY, "Azrieli TLV");
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TOKEN_KEY, "");
    }

    public static List<Store> getChosenStores(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CHOSEN_STORES_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Store>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static List<Store> getFavoriteStores(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(FAVORITE_STORES_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Store>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void addFavoriteStore(Context context, Store store) {
        List<Store> favoriteStores = getFavoriteStores(context);
        if (!favoriteStores.contains(store)) {
            favoriteStores.add(store);
            setFavoriteStores(context, favoriteStores);
        }
    }

    public static void removeFavoriteStore(Context context, Store store) {
        List<Store> favoriteStores = getFavoriteStores(context);
        if (favoriteStores.contains(store)) {
            favoriteStores.remove(store);
            setFavoriteStores(context, favoriteStores);
        }
    }

    public static Store getLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(LOCATION_KEY, null);
        if (json == null) {
            return null; // Return null or handle default location
        }
        return gson.fromJson(json, Store.class);
    }

    public static void setLocation(Store location) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(location);
        editor.putString(LOCATION_KEY, json);
        editor.apply();
    }

    public static void setClosestStores(Context context, List<Store> closestStores) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(closestStores);
        editor.putString(CLOSEST_STORES_KEY, json);
        editor.apply();
    }
    public static Store getClosestStores(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CLOSEST_STORES_KEY, null);
        if (json == null) {
            return null;
        }
        Type type = new TypeToken<List<Store>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
