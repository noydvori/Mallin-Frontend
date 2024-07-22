package com.example.ex3.objects;

import com.example.ex3.devtool.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;

public class WifiResultsAndPath {
    private List<GraphNode> nodes;
    private ArrayList<WifiScanResult> scanResults;
    public WifiResultsAndPath(List<GraphNode> nodes, ArrayList<WifiScanResult> scanResults) {
        this.scanResults = scanResults;
        this.nodes = nodes;
    }

    public void setNodes(List<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public ArrayList<WifiScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(ArrayList<WifiScanResult> scanResults) {
        this.scanResults = scanResults;
    }
}
