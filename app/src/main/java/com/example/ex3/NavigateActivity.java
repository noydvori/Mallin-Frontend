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
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.ex3.components.PathOverlayImageView;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.graph.GraphNode;
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
import java.util.concurrent.atomic.AtomicInteger;

public class NavigateActivity extends AppCompatActivity {
    private NavigateViewModel navigateViewModel;
    private Snackbar floorSnackbar;
    private NavigationWifiManager navigationWifiManager;
    private PathOverlayImageView mImageView;
    private List<GraphNode> route;
    private TabLayout tabLayout;
    private NavigationMapTappingHandler mMapTappingHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        // Initialize ViewModel
        navigateViewModel = new ViewModelProvider(this, new NavigateViewModelFactory(GraphDatabase.getDatabase(this)))
                .get(NavigateViewModel.class);

        // Initialize UI components
        mImageView = findViewById(R.id.devtool_map);
        tabLayout = findViewById(R.id.tab_layout);
        FloatingActionButton centerButton = findViewById(R.id.center);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_menu);

        // Set initial image and handlers for the map
        mImageView.setImage(ImageSource.resource(R.drawable.floor_1));
        mMapTappingHandler = new NavigationMapTappingHandler(mImageView, navigateViewModel);

        // Handle center button click
        centerButton.setOnClickListener(v -> mImageView.centerOnLocation());

        // Retrieve and set the user's preferred route
        route = UserPreferencesUtils.getNodes(this);
        mImageView.setDestinations(UserPreferencesUtils.getStores(this));
        AtomicInteger initialFloor = new AtomicInteger();


        if (route != null && !route.isEmpty()) {
            mImageView.setCurrentFloor(route.get(0).getFloor());

            mImageView.setPath(route);
            mImageView.setLocation(route.get(0));

            initialFloor.set(route.get(0).getFloor());
        }

        // Set the initial floor and show the SnackBar
        setFloor(initialFloor.get(), "Floor " + initialFloor);
        showFloorSnackbar(initialFloor.get());

        // Bottom Navigation Menu setup
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

        // Observe LiveData changes
        navigateViewModel.getFloorImage().observe(this, integer -> {
            mImageView.setImage(ImageSource.resource(integer));
            mImageView.setOnImageEventListener(new MapScalingHandler(mImageView));
            mImageView.setOnTouchListener(mMapTappingHandler);
        });

        navigateViewModel.getCurrentLocation().observe(this, new Observer<GraphNode>() {
            @Override
            public void onChanged(GraphNode node) {
                Log.d("NavigateActivity","liveLocation: " + node);
                mImageView.setLocation(node);
                initialFloor.set(node.getFloor());
            }
        });

        // Check and request permissions for location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            initializeWifiManager();
        }

        // Setup the TabLayout with floor tabs
        setupTabLayout(initialFloor.get());

        // Center map on location after 5 seconds
        new Handler().postDelayed(() -> mImageView.centerOnLocation(), 5000);
    }

    private void setupTabLayout(int initialFloor) {
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
                showFloorSnackbar(position);
                new Handler().postDelayed(() -> mImageView.centerOnLocation(), 1000);
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

    @Override
    protected void onResume() {
        mImageView.setDestinations(UserPreferencesUtils.getStores(this));
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeWifiManager();
            } else {
                // Handle permission denial - do nothing.
            }
        }
    }

    private void initializeWifiManager() {
        navigationWifiManager = new NavigationWifiManager(this, navigateViewModel);
    }

    private void showFloorSnackbar(int currentFloor) {
        if (route == null || route.isEmpty()) return;
        int floorsToGo = 0;
        for (int i = 0; i < route.size(); i++) {
            if(route.get(i).getFloor() != currentFloor) {
                floorsToGo = route.get(i).getFloor() - currentFloor;
                break;
            }
        }
        GraphNode endNode = route.get(route.size() - 1);
        int endFloor = endNode.getFloor();

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
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.TOP;
            view.setLayoutParams(params);
            floorSnackbar.show();
        }

        if (currentFloor == endFloor) {
            floorSnackbar.dismiss();
        }
    }

    private void setFloor(int i, CharSequence title) {
        if (title != null) {
            navigateViewModel.setTitle(title.toString());
        }
        navigateViewModel.setSelectedFloor(i);
        navigateViewModel.setFloorImage(getFloorResource(i));
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
        }}}