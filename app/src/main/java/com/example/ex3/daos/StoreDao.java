package com.example.ex3.daos;
import androidx.room.Dao;

import androidx.room.Insert;
import androidx.room.Query;

import com.example.ex3.entities.Store;


import java.util.List;

@Dao
public interface StoreDao {
    @Query("SELECT * FROM stores WHERE storename=:name")
    Store get(String name);

    @Insert
    void insert(Store storeItem);

    @Query("SELECT * FROM stores")
    List<Store> getAll();

    @Query("DELETE FROM stores")
    void clear();

    //@Query("DELETE FROM contacts WHERE id=:id")
    //void delete(int id);
}
