// Bluetooth.java
package com.example.ex3.devtool.enteties;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "bluetooth",
        foreignKeys = @ForeignKey(entity = GraphNodeData.class,
                parentColumns = "id",
                childColumns = "nodeDataId",
                onDelete = ForeignKey.CASCADE))
public class Bluetooth {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int nodeDataId; // Foreign key
    private String deviceName;
    private int signalStrength;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNodeDataId() { return nodeDataId; }
    public void setNodeDataId(int nodeDataId) { this.nodeDataId = nodeDataId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public int getSignalStrength() { return signalStrength; }
    public void setSignalStrength(int signalStrength) { this.signalStrength = signalStrength; }
}
