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
    }

    public void startInitialScan() {
        startScan();
    }

    private void startScan() {
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mCallBack.onMagneticFieldCallBack(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes if needed
    }
}
