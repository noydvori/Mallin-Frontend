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

import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.api.LocationAPI;
import com.example.ex3.interfaces.LocationCallBack;
import com.example.ex3.objects.WifiScanResult;

import java.util.ArrayList;
import java.util.List;

public class NavigationWifiManager {
    private static final String TAG = "ClientWifiManager";
    private WifiManager wifiManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int SCAN_INTERVAL = 1000 * 5; // 5 seconds
    private Context context;
    private LocationCallBack mCallBack;
    private BroadcastReceiver scanResultsReceiver;

    public NavigationWifiManager(Context context, LocationCallBack callBack) {
        this.context = context;
        this.mCallBack = callBack;
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
        ArrayList<WifiScanResult> wifiScanResults = new ArrayList<>();
        for (ScanResult result : scanResults) {
            String SSID = result.SSID;
            String BSSID = result.BSSID;
            int rssi = result.level;
            wifiScanResults.add(new WifiScanResult(SSID, BSSID, rssi));
            Log.d(TAG, "Wi-Fi stats: " + SSID + " BSSID: " + BSSID + " RSSI: " + rssi);
        }

        // Here you can send the scanResults to your server
        sendScanResultsToServer(wifiScanResults);
    }
    private void sendScanResultsToServer(ArrayList<WifiScanResult> scanResults) {
        LocationAPI.getInstance().getLiveLocation("", scanResults).thenAccept(node -> {
            mCallBack.onResponse(node);
        });
    }
}
