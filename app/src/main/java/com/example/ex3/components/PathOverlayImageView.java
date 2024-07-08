package com.example.ex3.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class PathOverlayImageView extends SubsamplingScaleImageView {
    private List<GraphNode> pathStores;
    private GraphNode location;

    public PathOverlayImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public PathOverlayImageView(Context context) {
        super(context);
    }

    public void setPath(List<GraphNode> pathStores) {
        this.pathStores = pathStores;
        invalidate(); // Redraw the view when the graph changes
    }

    public void setLocation(GraphNode location) {
        this.location = location;
        invalidate(); // Redraw the view when the location changes
    }

    private void centerOnLocation() {
        if (location != null) {
            PointF locationCenter = new PointF(location.getXMultpyed(), location.getYMultpyed());
            setScaleAndCenter(getScale(), locationCenter);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (pathStores.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Go to home to find your desired stores", Snackbar.LENGTH_SHORT).show();
            return;
        }

        float scale = getScale();
        Paint paint = new Paint();
        paint.setStrokeWidth(10 * scale); // Scale the line width with the image

        // Draw edges and nodes in pathStores
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);

        for (int i = 0; i < pathStores.size() - 1; i++) { // Changed from pathStores.size() - 2 to pathStores.size() - 1
            GraphNode node = pathStores.get(i);
            PointF center = sourceToViewCoord(node.getXMultpyed(), node.getYMultpyed());
            if (center != null) {
                canvas.drawCircle(center.x, center.y, 30 * scale, paint); // Scale node size with the image
            }
            GraphNode nextNode = pathStores.get(i + 1);
            PointF nextCenter = sourceToViewCoord(nextNode.getXMultpyed(), nextNode.getYMultpyed());
            if (center != null && nextCenter != null) {
                canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);
            }
        }

        // Draw the current location if it is set
        if (location != null) {
            paint.setColor(Color.BLACK); // Change color for the location
            PointF locationCenter = sourceToViewCoord(location.getXMultpyed(), location.getYMultpyed());
            if (locationCenter != null) {
                canvas.drawCircle(locationCenter.x, locationCenter.y, 50 * scale, paint); // Scale node size with the image
            }
        }
    }
}
