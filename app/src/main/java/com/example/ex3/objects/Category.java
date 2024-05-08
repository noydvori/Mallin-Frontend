package com.example.ex3.objects;

import com.example.ex3.entities.Store;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String categoryName;
    private List<Store> storesList;

    public Category(String categoryName, List<Store> storesList) {
        this.categoryName = categoryName;
        this.storesList = storesList;
    }
    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.storesList = new ArrayList<>();
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

    public void setStoreItemList(List<Store> storeItemList) {
        this.storesList = storeItemList;
    }
    public void addStore(Store store) {
        this.storesList.add(store);
    }
}
