package com.example.ex3.objects;

import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Store;

import java.util.List;

public class Paths {
    private List<GraphNode> nodes;
    private List<Store> stores;

    public Paths(List<GraphNode> nodes, List<Store> stores) {
        this.nodes = nodes;
        this.stores = stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public List<Store> getStores() {
        return stores;
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<GraphNode> nodes) {
        this.nodes = nodes;
    }
}
