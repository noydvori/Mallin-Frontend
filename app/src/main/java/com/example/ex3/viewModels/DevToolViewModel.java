package com.example.ex3.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ex3.R;
import com.example.ex3.adapters.GrapthDataAdapter;
import com.example.ex3.objects.graph.Graph;
import com.example.ex3.objects.graph.GraphNode;
import com.example.ex3.objects.graph.NodeStatus;

import java.util.ArrayList;

public class DevToolViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Graph>> mGraphs = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedFloor = new MutableLiveData<>();
    
    private MutableLiveData<Integer> floorImage = new MutableLiveData<>();

    private MutableLiveData<GraphNode> selectedNode = new MutableLiveData<>();

    private MutableLiveData<String> title = new MutableLiveData<>();

    private boolean isLocked = true;
    public DevToolViewModel(GrapthDataAdapter dataAdapter) {
        selectedFloor.setValue(3);
        title.setValue("Floor 0");
        Graph floor_0 = dataAdapter.loadGrapthData(R.raw.graph_data);
        ArrayList<Graph> graphs = new ArrayList<>();
        graphs.add(floor_0);
        graphs.add(floor_0);
        graphs.add(floor_0);
        graphs.add(floor_0);
        mGraphs.setValue(graphs);
    }

    public MutableLiveData<Integer> getSelectedFloor() {return this.selectedFloor;}

    public void setSelectedFloor(Integer selectedFloor) {
        this.selectedFloor.setValue(selectedFloor);
    }

    public MutableLiveData<ArrayList<Graph>> getGraphs() {
        return mGraphs;
    }

    public void setTitle( String title) {
        this.title.setValue(title);
    }

    public MutableLiveData<String> getTitle () {
        return this.title;
    }

    public MutableLiveData<Integer> getFloorImage() {
        return floorImage;
    }


    public void setFloorImage(Integer floorImage) {
        this.floorImage.setValue(floorImage);
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setOnClicked(float imageX, float imageY) {

    }

    public void setSelectedNode(GraphNode node) {
        GraphNode selectedNode = this.selectedNode.getValue();
        if(selectedNode == null){
            this.selectedNode.setValue(node);
            return;
        }
        if(node == null) {
            selectedNode.setStatus(NodeStatus.none);
            this.selectedNode.setValue(null);
            return;
        }
        if(!selectedNode.getId().equals(node.getId())){
            selectedNode.setStatus(NodeStatus.none);
            node.setStatus(NodeStatus.selected);
            this.selectedNode.setValue(node);
        }

    }

    public MutableLiveData<GraphNode> getSelectedNode () {
        return this.selectedNode;
    }
}
