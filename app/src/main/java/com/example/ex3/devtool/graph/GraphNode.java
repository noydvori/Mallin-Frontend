package com.example.ex3.devtool.graph;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "graph_node")
public class GraphNode {

    private NodeStatus status = NodeStatus.none;
    private int level = 0;


    @PrimaryKey
    private String id;

    private String name;
    private float multplyer = 2.6F;
    private float x, y;

    @Ignore
    private List<GraphEdge> edges = new ArrayList<>();

    public GraphNode(String id, String name, float x, float y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x * multplyer;
    }

    public float getY() {
        return y * multplyer;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() { return  this.level;}
    public void setLevel(int level) {this.level = level;}

    @Ignore
    public List<GraphEdge> getEdges() {
        return this.edges;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "node: " + this.id + " name: " + this.name + " x= " + getX() + " y= " + getY();
    }
}
