package com.example.ex3.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ex3.entities.FavoriteStore;

import java.util.List;

@Dao
public interface FavoriteStoreDao {
    @Query("SELECT * FROM favoritestore WHERE storename=:name")
    FavoriteStore get(String name);

    @Insert
    void insert(FavoriteStore storeItem);

    @Query("SELECT * FROM favoritestore")
    List<FavoriteStore> getAll();

    @Query("DELETE FROM favoritestore")
    void clear();

    @Update
    void update(FavoriteStore store);

    @Query("SELECT * FROM favoritestore WHERE isFavorite = 1")
    List<FavoriteStore> getFavoriteStores();

    @Query("UPDATE favoritestore SET isFavorite = 1 WHERE storename = :name")
    void markAsFavorite(String name);

    @Query("SELECT * FROM favoritestore WHERE categoryId = :categoryId")
    List<FavoriteStore> getStoresByCategory(int categoryId);
}
