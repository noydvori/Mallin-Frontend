package com.example.ex3.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ex3.entities.Msg;

import java.util.List;

@Dao
public interface MsgDao {
    @Query("SELECT * FROM msg")
    List<Msg> index();

    @Query("SELECT * FROM msg WHERE id = :id")
    Msg getMsg(int id);

    @Insert
    void insert(Msg ...messages);

    @Update
    void update(Msg ...messages);

    @Delete
    void delete(Msg...messages);
}
