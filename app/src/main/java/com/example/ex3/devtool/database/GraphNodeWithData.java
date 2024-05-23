// GraphNodeWithData.java
package com.example.ex3.devtool.database;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.ex3.devtool.enteties.Accelerometer;
import com.example.ex3.devtool.enteties.Bluetooth;
import com.example.ex3.devtool.enteties.GraphNodeData;
import com.example.ex3.devtool.enteties.MagneticField;
import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.graph.GraphNode;

import java.util.List;

public class GraphNodeWithData {
    @Embedded
    public GraphNode graphNode;

    @Relation(parentColumn = "id", entityColumn = "nodeId")
    public List<GraphNodeData> graphNodeData;

    @Relation(entity = Wifi.class, parentColumn = "id", entityColumn = "nodeDataId")
    public List<Wifi> wifiList;

    @Relation(entity = MagneticField.class, parentColumn = "id", entityColumn = "nodeDataId")
    public List<MagneticField> magneticFieldList;

    @Relation(entity = Bluetooth.class, parentColumn = "id", entityColumn = "nodeDataId")
    public List<Bluetooth> bluetoothList;

    @Relation(entity = Accelerometer.class, parentColumn = "id", entityColumn = "nodeDataId")
    public List<Accelerometer> accelerometerList;
}
