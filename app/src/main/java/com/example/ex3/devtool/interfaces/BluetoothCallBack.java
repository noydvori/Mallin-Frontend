package com.example.ex3.devtool.interfaces;

import android.bluetooth.le.ScanResult;

import java.util.List;

public interface BluetoothCallBack {
    public void onBluetoothCallBack(List<ScanResult> bluetoothScanResults);
}
