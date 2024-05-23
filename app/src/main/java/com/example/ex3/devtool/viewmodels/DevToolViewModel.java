package com.example.ex3.devtool.viewmodels;

import android.bluetooth.le.ScanResult;
import android.hardware.SensorEvent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ex3.R;
import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.devtool.interfaces.BluetoothCallBack;
import com.example.ex3.devtool.interfaces.MagneticFieldCallBack;
import com.example.ex3.devtool.interfaces.WifiCallBack;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.graph.NodeStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DevToolViewModel extends ViewModel implements WifiCallBack, BluetoothCallBack, MagneticFieldCallBack {
    private MutableLiveData<ArrayList<Graph>> mGraphs = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedFloor = new MutableLiveData<>();
    
    private MutableLiveData<Integer> floorImage = new MutableLiveData<>();

    private MutableLiveData<GraphNode> selectedNode = new MutableLiveData<>();

    private MutableLiveData<String> title = new MutableLiveData<>();

    public static final String BLUETOOTH_LOCK =  "BLUETOOTH_LOCK";
    public static final String WIFI_LOCK =  "WIFI_LOCK";

    private HashMap<String, Boolean> scanLocks = new HashMap<>();

    private MutableLiveData<Boolean> isScanLocked = new MutableLiveData<>();


    private boolean isLocked = true;
    public DevToolViewModel(GrapthDataAdapter dataAdapter) {
        selectedFloor.setValue(3);
//        scanLocks.put(BLUETOOTH_LOCK,false);
//        scanLocks.put(WIFI_LOCK, false);
        isScanLocked.setValue(false);
        title.setValue("Floor 0");
        Graph floor_0 = dataAdapter.loadGrapthData(R.raw.graph_data);
        ArrayList<Graph> graphs = new ArrayList<>();
        graphs.add(floor_0);
        graphs.add(floor_0);
        graphs.add(floor_0);
        graphs.add(floor_0);
        mGraphs.setValue(graphs);
    }

    public MutableLiveData<Integer> getSelectedFloor() {return this.selectedFloor;}

    public void setSelectedFloor(Integer selectedFloor) {
        this.selectedFloor.setValue(selectedFloor);
    }

    public MutableLiveData<ArrayList<Graph>> getGraphs() {
        return mGraphs;
    }

    public void setTitle( String title) {
        this.title.setValue(title);
    }

    public MutableLiveData<String> getTitle () {
        return this.title;
    }

    public MutableLiveData<Integer> getFloorImage() {
        return floorImage;
    }


    public void setFloorImage(Integer floorImage) {
        this.floorImage.setValue(floorImage);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setOnClicked(float imageX, float imageY) {

    }

    public void setSelectedNode(GraphNode node) {
        GraphNode selectedNode = this.selectedNode.getValue();
        if(selectedNode == null){
            this.selectedNode.setValue(node);
            return;
        }
        if(node == null) {
            selectedNode.setStatus(NodeStatus.none);
            this.selectedNode.setValue(null);
            return;
        }
        if(!selectedNode.getId().equals(node.getId())){
            selectedNode.setStatus(NodeStatus.none);
            node.setStatus(NodeStatus.selected);
            this.selectedNode.setValue(node);
        }

    }

    public void  setScanLock(String lock,Boolean isLocked) {
        this.scanLocks.put(lock,isLocked);
        boolean isAllFree = isScanLockFree();
        if(isAllFree) {
            this.isScanLocked.setValue(false);
        }
    }

    public boolean isScanLockFree() {
        for(boolean isLocked: scanLocks.values()){
            if(isLocked) {
                return false;
            }
        }
        return true;
    }


    public MutableLiveData<Boolean> getIsScanLocked() {
        return this.isScanLocked;
    }

    public void setIsScanLocked(Boolean b) {
        this.isScanLocked.setValue(b);
    }

    public MutableLiveData<GraphNode> getSelectedNode () {
        return this.selectedNode;
    }

    public void setAllScanLocked(boolean b) {
        scanLocks.keySet().forEach( key -> scanLocks.put(key,b));
        isScanLocked.setValue(b);
    }

    @Override
    public void onBluetoothCallBack(List<ScanResult> bluetoothScanResults) {
        Log.d("DEVTOOL_VIEW_MODEL", "freed lock: " + BLUETOOTH_LOCK);

     //   setScanLock(BLUETOOTH_LOCK,false);
    }

    @Override
    public void onWifiCallBack(List<android.net.wifi.ScanResult> wifiScanResults) {
        Log.d("DEVTOOL_VIEW_MODEL", "freed lock: " + WIFI_LOCK);
   //     setScanLock(WIFI_LOCK,false);

    }

    @Override
    public void onMagneticFieldCallBack(SensorEvent event) {
       float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        Log.d("DEVTOOL_VIEW_MODEL","Magnetic field area: x="+x + " y="+y + " z=" + z );

    }
}
