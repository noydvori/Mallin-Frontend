package com.example.ex3.devtool.dao;
// BluetoothDao.java
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.enteties.Bluetooth;

@Dao
public interface BluetoothDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Bluetooth... bluetooths);
}

