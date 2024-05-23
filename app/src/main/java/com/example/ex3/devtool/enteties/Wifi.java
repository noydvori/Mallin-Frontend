package com.example.ex3.devtool.enteties;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;

@Entity(tableName = "wifi",
        foreignKeys = @ForeignKey(entity = GraphNodeData.class,
                parentColumns = "id",
                childColumns = "nodeDataId",
                onDelete = ForeignKey.CASCADE))
public class Wifi {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_id")
    public String userID;

    @ColumnInfo(name = "ssid")
    public String SSID;

    @ColumnInfo(name = "bssid")
    public String BSSID;

    @ColumnInfo(name = "rssi")
    public int rssi;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
