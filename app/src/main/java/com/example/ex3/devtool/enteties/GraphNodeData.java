// GraphNodeData.java
package com.example.ex3.devtool.enteties;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

import com.example.ex3.devtool.graph.GraphNode;

@Entity(tableName = "graph_node_data",
        foreignKeys = @ForeignKey(entity = GraphNode.class,
                parentColumns = "id",
                childColumns = "nodeId",
                onDelete = ForeignKey.CASCADE))
public class GraphNodeData {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nodeId; // Foreign key

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
}
