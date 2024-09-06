package com.example.ex3.devtool.graph;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "graph_node")
public class GraphNode {

    private NodeStatus status = NodeStatus.none;
    private int floor = 0;
    @NonNull
    @PrimaryKey
    private String id;

    private String name;
    private float multplyer = 1F;

    private float x, y;

    @TypeConverters(StringListConverter.class)
    private List<String> neighbors = new ArrayList<>();

    public GraphNode(String id, String name, float x, float y, int floor) {
        this.id =  id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.floor = floor;
    }



    public float getMultplyer() {
        return this.multplyer;
    }

    public void setMultplyer(float multplyer) {
        this.multplyer = multplyer;
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getFloor() { return  this.floor;}
    public void setFloor(int floor) {this.floor = floor;}



    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public float getXMultpyed() {
        return  this.x * 3.5f;
    }

    public float getYMultpyed() {
        return  this.y * 3.5f;
    }
    public float getXMultipliedForPath() {
        return this.x * 2F;
    }

    public float getYMultipliedForPath() {
        return this.y * 2F;
    }
    @Override
    public String toString() {
        return "node: " + this.id + " name: " + this.name + " x= " + getX() + " y= " + getY();
    }

    public void addNeighbor(String neighborID) {
        this.neighbors.add(neighborID);
    }

    public List<String> getNeighbors() {
        return this.neighbors;
    }

    public void setNeighbors(List<String> neighbors) {
        this.neighbors = neighbors;
    }
}
