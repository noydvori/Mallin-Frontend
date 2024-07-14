package com.example.ex3.viewModels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ex3.entities.Store;
import com.example.ex3.interfaces.StoresCallBack;
import java.util.List;

public class CurrentLocationViewModel extends ViewModel implements StoresCallBack {
    MutableLiveData<List<Store>> stores = new MutableLiveData<>();
    public MutableLiveData<List<Store>> getStores() {
        return this.stores;
    }
    @Override
    public void onResponse(List<Store> stores) {
        this.stores.setValue(stores);
    }
}
