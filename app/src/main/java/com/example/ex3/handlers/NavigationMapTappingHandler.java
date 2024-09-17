package com.example.ex3.handlers;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.ex3.components.PathOverlayImageView;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.viewmodels.DevToolViewModel;
import com.example.ex3.viewModels.NavigateViewModel;

import java.util.function.BiFunction;

public class NavigationMapTappingHandler implements View.OnTouchListener {
    private PathOverlayImageView mImageView;
    private NavigateViewModel viewModel; // Define a threshold for what constitutes a tap
    public NavigationMapTappingHandler(PathOverlayImageView imageView, NavigateViewModel viewModel) {
        this.mImageView = imageView;
        this.viewModel=viewModel;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
        }
        return false;
    }
}

