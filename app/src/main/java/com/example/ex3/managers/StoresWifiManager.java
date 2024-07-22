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

import com.example.ex3.api.LocationAPI;
import com.example.ex3.interfaces.StoresCallBack;
import com.example.ex3.objects.WifiResultsAndPath;
import com.example.ex3.objects.WifiScanResult;
import com.example.ex3.utils.UserPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class StoresWifiManager {
    private static final String TAG = "StoresWifiManager";
    private WifiManager wifiManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private BroadcastReceiver scanResultsReceiver;
    private StoresCallBack mCallback;
    private boolean isReceiverRegistered = false;

    public StoresWifiManager(Context context, StoresCallBack callBack) {
        this.mCallback = callBack;
        this.context = context;
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Initialize the receiver
        initializeReceiver();

        // Start scanning for WiFi networks
        startScan();
    }

    private void initializeReceiver() {
        scanResultsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Log.d(TAG, "wifiManagerScan");
                List<ScanResult> results = wifiManager.getScanResults();
                handleScanResults(results);
                stopScan(); // Stop the scan after receiving results
            }
        };
    }

    private void startScan() {
        // Check for permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (!isReceiverRegistered) {
            // Register broadcast receiver to get scan results
            context.registerReceiver(scanResultsReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            isReceiverRegistered = true;
        }

        // Start the first scan
        wifiManager.startScan();
    }

    public void stopScan() {
        if (isReceiverRegistered) {
            // Unregister the BroadcastReceiver to stop receiving scan results
            context.unregisterReceiver(scanResultsReceiver);
            isReceiverRegistered = false;
        }
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
        WifiResultsAndPath wifiResultsAndPath = new WifiResultsAndPath(UserPreferencesUtils.getNodes(context), wifiScanResults);

        // Here you can send the scanResults to your server
        sendScanResultsToServer(wifiResultsAndPath);
    }

    private void sendScanResultsToServer(WifiResultsAndPath wifiResultsAndPath) {
        Log.d(TAG, "send wifi results to server");
        LocationAPI.getInstance().getClosestStores("", wifiResultsAndPath).thenAccept(store -> {
            Log.d(TAG, "response from server");
            mCallback.onResponse(store);
        });
    }
}
