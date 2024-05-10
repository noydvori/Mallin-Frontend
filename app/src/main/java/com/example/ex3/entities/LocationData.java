package com.example.ex3.entities;

import androidx.room.PrimaryKey;

import java.util.List;

public class LocationData {
    @PrimaryKey
    int id;
    String name;
    double x;
    double y;
    double magneticField;
    List<Wifi> wifiList;
    List<Bluetooth> bluetoothsList;


}
