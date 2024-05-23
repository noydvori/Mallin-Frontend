// GraphNodeDataDao.java
package com.example.ex3.devtool.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.enteties.GraphNodeData;

@Dao
public interface GraphNodeDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(GraphNodeData... graphNodeData);
}
