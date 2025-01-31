package com.example.ex3.devtool.managers;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.ex3.devtool.interfaces.BluetoothCallBack;

import java.util.ArrayList;
import java.util.List;

public class CustomBluetoothManager {
    private static final String TAG = "CustomBluetoothManager";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler = new Handler(Looper.getMainLooper());

    private BluetoothCallBack mOnScanCallBack;
    private static final int SCAN_INTERVAL = 30000; // 30 seconds
    private Context context;

    public CustomBluetoothManager(Context context, BluetoothCallBack callBack) {
        this.context = context;
        this.mOnScanCallBack = callBack;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled or not supported on this device");
        }
    }



    public void startScan() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            return;
        }

        if (bluetoothLeScanner == null) {
            Log.e(TAG, "BluetoothLeScanner is null");
            return;
        }

        // Ensure the previous scan is stopped
        bluetoothLeScanner.stopScan(leScanCallback);

        try {
            bluetoothLeScanner.startScan(leScanCallback);
//            handler.postDelayed(() -> {
//                bluetoothLeScanner.stopScan(leScanCallback);
//                startScan(); // Restart the scan after the interval
//            }, SCAN_INTERVAL);
        } catch (Exception e) {
            Log.e(TAG, "Exception during Bluetooth scan", e);
        }
    }



    private final ScanCallback leScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            int rssi = result.getRssi();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.d(TAG, "Bluetooth Device: " + device.getName() + " - " + device.getAddress() + " RSSI: " + rssi);
            mOnScanCallBack.onBluetoothCallBack(new ArrayList<>());
            // Add logic to use the scan results
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                BluetoothDevice device = result.getDevice();
                int rssi = result.getRssi();
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Log.d(TAG, "Bluetooth Device: " + device.getName() + " - " + device.getAddress() + " RSSI: " + rssi);
                // Add logic to use the scan results
            }
            mOnScanCallBack.onBluetoothCallBack(results);

        }



        @Override
        public void onScanFailed(int errorCode) {
            mOnScanCallBack.onBluetoothCallBack(new ArrayList<>());
            Log.e(TAG, "Bluetooth scan failed with error code: " + errorCode);
        }
    };
}
