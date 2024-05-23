package com.example.ex3.devtool.enteties;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
@Entity(tableName = "magnetic_field",
        foreignKeys = @ForeignKey(entity = GraphNodeData.class,
                parentColumns = "id",
                childColumns = "nodeDataId",
                onDelete = ForeignKey.CASCADE))
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
    private int nodeDataId; // Foreign key

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

    public void setNodeDataId(int id) {
        this.nodeDataId = id;
    }
}
