package com.example.ex3.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Graph {
    private Map<String, GraphNode> nodes = new HashMap<>();
    public void addNode(GraphNode node) {
        nodes.put(node.getId(), node);
    }
    public void addEdge(String fromId, String toId) {
        GraphNode fromNode = nodes.get(fromId);
        GraphNode toNode = nodes.get(toId);
        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Both nodes must exist in the graph");
        }
        GraphEdge edge = new GraphEdge(fromNode, toNode);
        fromNode.getEdges().add(edge);
    }

    public Collection<GraphNode> getNodes ( ) {
       return nodes.values();
    }

    public GraphNode getNode(String id) {
        return nodes.get(id);
    }
}