package com.example.ex3.viewModels;

import android.bluetooth.le.ScanResult;
import android.hardware.SensorEvent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.components.PathOverlayImageView;
import com.example.ex3.devtool.database.GraphDatabase;
import com.example.ex3.devtool.enteties.MagneticField;
import com.example.ex3.devtool.enteties.Wifi;
import com.example.ex3.devtool.interfaces.BluetoothCallBack;
import com.example.ex3.devtool.interfaces.MagneticFieldCallBack;
import com.example.ex3.devtool.interfaces.WifiCallBack;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.graph.NodeStatus;
import com.example.ex3.interfaces.LocationCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NavigateViewModel extends ViewModel implements WifiCallBack, LocationCallBack,BluetoothCallBack, MagneticFieldCallBack {
    private MutableLiveData<Integer> selectedFloor = new MutableLiveData<>();
    private MutableLiveData<Integer> floorImage = new MutableLiveData<>();
    public static final String BLUETOOTH_LOCK =  "BLUETOOTH_LOCK";
    public static final String WIFI_LOCK =  "WIFI_LOCK";
    private MutableLiveData<GraphNode> currentLocation = new MutableLiveData<>();
    private MutableLiveData<Boolean> isScanLocked = new MutableLiveData<>();
    private GraphDatabase mDataBase;
    private GraphNode savedNode;
    public NavigateViewModel(GraphDatabase database) {
        mDataBase = database;
        isScanLocked.setValue(false);

    }
    public void setSelectedFloor(Integer selectedFloor) {
        this.selectedFloor.setValue(selectedFloor);
    }


    public MutableLiveData<Integer> getFloorImage() {
        return floorImage;
    }


    public void setFloorImage(Integer floorImage) {
        this.floorImage.setValue(floorImage);
    }



    @Override
    public void onBluetoothCallBack(List<ScanResult> bluetoothScanResults) {
        Log.d("DEVTOOL_VIEW_MODEL", "freed lock: " + BLUETOOTH_LOCK);

        //   setScanLock(BLUETOOTH_LOCK,false);
    }

    @Override
    public void onWifiCallBack(List<android.net.wifi.ScanResult> wifiScanResults) {
        ArrayList<Wifi> wifiList = new ArrayList<>();
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
    @Override
    public void onResponse(GraphNode node) {
        this.currentLocation.setValue(node);
    }

    public MutableLiveData<GraphNode> getCurrentLocation(){
        return this.currentLocation;
    }

}
