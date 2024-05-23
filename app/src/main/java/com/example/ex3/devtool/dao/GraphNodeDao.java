package com.example.ex3.devtool.dao;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.ex3.devtool.graph.GraphNode;

import java.util.List;

@Dao
public interface GraphNodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(GraphNode... graphNodes);

    @Query("SELECT * FROM graph_node")
    List<GraphNode> getAllNodes();

    @Query("SELECT COUNT(*) FROM graph_node")
    int getCount();
}
