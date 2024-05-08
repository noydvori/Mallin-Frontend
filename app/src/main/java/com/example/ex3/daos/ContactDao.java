package com.example.ex3.daos;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ex3.entities.Contact;
import com.example.ex3.entities.Store;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contacts WHERE username=:username")
    Contact get(String username);

    @Insert
    void insert(Store storeItem);

    @Query("SELECT * FROM contacts")
    List<Store> getAll();

    @Query("DELETE FROM contacts")
    void clear();

    //@Query("DELETE FROM contacts WHERE id=:id")
    //void delete(int id);
}