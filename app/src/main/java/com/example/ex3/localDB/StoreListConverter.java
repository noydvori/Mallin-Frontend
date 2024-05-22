package com.example.ex3.localDB;

import androidx.room.TypeConverter;

import com.example.ex3.entities.Store;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StoreListConverter {
    @TypeConverter
    public static List<Store> fromString(String value) {
        Type listType = new TypeToken<List<Store>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<Store> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
