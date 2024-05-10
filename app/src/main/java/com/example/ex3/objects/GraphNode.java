package com.example.ex3.objects;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {
    private String id;
    private String name;
   private float multplyer = 2.6F;
   private float x, y;
   private List<GraphEdge> edges = new ArrayList<>();

    public GraphNode(String id,String name, float x, float y) {
        this.id = id;
        this.name=name;
        this.x = x;
        this.y = y;
    }
    public float getX() {
        return x * multplyer;
    }
    public float getY() {
        return y * multplyer;
    }
    public String getId() { return  this.id; }
    public String getName() { return this.name; }
    public List<GraphEdge> getEdges() {return this.edges;}
}
