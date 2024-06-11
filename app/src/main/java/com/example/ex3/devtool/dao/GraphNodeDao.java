package com.example.ex3.devtool.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ex3.devtool.graph.GraphNode;

import java.util.Collection;
import java.util.List;

@Dao
public interface GraphNodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(GraphNode... graphNodes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllNodes(Collection<GraphNode> nodes);

    @Query("SELECT * FROM graph_node")
    LiveData<List<GraphNode>> getAllNodes();

    @Query("SELECT COUNT(*) FROM graph_node")
    LiveData<Integer> getCount();

    @Update
    void update(GraphNode graphNode);
    @Query("SELECT * FROM graph_node WHERE floor = :floor")
    LiveData<List<GraphNode>> getNodesByFloor(int floor);
}
