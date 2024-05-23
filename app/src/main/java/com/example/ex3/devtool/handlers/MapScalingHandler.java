package com.example.ex3.devtool.handlers;

import android.graphics.PointF;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class MapScalingHandler implements SubsamplingScaleImageView.OnImageEventListener{
    private SubsamplingScaleImageView imageView;
    public MapScalingHandler(SubsamplingScaleImageView imageView) {
        this.imageView = imageView;
    }
    @Override
    public void onReady() {
        imageView.setZoomEnabled(false);
        imageView.setScaleAndCenter(0.5f, new PointF(imageView.getWidth(), imageView.getHeight()));
    }

    @Override
    public void onImageLoaded() {

    }

    @Override
    public void onPreviewLoadError(Exception e) {

    }

    @Override
    public void onImageLoadError(Exception e) {

    }

    @Override
    public void onTileLoadError(Exception e) {

    }

    @Override
    public void onPreviewReleased() {

    }
}
