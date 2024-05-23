package com.example.ex3.devtool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.ex3.R;
import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.adapters.SharedPreferencesAdapter;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.enteties.Accelerometer;
import com.example.ex3.devtool.enteties.Bluetooth;
import com.example.ex3.devtool.enteties.GraphNodeData;
import com.example.ex3.devtool.enteties.MagneticField;
import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.graph.GraphNode;

public class SplashScreen extends AppCompatActivity {
    private GraphDatabase db;
   // db = SampleDatabase.getDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if(SharedPreferencesAdapter.getInstance(this).isDataLoaded()) {
            Intent intent = new Intent(this, DevTool.class);
          //  startActivity(intent);



        }else {
         //   new LoadDataAsyncTask().execute();


        }
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Load initial data into the database
            GraphNode node1 = new GraphNode("1", "Node1", 10.0f, 20.0f);
            GraphNode node2 = new GraphNode("2", "Node2", 30.0f, 40.0f);

            db.graphNodeDao().insertAll(node1, node2);

            GraphNodeData node1Data = new GraphNodeData();
            node1Data.setNodeId("1");

            db.graphNodeDataDao().insertAll(node1Data);

            Wifi wifi1 = new Wifi();
            wifi1.setId(node1Data.getId());
            wifi1.setBSSID("SSID1");
            wifi1.setRssi(-50);

            db.wifiDao().insertAll(wifi1);

            MagneticField magneticField1 = new MagneticField();
            magneticField1.setNodeDataId(node1Data.getId());
            magneticField1.setX(1.0f);
            magneticField1.setY(0.5f);
            magneticField1.setZ(0.2f);

            db.magneticFieldDao().insertAll(magneticField1);

            Bluetooth bluetooth1 = new Bluetooth();
            bluetooth1.setNodeDataId(node1Data.getId());
            bluetooth1.setDeviceName("Device1");
            bluetooth1.setSignalStrength(-70);

            db.bluetoothDao().insertAll(bluetooth1);

//            Accelerometer accelerometer1 = new Accelerometer();
//            accelerometer1.setNodeDataId(node1Data.getId());
//            accelerometer1.setX(0.1f);
//            accelerometer1.setY(0.2f);
//            accelerometer1.setZ(0.3f);

       //     db.accelerometerDao().insertAll(accelerometer1);
            SharedPreferencesAdapter.getInstance(getApplicationContext()).setDataLoaded(true);
            return null;
        }
    }

}
