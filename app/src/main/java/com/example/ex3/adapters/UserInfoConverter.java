package com.example.ex3.adapters;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.example.ex3.objects.UserInfo;

public class UserInfoConverter {
    private Gson gson = new Gson();

    @TypeConverter
    public String fromUserInfo(UserInfo userInfo) {
        return gson.toJson(userInfo);
    }

    @TypeConverter
    public UserInfo toUserInfo(String userInfoJson) {
        return gson.fromJson(userInfoJson, UserInfo.class);
    }
}
