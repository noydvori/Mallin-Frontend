package com.example.ex3.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ex3.entities.Token;

import java.util.List;

@Dao
public interface TokenDao {

    @Query("SELECT * FROM token")
    Token get();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Token... tokens);

    @Update
    void update(Token... tokens);

    @Delete
    void delete(Token... tokens);

    @Query("DELETE FROM token")
    void deleteAll();
}
