package com.example.ex3.devtool.viewmodels;

import android.bluetooth.le.ScanResult;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ex3.api.WifiAPI;
import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.enteties.MagneticField;
import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.interfaces.BluetoothCallBack;
import com.example.ex3.devtool.interfaces.MagneticFieldCallBack;
import com.example.ex3.devtool.interfaces.WifiCallBack;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.graph.NodeStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DevToolViewModel extends ViewModel implements WifiCallBack, BluetoothCallBack, MagneticFieldCallBack {
    private  MutableLiveData<Boolean> syncData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Graph>> mGraphs = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedFloor = new MutableLiveData<>();

    private MutableLiveData<Integer> floorImage = new MutableLiveData<>();

    private MutableLiveData<GraphNode> selectedNode = new MutableLiveData<>();
    private String mName;

    private MutableLiveData<Boolean> resultsArrived = new MutableLiveData<>();

    private MutableLiveData<String> title = new MutableLiveData<>();

    public static final String BLUETOOTH_LOCK =  "BLUETOOTH_LOCK";
    public static final String WIFI_LOCK =  "WIFI_LOCK";

    private HashMap<String, Boolean> scanLocks = new HashMap<>();

    private MutableLiveData<Boolean> isScanLocked = new MutableLiveData<>();

    private ArrayList<LiveData<List<GraphNode>>> floors = new ArrayList<>();
    private boolean isLocked = true;
    private GraphDatabase mDataBase;
    private  WifiAPI mWifiApi;
    private GraphNode savedNode;
    public DevToolViewModel(GraphDatabase database, String name) {
        mDataBase = database;
        selectedFloor.setValue(3);
        LiveData<List<GraphNode>> graphNodes0 = database.graphNodeDao().getNodesByFloor(0);
        LiveData<List<GraphNode>> graphNodes1 = database.graphNodeDao().getNodesByFloor(1);
        LiveData<List<GraphNode>> graphNodes2 = database.graphNodeDao().getNodesByFloor(2);
        LiveData<List<GraphNode>> graphNodes3 = database.graphNodeDao().getNodesByFloor(3);
        resultsArrived.setValue(false);
        mWifiApi = WifiAPI.getInstance();
        floors.add(graphNodes0);
        floors.add(graphNodes1);
        floors.add(graphNodes2);
        floors.add(graphNodes3);
        isScanLocked.setValue(false);
        title.setValue("Floor 0");
        mName = name;

    }

    public MutableLiveData<Integer> getSelectedFloor() {return this.selectedFloor;}

    public void setSelectedFloor(Integer selectedFloor) {
        this.selectedFloor.setValue(selectedFloor);
    }

    public ArrayList<LiveData<List<GraphNode>>> getGraphs() {
        return floors;
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
       ///     selectedNode.setStatus(NodeStatus.none);
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
        ArrayList<Wifi> wifiList = new ArrayList<>();
        resultsArrived.setValue(true);
        wifiScanResults.forEach(result -> wifiList.add(new Wifi("lioz",result.SSID, result.BSSID, result.level,savedNode.getId())));
        new Thread(() -> {
            mDataBase.wifiDao().insertAll(wifiList);
        }).start();
    }

    @Override
    public void onMagneticFieldCallBack(SensorEvent event) {
       float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
         Log.d("DEVTOOL_VIEW_MODEL","Magnetic field area: x="+x + " y="+y + " z=" + z );
         new Thread(()->{
             mDataBase.magneticFieldDao().insertAll(new MagneticField(x,y,z,savedNode.getId(), "galaxy_a71"));
         }).start();
    }

    public GraphNode getSavedNode() {
        return savedNode;
    }

    public void setSavedNode(GraphNode savedNode) {
        this.savedNode = savedNode;
    }

    public void updateSelectedNodeStatus(NodeStatus nodeStatus) {
        new Thread(()->{
            Objects.requireNonNull(selectedNode.getValue()).setStatus(nodeStatus);
            mDataBase.graphNodeDao().update(selectedNode.getValue());
        }).start();
    }

    public void deleteSelectedNodeData(GraphOverlayImageView mImageView) {
        new Thread(()->{
            Log.d("DevToolViewModel","deleting by Id: " + selectedNode.getValue().getId());
            mDataBase.wifiDao().deleteByNodeId(Objects.requireNonNull(selectedNode.getValue()).getId());
            mDataBase.magneticFieldDao().deleteByNodeId(Objects.requireNonNull(selectedNode.getValue()).getId());
            selectedNode.getValue().setStatus(NodeStatus.none);
            mDataBase.graphNodeDao().update(selectedNode.getValue());
            mImageView.invalidate();
        }).start();

    }

    public void uploadData() {
        new Thread(() -> {
            syncData.setValue(true);
            List<Wifi> allWifiData = mDataBase.wifiDao().getAll();
            DataSingleToneSender.getInstance().addWifiData(allWifiData);
            DataSingleToneSender.getInstance().processBatches();
            syncData.setValue(false);

        }).start();
    }


    public void sendWifiBatches(List<List<Wifi>> wifiBatches) {
        Handler handler = new Handler(Looper.getMainLooper());
        int delay = 2000; // 2 seconds delay between batches

        for (int i = 0; i < wifiBatches.size(); i++) {
            List<Wifi> batch = wifiBatches.get(i);
            int test = i;
            handler.postDelayed(() -> {
                mWifiApi.postWifiList(batch);
                Log.d("devtool_viewmodel", "send wifi bathc: " + test);
            }, i * delay);
        }
    }
    //779
    public List<List<Wifi>> splitWifiList(List<Wifi> wifiList, int batchSize) {
        List<List<Wifi>> batches = new ArrayList<>();
        int totalSize = wifiList.size();
        Log.d("DEVTOOL" , "total size: "  + totalSize);
//        for (int i = 0; i < totalSize; i += batchSize) {
//            int end = Math.min(totalSize, i + batchSize);
//            batches.add(wifiList.subList(i, end));
//        }

        return batches;
    }

    public MutableLiveData<Boolean> getResultsArrived() {
        return this.resultsArrived;
    }

    public MutableLiveData<Boolean> getSyncData() {
        return syncData;
    }

    public void setSyncData(MutableLiveData<Boolean> syncData) {
        this.syncData = syncData;
    }
}
