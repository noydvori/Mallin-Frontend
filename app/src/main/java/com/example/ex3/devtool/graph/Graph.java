package com.example.ex3.devtool.graph;

import android.graphics.PointF;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

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

    public GraphNode getClosestNode(float imageX, float imageY, BiFunction<Float, Float, PointF> viewToSourceCoordFunction) {
        GraphNode closestNode = null;
        if (nodes.values().isEmpty()) {
            return null;
        }

        float minDistance = Float.MAX_VALUE;  // Use Float.MAX_VALUE for consistency
        for (GraphNode node : nodes.values()) {
            PointF nodePoint = viewToSourceCoordFunction.apply(node.getX(), node.getY());
            float distance = (float) Math.hypot(imageX - nodePoint.x, imageY - nodePoint.y);

            // Check if this node is the closest found so far
            if (distance < minDistance) {
                minDistance = distance;
                closestNode = node;
            }
        }

        // Check if the closest node is within the acceptable threshold
        if (minDistance < 50) {
            Log.d("CLOSEST_NODE", "Node: " + closestNode);
            return closestNode;
        } else {
            return null;
        }
    }

}