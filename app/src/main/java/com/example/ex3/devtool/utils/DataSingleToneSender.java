package com.example.ex3.devtool.viewmodels;

import android.util.Log;

import com.example.ex3.api.WifiAPI;
import com.example.ex3.devtool.enteties.Wifi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DataSingleToneSender {
    private static DataSingleToneSender instance;
    private List<Wifi> wifiList;
    private int currentIndex = 0;
    private final int batchSize = 100; // Define your batch size here
    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust the pool size as needed
    private final Logger logger = Logger.getLogger(DataSingleToneSender.class.getName());

    private DataSingleToneSender() {
        wifiList = new ArrayList<>();
    }

    public static synchronized DataSingleToneSender getInstance() {
        if (instance == null) {
            instance = new DataSingleToneSender();
        }
        return instance;
    }

    public synchronized void addWifiData(List<Wifi> data) {
        wifiList.addAll(data);
    }

    public synchronized List<Wifi> getNextBatch() {
        if (currentIndex >= wifiList.size()) {
            return new ArrayList<>(); // No more data
        }

        int endIndex = Math.min(currentIndex + batchSize, wifiList.size());
        Log.d("SingleToneSender", "bach: "+ endIndex + " out of " + wifiList.size()  );


        List<Wifi> batch = wifiList.subList(currentIndex, endIndex);
        currentIndex = endIndex;
        return new ArrayList<>(batch); // Return a copy to avoid ConcurrentModificationException
    }

    public void processBatches() {
        while (true) {
            List<Wifi> batch = getNextBatch();
            if (batch.isEmpty()) {
                break; // No more data to process
            }
            executorService.submit(() -> {
                boolean success;
                do {
                    success = sendBatch(batch);
                } while (!success);
            });
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private boolean sendBatch(List<Wifi> batch) {
        try {
            CompletableFuture<String> future = WifiAPI.getInstance().postWifiList(batch);
            String result = future.get(); // Wait for the response
            if ("ok".equals(result)) {
                return true; // Success
            } else if (result.contains("E11000 duplicate key error")) {
                // Log and skip to the next batch
                logger.warning("Duplicate key error detected, skipping batch: " + result);
                return true; // Consider as success to move to the next batch
            } else {
                // Log and retry
                logger.warning("Retrying batch due to failure: " + result);
                return false;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("E11000 duplicate key error")) {
                // Log and skip to the next batch
                logger.warning("Duplicate key error detected, skipping batch: " + e.getMessage());
                return true; // Consider as success to move to the next batch
            } else {
                // Log and retry
                logger.warning("Retrying batch due to exception: " + e.getMessage());
                return false;
            }
        }
    }
}
