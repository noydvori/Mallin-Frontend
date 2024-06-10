package com.example.ex3.devtool.enteties;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
@Entity(tableName = "magnetic_field")
public class MagneticField {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "phone_type")
    public String phoneType;

    @ColumnInfo(name = "x_value")
    public float x
;

    @ColumnInfo(name = "y_value")
    public float y;

    @ColumnInfo(name = "z_value")
    public float z;

    @ColumnInfo(name = "node_id")
    private String nodeId; // Foreign key

    public MagneticField(float x, float y, float z, String nodeId, String phoneType) {
        this.x= x;
        this.y=y;
        this.z=z;
        this.nodeId = nodeId;
        this.phoneType = phoneType;
    }


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setNodeId(String id) {
        this.nodeId = id;
    }

    public String getNodeId() {
        return nodeId;
    }
}
