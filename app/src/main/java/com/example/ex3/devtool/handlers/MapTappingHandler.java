package com.example.ex3.devtool.handlers;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.viewmodels.DevToolViewModel;

import java.util.List;
import java.util.function.BiFunction;

public class MapTappingHandler implements View.OnTouchListener {
    private final static String TAG = "MapTappingHandler";
    private GraphOverlayImageView mImageView;
    private float startX, startY;
    private DevToolViewModel viewModel;
    private Graph mGraph;
    private final static float TAP_THRESHOLD = 10; // Define a threshold for what constitutes a tap
    public MapTappingHandler(GraphOverlayImageView imageView, DevToolViewModel viewModel) {
        this.mImageView = imageView;
        this.viewModel=viewModel;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Record the starting position of the touch event
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (Math.abs(endX - startX) <= TAP_THRESHOLD && Math.abs(endY - startY) <= TAP_THRESHOLD) {
                    PointF sCoord = mImageView.viewToSourceCoord(endX, endY);
                    if (sCoord != null) {
                        float imageX = sCoord.x;
                        float imageY = sCoord.y;
                        viewModel.setOnClicked(imageX, imageY);
                        Log.d(TAG, " x=" + imageX + " y=" + imageY);
                        if(mGraph != null){
                           // List<GraphNode> nodes = viewModel.getGraphs().get(viewModel.getSelectedFloor().getValue()).getValue();

                            BiFunction<Float, Float, PointF> viewToSourceCoordFunction = (Float x, Float y) -> {
                             //   Log.d(TAG, "location: " + mImageView.viewToSourceCoord(x, y).toString());
                                return new PointF( x,  y);
                            };
                           GraphNode node =  mGraph.getClosestNode(imageX, imageY, viewToSourceCoordFunction);
                           viewModel.setSelectedNode(node);
                        }
                    }
                }
                break;
        }
        return false;
    }
    public void setGraph(Graph graph) {
        this.mGraph = graph;
    }
    public Graph getGraph() {
        return mGraph;
    }
}
