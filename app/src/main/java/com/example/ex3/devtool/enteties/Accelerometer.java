package com.example.ex3.devtool.enteties;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "accelerometer")
public class Accelerometer {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "source")
    public String source;

    @ColumnInfo(name = "target")
    public String target;

    @ColumnInfo(name = "distance")
    public float distance;

    @ColumnInfo(name = "accuracy")
    public float accuracy;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
