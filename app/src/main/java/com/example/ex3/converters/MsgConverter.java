package com.example.ex3.converters;

import androidx.room.TypeConverter;

import com.example.ex3.entities.Msg;
import com.google.gson.Gson;

public class MsgConverter {
    @TypeConverter
    public static String chatToString(Msg msg) {
        return new Gson().toJson(msg);
    }

    @TypeConverter
    public static Msg stringToChat(String value) {
        return new Gson().fromJson(value, Msg.class);
    }
}
