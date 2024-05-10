package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.devtool.MapScalingHandler;
import com.google.android.material.appbar.MaterialToolbar;

public class DevTool extends AppCompatActivity {
    private static final String mapActivity = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_map);
        MaterialToolbar toolbar = findViewById(R.id.devtool_toolbar);
        setSupportActionBar(toolbar);

        GraphOverlayImageView imageView = (GraphOverlayImageView)findViewById(R.id.devtool_map);
        imageView.setImage(ImageSource.resource(R.drawable.floor_1));

        Log.d(mapActivity, "start this app");
        GrapthDataAdapter dataAdapter = new GrapthDataAdapter(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                dataAdapter.loadGrapthData();
                imageView.setGraph(dataAdapter.getGraph());
                imageView.setOnImageEventListener(new MapScalingHandler(imageView));
            }
        }
                );
        thread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.devtool_app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks here
        int id = item.getItemId();

        if (id == R.id.action_lock) {
            // Handle the lock icon click
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}