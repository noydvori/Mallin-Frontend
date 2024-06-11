// WifiDao.java
package com.example.ex3.devtool.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.enteties.Wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Dao
public interface WifiDao {
    //void insertAll(Wifi... wifis);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Wifi> wifiList);
    @Query("SELECT * FROM wifi")
    List<Wifi> getAll();

    @Query("DELETE FROM wifi WHERE nodeId = :nodeDataId")
    void deleteByNodeId(String nodeDataId);
}
