package com.example.ex3.viewModels;

public class StoreItem {
    private String storeName;
    private String floorNumber;
    private String logoUrl;

    public StoreItem(String storeName, String floorNumber, String logoUrl) {
        this.storeName = storeName;
        this.floorNumber = floorNumber;
        this.logoUrl = logoUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}

