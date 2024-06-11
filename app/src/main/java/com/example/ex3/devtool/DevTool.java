package com.example.ex3.devtool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.ex3.R;
import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.NodeStatus;
import com.example.ex3.devtool.managers.CustomAccelerometerManager;
import com.example.ex3.devtool.managers.CustomBluetoothManager;
import com.example.ex3.devtool.managers.CustomMagneticFieldManager;
import com.example.ex3.devtool.managers.CustomWifiManager;
import com.example.ex3.devtool.handlers.MapScalingHandler;
import com.example.ex3.devtool.handlers.MapTappingHandler;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.viewmodels.DevToolViewModel;
import com.example.ex3.devtool.viewmodels.DevToolViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.Objects;

public class DevTool extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int SCAN_INTERVAL = 4000; // 15 seconds
    private static final String mapActivity = "MapActivity";
    private DevToolViewModel devToolViewModel;
    private MaterialToolbar mToolBar;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private CustomWifiManager customWifiManager;
    private Button mSaveButton;
    private Button mDeleteButton;
    private GraphOverlayImageView mImageView;
    private CustomBluetoothManager customBluetoothManager;
    private CustomMagneticFieldManager customMagneticFieldManager;
    private CustomAccelerometerManager customAccelerometerManager;

    private MapTappingHandler mMapTappingHandler;



    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_map);
        mToolBar = findViewById(R.id.devtool_toolbar);
        setSupportActionBar(mToolBar);
        devToolViewModel = new ViewModelProvider(this, new DevToolViewModelFactory(GraphDatabase.getDatabase(this))).get(DevToolViewModel.class);
         mImageView = findViewById(R.id.devtool_map);
        mMapTappingHandler = new MapTappingHandler(mImageView,devToolViewModel);
        mImageView.setImage(ImageSource.resource(R.drawable.floor_1));
        Log.d(mapActivity, "start this app");
        customMagneticFieldManager = new CustomMagneticFieldManager(this,devToolViewModel);
        customAccelerometerManager = new CustomAccelerometerManager(this);

        initiateBottomSheet();

        devToolViewModel.getTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mToolBar.setTitle(s);
            }
        });

        devToolViewModel.getSelectedNode().observe(this, new Observer<GraphNode>() {
            @Override
            public void onChanged(GraphNode graphNode) {
                if (graphNode != null) {
                    TextView tvNodeName = findViewById(R.id.tvNodeName);
                    tvNodeName.setText(graphNode.getName());
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    Log.d("DEVTOOL", "trying to collaps bottom sheet");
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                mImageView.invalidate();
            }
        });

        devToolViewModel.getFloorImage().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
              //  mMapTappingHandler = new  MapTappingHandler(imageView, devToolViewModel);

                mImageView.setImage(ImageSource.resource(integer));
                mImageView.setOnImageEventListener(new MapScalingHandler(mImageView));
                mImageView.setOnTouchListener(mMapTappingHandler);

            }
        });


        graphChangedListeners(mImageView);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            initializeWifiManager();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            initializeBluetoothManager();
        }

        devToolViewModel.getIsScanLocked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLocked) {
                Log.d("DEV_TOOL", "isLocked: "  + isLocked);
                mSaveButton.setEnabled(!isLocked);
                mDeleteButton.setEnabled(!isLocked);
                if(isLocked) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        // Register broadcast receiver to get scan results
        setFloor(2, "Floor 3");
    }

    private void graphChangedListeners(GraphOverlayImageView imageView) {
        devToolViewModel.getSelectedFloor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(devToolViewModel.getGraphs().get(integer).getValue()!=null) {
                    devToolViewModel.getGraphs().get(integer).getValue().forEach(node-> Log.d("GraphOverlay", "node: " + node.getId() + " name: " + node.getName()));
                    Graph graph = new Graph(devToolViewModel.getGraphs().get(integer).getValue());
                    mMapTappingHandler.setGraph(graph);
                    imageView.setGraph(graph);
                }else {
                    Log.d("DevTool" , "graph: " + integer + " is null");
                }

            }
        });
        devToolViewModel.getGraphs().get(0).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(devToolViewModel.getSelectedFloor().getValue() == 0) {
                    Graph graph = new Graph(devToolViewModel.getGraphs().get(0).getValue());
                    mMapTappingHandler.setGraph(graph);
                    imageView.setGraph(graph);
                }

            }
        });

        devToolViewModel.getGraphs().get(1).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(devToolViewModel.getSelectedFloor().getValue() == 1) {
                    Graph graph = new Graph(devToolViewModel.getGraphs().get(1).getValue());
                    mMapTappingHandler.setGraph(graph);
                    imageView.setGraph(graph);
                }

            }
        });

        devToolViewModel.getGraphs().get(2).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(devToolViewModel.getSelectedFloor().getValue() == 2) {
                    Graph graph = new Graph(devToolViewModel.getGraphs().get(2).getValue());
                    mMapTappingHandler.setGraph(graph);
                    imageView.setGraph(graph);
                }

            }
        });

        devToolViewModel.getGraphs().get(3).observe(this, new Observer<List<GraphNode>>() {
            @Override
            public void onChanged(List<GraphNode> graphNodes) {
                graphNodes.forEach(node->Log.d("Test", " node: " +node.getName()));
                if(devToolViewModel.getSelectedFloor().getValue() == 3) {
                    Graph graph = new Graph(devToolViewModel.getGraphs().get(3).getValue());
                    mMapTappingHandler.setGraph(graph);
                    imageView.setGraph(graph);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeWifiManager();
                initializeBluetoothManager();
            } else {
                // Handle the case where the user denies the permission
            }
        }
    }


    private void initializeWifiManager() {
        customWifiManager = new CustomWifiManager(this, devToolViewModel);
    }

    private void initializeBluetoothManager() {
        customBluetoothManager = new CustomBluetoothManager(this,devToolViewModel);
    }

    private void initiateBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        mProgressBar = bottomSheet.findViewById(R.id.save_area_stats_loading_bar);

        bottomSheet.post(new Runnable() {
            @Override
            public void run() {
                bottomSheet.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        bottomSheet.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View view) {
                mImageView.invalidate();
            }
        });
       mSaveButton = bottomSheet.findViewById(R.id.button_save);
        mDeleteButton = bottomSheet.findViewById(R.id.button_delete);
        mSaveButton.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            initiateFullScan();
        });

        mDeleteButton.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            devToolViewModel.deleteSelectedNodeData(mImageView);

        });
    }

    private void initiateFullScan() {
        devToolViewModel.setSavedNode(devToolViewModel.getSelectedNode().getValue());
        devToolViewModel.setIsLocked(true);
        devToolViewModel.setAllScanLocked(true);
         new Thread(() -> {
             devToolViewModel.updateSelectedNodeStatus(NodeStatus.selected);
             customBluetoothManager.startScan();
             customWifiManager.startScan();
             customMagneticFieldManager.startInitialScan();
         }).start();

        handler.postDelayed(()->{
            customMagneticFieldManager.stopScan();
            devToolViewModel.setIsScanLocked(false);
        }, SCAN_INTERVAL);

    }

    public int getFloorResource(Integer floor) {
        switch (floor) {
            case 0: return R.drawable.floor_0;
            case 1: return R.drawable.floor_1;
            case 2: return R.drawable.floor_2;
            default:return R.drawable.floor_3;
        }
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

        if (id == R.id.action_toggle_lock) {
            devToolViewModel.setIsLocked(!devToolViewModel.isLocked());
            invalidateMenu();
        } else if (id == R.id.floor_0) {
            setFloor(0, item.getTitle());
        } else if (id == R.id.floor_1) {
            setFloor(1, item.getTitle());
        } else if (id == R.id.floor_2) {
            setFloor(2, item.getTitle());
        } else if (id == R.id.floor_3) {
            setFloor(3, item.getTitle());
        }

        return super.onOptionsItemSelected(item);
    }

    private void setFloor(int i, CharSequence title) {
        if (title != null) {
            devToolViewModel.setTitle(title.toString());
        }
        devToolViewModel.setSelectedFloor(i);
        devToolViewModel.setFloorImage(getFloorResource(i));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem lockItem = menu.findItem(R.id.action_toggle_lock);
        if (devToolViewModel.isLocked()) {
            lockItem.setIcon(R.drawable.baseline_lock_24);
        } else {
            lockItem.setIcon(R.drawable.baseline_lock_open_24);
        }
        return true;
    }
}
