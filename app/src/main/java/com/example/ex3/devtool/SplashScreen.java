package com.example.ex3.devtool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.ex3.R;
import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.adapters.SharedPreferencesAdapter;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.enteties.Accelerometer;
import com.example.ex3.devtool.enteties.Bluetooth;
import com.example.ex3.devtool.enteties.GraphNodeData;
import com.example.ex3.devtool.enteties.MagneticField;
import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.utils.ExportToXml;

public class SplashScreen extends AppCompatActivity {
    private GraphDatabase db;
   // db = SampleDatabase.getDatabase(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        db = GraphDatabase.getDatabase(this);
        ExportToXml exportToXml = new ExportToXml(this);

        new Thread(exportToXml::exportDataToXml).start();

//        new Thread(()->{
//            db.wifiDao().getAll().forEach(wifi->{
//                Log.d("SPlashScreen", " wifi: " + wifi.BSSID + " node: " + wifi.getNodeDataId());
//            });
//
//            db.magneticFieldDao().getAll().forEach(magneticField -> {
//                Log.d("SPlashScreen", " magneticField: x=" + magneticField.x + " y=" + magneticField.y + " z=" + magneticField.z + " node: " + magneticField.getNodeId());
//
//            });
//        }).start();
        if(SharedPreferencesAdapter.getInstance(this).isDataLoaded()){
            Intent intent = new Intent(this, DevTool.class);
            startActivity(intent);



        }else {
            new LoadDataAsyncTask().execute();


        }
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Load initial data into the database
            loadData(0, R.raw.floor0);
            loadData(1, R.raw.floor1);
            loadData(2, R.raw.floor2);
            loadData(3, R.raw.floor3);


            SharedPreferencesAdapter.getInstance(getApplicationContext()).setDataLoaded(true);
            runOnUiThread(() -> {
                Intent intent = new Intent(SplashScreen.this, DevTool.class);
                startActivity(intent);
            });

            return null;
        }

        private void loadData(int floor, int resource) {
            GrapthDataAdapter adapter = new GrapthDataAdapter(getApplicationContext(), floor);
            Graph graph = adapter.loadGrapthData(resource);
            db.graphNodeDao().insertAllNodes(graph.getNodes());
        }
    }

}
