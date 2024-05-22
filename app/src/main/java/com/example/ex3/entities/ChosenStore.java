package com.example.ex3.entities;

import androidx.annotation.NonNull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chosen_stores")
public class ChosenStore {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "storename")
    private String storename;

    @NonNull
    public String getStorename() {
        return storename;
    }

    public void setStorename(@NonNull String storename) {
        this.storename = storename;
    }

}
