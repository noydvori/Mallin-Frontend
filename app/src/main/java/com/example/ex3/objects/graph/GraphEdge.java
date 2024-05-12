package com.example.ex3.objects.graph;

public class GraphEdge {
    private GraphNode source;
    private GraphNode target;
    public GraphEdge(GraphNode source, GraphNode target) {
        this.source = source;
        this.target = target;
    }

    public GraphNode getTarget() { return this.target; }

    public GraphNode getSource() { return this.source; }


}
