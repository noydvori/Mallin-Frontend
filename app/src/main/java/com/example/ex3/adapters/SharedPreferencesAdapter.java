package com.example.ex3.adapters;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesAdapter {

    private String DATA_BASE_NAME = "DEV_TOOL_SHARED_PREFRENCES";
    private String IS_LOADED_DATA_BASE = "IS_LOADED_DATA_BASE";

    private String NAME = "CLIENT_NAME";

    private SharedPreferences mSharedPreferences;
    private static SharedPreferencesAdapter sharedPreferencesAdapter;


    public static SharedPreferencesAdapter getInstance(Context c) {
        if(sharedPreferencesAdapter == null) {
            sharedPreferencesAdapter = new SharedPreferencesAdapter(c);
        }
        return sharedPreferencesAdapter;
    }

    public SharedPreferencesAdapter(Context c) {
             this.mSharedPreferences = c.getSharedPreferences(DATA_BASE_NAME,Context.MODE_PRIVATE);
    }

    public boolean isDataLoaded() {
       return mSharedPreferences.getBoolean(IS_LOADED_DATA_BASE,false);
    }

    public void setDataLoaded(Boolean b) {
       mSharedPreferences.edit().putBoolean(IS_LOADED_DATA_BASE,b).apply();
    }

    public String getName() {
        return mSharedPreferences.getString(NAME,null);
    }


    public void setName(String text) {
         mSharedPreferences.edit().putString(NAME,text).apply();
    }
}
