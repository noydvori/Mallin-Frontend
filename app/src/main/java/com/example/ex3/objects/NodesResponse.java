package com.example.ex3.objects;

import java.util.List;

public class NodesResponse {

    private List<Node> nodes;
    private Node currentLocation;

    // Constructor
    public NodesResponse(List<Node> nodes, Node currentLocation) {
        this.nodes = nodes;
        this.currentLocation = currentLocation;
    }

    // Getters and Setters
    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Node getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Node currentLocation) {
        this.currentLocation = currentLocation;
    }
}
