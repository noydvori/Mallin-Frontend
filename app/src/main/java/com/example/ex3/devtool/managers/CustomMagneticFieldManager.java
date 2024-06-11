package com.example.ex3.devtool.managers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.ex3.devtool.interfaces.MagneticFieldCallBack;

public class CustomMagneticFieldManager implements SensorEventListener {
    private static final String TAG = "CustomMagneticFieldManager";
    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private boolean isScanning;

    private Context context;
    private MagneticFieldCallBack mCallBack;

    public CustomMagneticFieldManager(Context context, MagneticFieldCallBack callback) {
        this.mCallBack = callback;
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticSensor == null) {
            Log.e(TAG, "Magnetic Sensor not available");
        }
        isScanning = false;
    }

    public void startInitialScan() {
        startScan();
    }

    private void startScan() {
        if (!isScanning) {
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isScanning = true;
        }
    }

    public void stopScan() {
        if (isScanning) {
            sensorManager.unregisterListener(this);
            isScanning = false;
        }
    }

    public void saveScanResults() {
        stopScan();
        // Your logic to save scan results
        Log.d(TAG, "Scan results saved");
        // Restart scanning if needed
        startScan();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

            mCallBack.onMagneticFieldCallBack(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes if needed
    }
}
