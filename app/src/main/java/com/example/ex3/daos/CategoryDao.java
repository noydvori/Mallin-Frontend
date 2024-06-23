package com.example.ex3.daos;

import android.icu.util.ULocale;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.ex3.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    public void insert(Category category);
    @Update
    public  void update(Category category);
    @Delete
    public void delete(Category category);

    @Query("SELECT * FROM category WHERE category_name = :tag LIMIT 1")
    public Category getCategory(String tag);

    @Query("DELETE FROM category")
    void deleteAll();
    @Query("DELETE FROM category WHERE category_name = :tagName")
    void deleteByTagName(String tagName);

}