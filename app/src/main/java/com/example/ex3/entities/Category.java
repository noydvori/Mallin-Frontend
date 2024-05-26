package com.example.ex3.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.ex3.entities.Store;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String categoryName;
    private List<Store> storesList;

    public Category() {
        storesList = new ArrayList<>();
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
        storesList = new ArrayList<>();
    }

    public Category(String storeType, List<Store> storesOfType) {
        this.categoryName = storeType;
        storesList = storesOfType;
    }

    public void setStoresList(List<Store> storesList) {
        this.storesList = storesList;
    }
    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public List<Store> getStoresList() {
        return storesList;
    }

    public void addStore(Store store) {
        storesList.add(store);
    }
}
