package com.example.ex3.converters;

import androidx.room.TypeConverter;

import com.example.ex3.entities.User;
import com.google.gson.Gson;

public class UserConverter {
    @TypeConverter
    public static String userToString(User user) {
        return new Gson().toJson(user);
    }

    @TypeConverter
    public static User stringToUser(String value) {
        return new Gson().fromJson(value, User.class);
    }
}
