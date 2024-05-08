package com.example.ex3.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ex3.objects.LastMsg;
import com.example.ex3.objects.UserInfo;


@Entity(tableName = "stores")
public class Store {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "storename")
    private String storename;
    @ColumnInfo(name = "workingHours")

    private String workingHours;
    @ColumnInfo(name = "floor")

    private String floor;
    @ColumnInfo(name = "logoPic")

    private String logoPic;
    @ColumnInfo(name = "storeType")


    private String storeType;


    public Store(String storename,String workingHours, String floor, String logoPic,String storeType) {
        this.storename = storename;
        this.workingHours = workingHours;
        this.floor = floor;
        this.logoPic =logoPic;
        this.storeType = storeType;
    }

    @NonNull
    public String getStoreName() {
        return this.storename;
    }

    public void setName(@NonNull String name) {
        this.storename = name;
    }

    public String getFloor() {
        return floor;
    }

    public String getLogoUrl() {
        return logoPic;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoPic = logoUrl;
    }

    public String getLogoPic() {
        return logoPic;
    }

    public String getStoreType() {
        return storeType;
    }

    @NonNull
    public String getStorename() {
        return storename;
    }

    public String getWorkingHours() {
        return workingHours;
    }
}
