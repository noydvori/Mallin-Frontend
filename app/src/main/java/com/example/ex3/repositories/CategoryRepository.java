package com.example.ex3.repositories;


import static com.example.ex3.repositories.MyApplication.context;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.ex3.api.CategoryAPI;
import com.example.ex3.daos.StoreDao;
import com.example.ex3.entities.Store;
import com.example.ex3.localDB.AppDB;
import com.example.ex3.objects.Category;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CategoryRepository {
    private StoreDao dao;
    private storesListData storesListData;
    private CategoryAPI api;
    private String token;
    private String type;


    public CategoryRepository(String token, String t) {
        this.token = token;
        AppDB db = AppDB.getInstance();
        dao = db.storeDao();
        storesListData = new storesListData();
        api = CategoryAPI.getInstance();
        type= t;
    }

    public void logout() {
        dao.clear();
    }

    class storesListData extends MutableLiveData<List<Store>> {
        public storesListData() {
            super();
            setValue(new LinkedList<>());
        }


        @Override
        protected void onActive() {

            super.onActive();
            new Thread(()->{
                storesListData.postValue(dao.getAll());
            }).start();
        }
    }
    public void reload() {
        CompletableFuture<Category> future = api.getStoresByType(token,type);
        future.thenAccept(category -> {
            dao.clear();
            for (Store c : category.getStoresList()) {
                dao.insert(c);
            }
//            List<Contact> contactsTemp = dao.getAll();
            storesListData.setValue(category.getStoresList());
        }).exceptionally(error -> {
            Toast.makeText(context, error.getCause().getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    public CategoryRepository.storesListData getAll() {
        //extract the contacts from the room
        return storesListData;
    }

}
