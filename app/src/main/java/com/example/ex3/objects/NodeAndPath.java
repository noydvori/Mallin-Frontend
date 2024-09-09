package com.example.ex3.objects;

import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Store;

import java.util.List;

public class NodeAndPath {
    private GraphNode node;
    private List<Store> stores;

    public NodeAndPath(GraphNode node, List<Store> stores) {
        this.node = node;
        this.stores = stores;
    }
}
