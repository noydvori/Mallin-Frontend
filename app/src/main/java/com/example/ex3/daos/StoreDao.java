package com.example.ex3.daos;
import androidx.room.Dao;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ex3.entities.Store;


import java.util.List;

@Dao
public interface StoreDao {
    @Query("SELECT * FROM store WHERE storename=:name")
    Store get(String name);

    @Insert
    void insert(Store storeItem);

    @Query("SELECT * FROM store")
    List<Store> getAll();

    @Query("DELETE FROM store")
    void clear();
    @Update
    public void update(Store store);
    @Query("SELECT * FROM store WHERE categoryId = :categoryId")
    List<Store> getStoresByCategory(int categoryId);
}
