package com.example.ex3.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "token")
public class Token {
    @PrimaryKey @NonNull
    private String token;

    public Token(@NonNull String token) {
        this.token = token;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }
}
