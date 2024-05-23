package com.example.ex3.devtool.managers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class CustomAccelerometerManager implements SensorEventListener {
    private static final String TAG = "CustomAccelerometerManager";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Context context;
    private float[] lastAcceleration = new float[3];
    private long lastUpdateTime = 0;
    private float distance = 0;

    public CustomAccelerometerManager(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            Log.e(TAG, "Accelerometer not available");
        }
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this, accelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            if (lastUpdateTime != 0) {
                long timeDiff = currentTime - lastUpdateTime;
                float dt = timeDiff / 1000.0f; // Convert time difference to seconds

                // Calculate velocity change
                float[] velocityChange = new float[3];
                for (int i = 0; i < 3; i++) {
                    velocityChange[i] = (event.values[i] + lastAcceleration[i]) / 2 * dt;
                }

                // Calculate distance change
                float distanceChange = 0;
                for (int i = 0; i < 3; i++) {
                    distanceChange += velocityChange[i] * dt;
                }

                distance += distanceChange;
                Log.d(TAG, "Distance: " + distance + " meters");
            }

            lastUpdateTime = currentTime;
            System.arraycopy(event.values, 0, lastAcceleration, 0, event.values.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes if needed
    }

    public float getDistance() {
        return distance;
    }
}
