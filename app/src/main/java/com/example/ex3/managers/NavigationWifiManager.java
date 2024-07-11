package com.example.ex3.managers;

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

public class NavigationWifiManager {
    private static final String TAG = "ClientWifiManager";
    private WifiManager wifiManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int SCAN_INTERVAL = 1000 * 5; // 5 seconds
    private Context context;
    private BroadcastReceiver scanResultsReceiver;

    public NavigationWifiManager(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Start scanning for WiFi networks
        startScan();
    }

    private void startScan() {
        // Check for permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Register broadcast receiver to get scan results
        scanResultsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                List<ScanResult> results = wifiManager.getScanResults();
                handleScanResults(results);

                // Schedule the next scan
                handler.postDelayed(() -> wifiManager.startScan(), SCAN_INTERVAL);
            }
        };

        context.registerReceiver(scanResultsReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // Start the first scan
        wifiManager.startScan();
    }

    public void stopScan() {
        // Unregister the BroadcastReceiver to stop receiving scan results
        context.unregisterReceiver(scanResultsReceiver);
        // Remove any pending callbacks to stop further scans
        handler.removeCallbacksAndMessages(null);
    }

    private void handleScanResults(List<ScanResult> scanResults) {
        // Process the scan results
        // Here you can send the scanResults to your server or perform any other actions with them
        for (ScanResult result : scanResults) {
            String SSID = result.SSID;
            String BSSID = result.BSSID;
            int rssi = result.level;
            Log.d(TAG, "Wi-Fi stats: " + SSID + " BSSID: " + BSSID + " RSSI: " + rssi);
        }

        // Here you can send the scanResults to your server
        sendScanResultsToServer(scanResults);
    }

    private void sendScanResultsToServer(List<ScanResult> scanResults) {
        // Send the scan results to your server
        // Implement your logic here to send the scanResults to your server
    }
}
