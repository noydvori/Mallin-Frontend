package com.example.ex3.daos;

import android.icu.util.ULocale;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.ex3.objects.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Query("SELECT * FROM categories")
    List<ULocale.Category> getAllCategories();

    // Add more queries as needed, such as getting category by name or ID
}