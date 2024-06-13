package com.example.ex3.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ex3.entities.Store;
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
}
