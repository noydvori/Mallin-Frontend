package com.example.ex3.objects;


public class WifiScanResult {
    private String SSID;
    private String BSSID;
    private int rssi;

    // Constructor
    public WifiScanResult(String SSID, String BSSID, int rssi) {
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.rssi = rssi;
    }

    // Getter for userID

    // Getter for SSID
    public String getSSID() {
        return SSID;
    }

    // Setter for SSID
    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    // Getter for BSSID
    public String getBSSID() {
        return BSSID;
    }

    // Setter for BSSID
    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    // Getter for rssi
    public int getRssi() {
        return rssi;
    }

    // Setter for rssi
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
