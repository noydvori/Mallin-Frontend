// MagneticFieldDao.java
package com.example.ex3.devtool.dao;
// MagneticFieldDao.java

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.enteties.MagneticField;

@Dao
public interface MagneticFieldDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MagneticField... magneticFields);
}
