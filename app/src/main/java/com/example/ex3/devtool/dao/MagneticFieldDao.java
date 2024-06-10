// MagneticFieldDao.java
package com.example.ex3.devtool.dao;
// MagneticFieldDao.java

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.enteties.MagneticField;

import java.util.List;

@Dao
public interface MagneticFieldDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MagneticField... magneticFields);

    @Query("SELECT * FROM magnetic_field")
    List<MagneticField> getAll();

    @Query("DELETE FROM magnetic_field WHERE node_id = :id")
    void deleteByNodeId(String id);
}
