package com.example.ex3.interfaces;

import android.net.wifi.ScanResult;

import java.util.List;

public interface WifiCallBack {
    public void onWifiCallBack( List<ScanResult> wifiScanResults);
}
