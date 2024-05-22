package com.example.ex3.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

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
    @ColumnInfo(name = "categoryId")
    private int categoryId;


    private boolean isAddedToList;

    private boolean isFavorite;

    public Store(String storename,String workingHours, String floor, String logoPic,String storeType,boolean isFavorite) {
        this.storename = storename;
        this.workingHours = workingHours;
        this.floor = floor;
        this.logoPic =logoPic;
        this.storeType = storeType;
        this.isAddedToList = false;
        this.isFavorite = isFavorite;
    }

    @NonNull
    public String getStoreName() {
        return this.storename;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
    public boolean isAddedToList() {
        return isAddedToList;
    }

    public void setAddedToList(boolean addedToList) {
        isAddedToList = addedToList;
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
    public boolean isOpen(){
        return true;
    }
    public boolean isFavorite(){
        return this.isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(storename, store.storename);
    }
    @Override
    public int hashCode() {
        return Objects.hash(storename);
    }
}
