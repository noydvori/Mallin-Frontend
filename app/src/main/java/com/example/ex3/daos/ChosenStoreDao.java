package com.example.ex3.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.entities.ChosenStore;

import java.util.List;

@Dao
public interface ChosenStoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChosenStore(ChosenStore store);

    @Query("DELETE FROM chosen_stores WHERE storename = :storename") // Update to use storename
    void deleteChosenStore(String storename); // Change parameter type to String

    @Query("SELECT * FROM chosen_stores")
    List<ChosenStore> getAllChosenStores();
}
