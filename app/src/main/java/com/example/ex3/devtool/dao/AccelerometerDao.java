package com.example.ex3.devtool.dao;

// AccelerometerDao.java

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.enteties.Accelerometer;

@Dao
public interface AccelerometerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Accelerometer... accelerometers);
}
    