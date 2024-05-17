package com.example.ex3.devtool;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class CustomWifiManager {
    private static final String TAG = "CustomWifiManager";
    private WifiManager wifiManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int SCAN_INTERVAL = 30000; // 30 seconds
    private Context context;

    public CustomWifiManager(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Register broadcast receiver to get scan results
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                List<ScanResult> results = wifiManager.getScanResults();
                for (ScanResult result : results) {
                    // Extract information from result
                    String SSID = result.SSID;
                    String BSSID = result.BSSID;
                    int rssi = result.level;
                    Log.d(TAG, "Wi-Fi stats: " + SSID + " BSSID: " + BSSID + " RSSI: " + rssi);
                    // Add your logic to use the scan results
                }
                // Schedule the next scan
                handler.postDelayed(() -> wifiManager.startScan(), SCAN_INTERVAL);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void startInitialScan() {
        wifiManager.startScan();
    }
}
