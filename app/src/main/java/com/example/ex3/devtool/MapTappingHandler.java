package com.example.ex3.devtool;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.ex3.components.GraphOverlayImageView;
import com.example.ex3.objects.graph.Graph;
import com.example.ex3.objects.graph.GraphNode;
import com.example.ex3.viewModels.DevToolViewModel;

import java.util.function.BiFunction;

public class MapTappingHandler implements View.OnTouchListener {
    private final static String TAG = "MapTappingHandler";
    private GraphOverlayImageView mImageView;
    private float startX, startY;
    private DevToolViewModel viewModel;
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
                        if(viewModel.getGraphs().getValue()!=null&&viewModel.getSelectedFloor().getValue()!=null){
                           Graph graph = viewModel.getGraphs().getValue().get(viewModel.getSelectedFloor().getValue());
                            BiFunction<Float, Float, PointF> viewToSourceCoordFunction = (Float x, Float y) -> {
                             //   Log.d(TAG, "location: " + mImageView.viewToSourceCoord(x, y).toString());
                                return new PointF( x,  y);
                            };
                           GraphNode node =  graph.getClosestNode(imageX, imageY, viewToSourceCoordFunction);
                               viewModel.setSelectedNode(node);
                              // mImageView.invalidate();

                        }
                    }
                }
                break;
        }
        return false;
    }

}
