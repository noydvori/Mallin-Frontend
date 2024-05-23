// WifiDao.java
package com.example.ex3.devtool.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.enteties.Wifi;

@Dao
public interface WifiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Wifi... wifis);
}
