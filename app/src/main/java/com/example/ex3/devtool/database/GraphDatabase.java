// GraphDatabase.java
package com.example.ex3.devtool.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.ex3.devtool.dao.AccelerometerDao;
import com.example.ex3.devtool.dao.BluetoothDao;
import com.example.ex3.devtool.dao.GraphNodeDao;
import com.example.ex3.devtool.dao.GraphNodeDataDao;
import com.example.ex3.devtool.dao.MagneticFieldDao;
import com.example.ex3.devtool.dao.WifiDao;
import com.example.ex3.devtool.enteties.Accelerometer;
import com.example.ex3.devtool.enteties.Bluetooth;
import com.example.ex3.devtool.enteties.GraphNodeData;
import com.example.ex3.devtool.enteties.MagneticField;
import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.graph.GraphNode;

@Database(entities = {GraphNode.class, GraphNodeData.class, Wifi.class, MagneticField.class, Bluetooth.class, Accelerometer.class}, version = 1)
public abstract class GraphDatabase extends RoomDatabase {
    public abstract GraphNodeDao graphNodeDao();
    public abstract GraphNodeDataDao graphNodeDataDao();
    public abstract WifiDao wifiDao();
    public abstract MagneticFieldDao magneticFieldDao();
    public abstract BluetoothDao bluetoothDao();
    public abstract AccelerometerDao accelerometerDao();

    private static volatile GraphDatabase INSTANCE;

    public static GraphDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GraphDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    GraphDatabase.class, "graph_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
