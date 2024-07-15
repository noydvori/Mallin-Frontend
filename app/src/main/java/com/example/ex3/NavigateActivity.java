package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.ex3.components.PathOverlayImageView;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.handlers.MapTappingHandler;
import com.example.ex3.devtool.managers.CustomAccelerometerManager;
import com.example.ex3.devtool.handlers.MapScalingHandler;
import com.example.ex3.handlers.NavigationMapTappingHandler;
import com.example.ex3.managers.NavigationWifiManager;
import com.example.ex3.utils.UserPreferencesUtils;
import com.example.ex3.viewModels.NavigateViewModel;
import com.example.ex3.viewModels.NavigateViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class NavigateActivity extends AppCompatActivity {
    private static final String mapActivity = "MapActivity";
    private NavigateViewModel navigateViewModel;
    private Snackbar floorSnackbar;


    private NavigationWifiManager navigationWifiManager;
    private MutableLiveData<GraphNode> node;

    private BottomNavigationView bottomNavigationView;
    private PathOverlayImageView mImageView;
    private List<GraphNode> route;
    private CustomAccelerometerManager customAccelerometerManager;
    private TabLayout tabLayout;
    private NavigationMapTappingHandler mMapTappingHandler;
    FloatingActionButton centerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        navigateViewModel = new ViewModelProvider(this, new NavigateViewModelFactory(GraphDatabase.getDatabase(this))).get(NavigateViewModel.class);
        mImageView = findViewById(R.id.devtool_map);
        tabLayout = findViewById(R.id.tab_layout);
        mImageView.setImage(ImageSource.resource(R.drawable.floor_1));
        Log.d(mapActivity, "start this app");
        mMapTappingHandler = new NavigationMapTappingHandler(mImageView, navigateViewModel);
        centerButton = findViewById(R.id.center);
        centerButton.setOnClickListener(v -> mImageView.centerOnLocation());

        route = UserPreferencesUtils.getNodes(this);
        mImageView.setInitialized(false);

        int initialFloor = 0; // default floor

        if (route != null && !route.isEmpty()) {
            mImageView.setPath(route);
            mImageView.setInitialized(false);
            mImageView.setLocation(route.get(0));
            initialFloor = route.get(0).getFloor();
            mImageView.setCurrentFloor(initialFloor);
        }
        setFloor(initialFloor, "Floor " + initialFloor);

        // Show initial Snackbar
        showFloorSnackbar(initialFloor);

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

        navigateViewModel.getFloorImage().observe(this, integer -> {
            mImageView.setImage(ImageSource.resource(integer));
            mImageView.setOnImageEventListener(new MapScalingHandler(mImageView));
            mImageView.setOnTouchListener(mMapTappingHandler);

        navigateViewModel.getCurrentLocation().observe(this, new Observer<GraphNode>() {
            @Override

            public void onChanged(GraphNode node) {
                Log.d("NavigateActivity","liveLocation: " + node);
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

        // Set the correct tab based on the initial floor
        tabLayout.getTabAt(initialFloor).select();

        // Add TabSelectedListener to change image based on selected tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mImageView.setCurrentFloor(position);
                setFloor(position, "Floor " + position);
                showFloorSnackbar(position); // Update the Snackbar message
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
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Stop WiFi scan when activity is no longer visible
        if (navigationWifiManager != null) {
            navigationWifiManager.stopScan();
        }
    }

    private void graphChangedListeners(PathOverlayImageView imageView) {
        navigateViewModel.getSelectedFloor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(navigateViewModel.getGraphs().get(integer).getValue()!=null) {
                    navigateViewModel.getGraphs().get(integer).getValue().forEach(node-> Log.d("GraphOverlay", "node: " + node.getId() + " name: " + node.getName()));
                    Graph graph = new Graph(navigateViewModel.getGraphs().get(integer).getValue());
                    mMapTappingHandler.setGraph(graph);
                }else {
                    Log.d("DevTool" , "graph: " + integer + " is null");
                }

            }
        });
        navigateViewModel.getGraphs().get(0).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(navigateViewModel.getSelectedFloor().getValue() == 0) {
                    Graph graph = new Graph(navigateViewModel.getGraphs().get(0).getValue());
                    mMapTappingHandler.setGraph(graph);
                }

            }
        });

        navigateViewModel.getGraphs().get(1).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(navigateViewModel.getSelectedFloor().getValue() == 1) {
                    Graph graph = new Graph(navigateViewModel.getGraphs().get(1).getValue());
                    mMapTappingHandler.setGraph(graph);
                }

            }
        });

        navigateViewModel.getGraphs().get(2).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(navigateViewModel.getSelectedFloor().getValue() == 2) {
                    Graph graph = new Graph(navigateViewModel.getGraphs().get(2).getValue());
                    mMapTappingHandler.setGraph(graph);
                }

            }
        });

        navigateViewModel.getGraphs().get(3).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(navigateViewModel.getSelectedFloor().getValue() == 3) {
                    Graph graph = new Graph(navigateViewModel.getGraphs().get(3).getValue());
                    mMapTappingHandler.setGraph(graph);
                }

            }
        });
    }
    private void showFloorSnackbar(int currentFloor) {
        if (route == null || route.isEmpty()) return;
        GraphNode startNode = route.get(0);
        GraphNode endNode = route.get(route.size() - 1);

        int startFloor = startNode.getFloor();
        int endFloor = endNode.getFloor();

        int floorsToGo = endFloor - currentFloor;

        String message;
        if (floorsToGo > 0) {
            message = "Go up " + floorsToGo + " floors.";
        } else if (floorsToGo < 0) {
            message = "Go down " + Math.abs(floorsToGo) + " floors.";
        } else {
            message = "";
        }

        if (floorSnackbar != null && floorSnackbar.isShown()) {
            floorSnackbar.setText(message);
        } else {
            floorSnackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
            View view = floorSnackbar.getView();
            FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            floorSnackbar.show();
        }

        // Dismiss the Snackbar when the user reaches the destination floor
        if (currentFloor == endFloor) {
            floorSnackbar.dismiss();
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
        navigationWifiManager = new NavigationWifiManager(this, navigateViewModel);
    }

    public int getFloorResource(Integer floor) {
        switch (floor) {
            case 0:
                return R.drawable.floor_0;
            case 1:
                return R.drawable.floor_1;
            case 2:
                return R.drawable.floor_2;
            default:
                return R.drawable.floor_3;
        }
    }

    private void setFloor(int i, CharSequence title) {
        if (title != null) {
            navigateViewModel.setTitle(title.toString());
        }
        navigateViewModel.setSelectedFloor(i);
        navigateViewModel.setFloorImage(getFloorResource(i));
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (route != null && !route.isEmpty()) {
            mImageView.setInitialized(false);
            mImageView.setLocation(route.get(0));
        }
    }
}
