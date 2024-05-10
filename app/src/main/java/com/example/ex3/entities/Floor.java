package com.example.ex3.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.ex3.converters.MsgConverter;
import com.example.ex3.converters.UserConverter;

import java.util.List;

@Entity(tableName = "floor")
@TypeConverters({UserConverter.class, MsgConverter.class})
public class Floor {
    @PrimaryKey
    private int id;



    List<LocationData> storeDataList;


}
