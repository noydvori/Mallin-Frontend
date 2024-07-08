package com.example.ex3.objects;

import com.example.ex3.entities.Store;

import java.util.List;

public class LocationAndPath {
    private Store store;
    private List<Store> stores;

    public LocationAndPath(Store store, List<Store> stores) {
        this.store = store;
        this.stores = stores;
    }
}
