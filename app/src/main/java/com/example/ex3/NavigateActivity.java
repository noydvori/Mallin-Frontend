package com.example.ex3;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.managers.CustomAccelerometerManager;
import com.example.ex3.devtool.handlers.MapScalingHandler;
import com.example.ex3.managers.NavigationWifiManager;
import com.example.ex3.viewModels.NavigateViewModel;
import com.example.ex3.viewModels.NavigateViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class NavigateActivity extends AppCompatActivity {
    private static final String mapActivity = "MapActivity";
    private NavigateViewModel navigateViewModel;
    private NavigationWifiManager navigationWifiManager;
    private BottomNavigationView bottomNavigationView;

    private GraphOverlayImageView mImageView;
    private CustomAccelerometerManager customAccelerometerManager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        navigateViewModel = new ViewModelProvider(this, new NavigateViewModelFactory(GraphDatabase.getDatabase(this))).get(NavigateViewModel.class);
        mImageView = findViewById(R.id.devtool_map);
        tabLayout = findViewById(R.id.tab_layout);
        mImageView.setImage(ImageSource.resource(R.drawable.floor_1));
        Log.d(mapActivity, "start this app");
        customAccelerometerManager = new CustomAccelerometerManager(this);
        bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.getMenu().findItem(R.id.menu_navigate).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.menu_home:
                    intent = new Intent(NavigateActivity.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                case R.id.menu_navigate:
                    return true;
                case R.id.menu_favorites:
                    intent = new Intent(NavigateActivity.this, Favorites.class);
                    startActivity(intent);
                    return true;
                default:
                    return false;
            }
        });

        navigateViewModel.getFloorImage().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mImageView.setImage(ImageSource.resource(integer));
                mImageView.setOnImageEventListener(new MapScalingHandler(mImageView));
            }
        });

        graphChangedListeners(mImageView);

        // Check and request permissions for location if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            // Initialize the WiFi manager
            initializeWifiManager();
        }

        // Add tabs to the TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Floor 0"));
        tabLayout.addTab(tabLayout.newTab().setText("Floor 1"));
        tabLayout.addTab(tabLayout.newTab().setText("Floor 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Floor 3"));

        // Add TabSelectedListener to change image based on selected tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                setFloor(position, "Floor " + position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });

        // Set initial floor
        setFloor(0, "Floor 0");
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Stop WiFi scan when activity is no longer visible
        if (navigationWifiManager != null) {
            navigationWifiManager.stopScan();
        }
    }



    private void graphChangedListeners(GraphOverlayImageView imageView) {
        navigateViewModel.getSelectedFloor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (navigateViewModel.getGraphs().get(integer).getValue() != null) {
                    navigateViewModel.getGraphs().get(integer).getValue().forEach(node -> Log.d("GraphOverlay", "node: " + node.getId() + " name: " + node.getName()));
                    Graph graph = new Graph(navigateViewModel.getGraphs().get(integer).getValue());
                    imageView.setGraph(graph);
                } else {
                    Log.d("DevTool", "graph: " + integer + " is null");
                }
            }
        });

        for (int i = 0; i < navigateViewModel.getGraphs().size(); i++) {
            int finalI = i;
            navigateViewModel.getGraphs().get(i).observe(this, new Observer<List<GraphNode>>() {
                @Override
                public void onChanged(List<GraphNode> graphNodes) {
                    graphNodes.forEach(node -> Log.d("Test", " node: " + node.getName()));
                    if (navigateViewModel.getSelectedFloor().getValue() == finalI) {
                        Graph graph = new Graph(navigateViewModel.getGraphs().get(finalI).getValue());
                        imageView.setGraph(graph);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Initialize the WiFi manager
                initializeWifiManager();
            } else {
                // Handle the case where the user denies the permission
            }
        }
    }

    private void initializeWifiManager() {
        navigationWifiManager = new NavigationWifiManager(this);
    }


    public int getFloorResource(Integer floor) {
        switch (floor) {
            case 0: return R.drawable.floor_0;
            case 1: return R.drawable.floor_1;
            case 2: return R.drawable.floor_2;
            default: return R.drawable.floor_3;
        }
    }

    private void setFloor(int i, CharSequence title) {
        if (title != null) {
            navigateViewModel.setTitle(title.toString());
        }
        navigateViewModel.setSelectedFloor(i);
        navigateViewModel.setFloorImage(getFloorResource(i));
    }
}
