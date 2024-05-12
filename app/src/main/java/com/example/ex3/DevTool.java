package com.example.ex3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.devtool.MapScalingHandler;
import com.example.ex3.devtool.MapTappingHandler;
import com.example.ex3.objects.graph.GraphNode;
import com.example.ex3.viewModels.DevToolViewModel;
import com.example.ex3.viewModels.DevToolViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class DevTool extends AppCompatActivity {
    private static final String mapActivity = "MapActivity";
    private DevToolViewModel devToolViewModel;
    private  MaterialToolbar mToolBar;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_map);
         mToolBar = findViewById(R.id.devtool_toolbar);
        setSupportActionBar(mToolBar);
        devToolViewModel = new ViewModelProvider(this, new DevToolViewModelFactory(new GrapthDataAdapter(this))).get(DevToolViewModel.class);
        GraphOverlayImageView imageView = (GraphOverlayImageView)findViewById(R.id.devtool_map);
        imageView.setImage(ImageSource.resource(R.drawable.floor_1));
        Log.d(mapActivity, "start this app");


        initiateBottomSheet();


        GrapthDataAdapter dataAdapter = new GrapthDataAdapter(this);
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
////                dataAdapter.loadGrapthData();
////                imageView.setGraph(dataAdapter.getGraph());
//                imageView.setOnImageEventListener(new MapScalingHandler(imageView));
//
//            }
//        }
//                );
//        thread.start();
        devToolViewModel.getTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mToolBar.setTitle(s);
            }
        });

        devToolViewModel.getSelectedNode().observe(this, new Observer<GraphNode>() {
            @Override
            public void onChanged(GraphNode graphNode) {
                if(graphNode != null) {
                    TextView tvNodeName = findViewById(R.id.tvNodeName);
                    tvNodeName.setText(graphNode.getName());
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    Log.d("DEVTOOL","trying to collaps bottom sheet");
                  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                imageView.invalidate();

            }
        });
        devToolViewModel.getFloorImage().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                imageView.setImage(ImageSource.resource(integer));
                imageView.setOnImageEventListener(new MapScalingHandler(imageView));
                imageView.setOnTouchListener(new MapTappingHandler(imageView, devToolViewModel));
            }
        });

        devToolViewModel.getSelectedFloor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                imageView.setGraph(devToolViewModel.getGraphs().getValue().get(integer));
            }
        });



        setFloor(2, "Floor 3");
    }

    private void initiateBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);

        bottomSheet.post(new Runnable() {
            @Override
            public void run() {
                bottomSheet.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });

        Button btnSave = bottomSheet.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view -> {
            // Handle the save action
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
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

        }else if(id == R.id.floor_0) {
            setFloor(0,item.getTitle());
        }else if(id == R.id.floor_1) {
            setFloor(1, item.getTitle());

        }else if(id == R.id.floor_2) {
            setFloor(2, item.getTitle());

        }else if(id == R.id.floor_3) {
            setFloor(3, item.getTitle());

        }

        return super.onOptionsItemSelected(item);
    }

    private void setFloor(int i, CharSequence title) {
        if(title != null){
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