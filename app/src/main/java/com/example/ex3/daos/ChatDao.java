package com.example.ex3.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ex3.entities.Chat;

import java.util.List;

@Dao
public interface ChatDao {
    @Query("SELECT * FROM chat")
    List<Chat> getAllChats();

    @Query("SELECT * FROM chat WHERE id = :id")
    Chat getChat(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chat... chats);

    @Update
    void update(Chat... chats);

    @Delete
    void delete(Chat... chats);
    // tom with query

    @Query("DELETE FROM chat")
    void deleteChats();
}
