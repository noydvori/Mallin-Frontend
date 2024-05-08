package com.example.ex3.adapters;


import androidx.room.TypeConverter;

import com.example.ex3.objects.LastMsg;
import com.example.ex3.objects.UserInfo;
import com.google.gson.Gson;

public class LastMsgConverter {

    @TypeConverter
    public static LastMsg fromString(String value) {
        return new Gson().fromJson(value, LastMsg.class);
    }

    @TypeConverter
    public static String toString(LastMsg lastMsg) {
        return new Gson().toJson(lastMsg);
    }
}