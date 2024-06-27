package com.example.ex3.objects;

import java.util.List;

public class Node {

    private String id;
    private String label;
    private String x;
    private String y;
    private String w;
    private String h;
    private String type;
    private String raisedBorder;
    private String fill;
    private String outline;
    private List<String> edges;

    // Constructor
    public Node(String id, String label, String x, String y, String w, String h, String type, String raisedBorder, String fill, String outline, List<String> edges) {
        this.id = id;
        this.label = label;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.type = type;
        this.raisedBorder = raisedBorder;
        this.fill = fill;
        this.outline = outline;
        this.edges = edges;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRaisedBorder() {
        return raisedBorder;
    }

    public void setRaisedBorder(String raisedBorder) {
        this.raisedBorder = raisedBorder;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public String getOutline() {
        return outline;
    }

    public void setOutline(String outline) {
        this.outline = outline;
    }

    public List<String> getEdges() {
        return edges;
    }

    public void setEdges(List<String> edges) {
        this.edges = edges;
    }
}

