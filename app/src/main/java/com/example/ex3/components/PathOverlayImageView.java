package com.example.ex3.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.ex3.R;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;

import java.util.List;

public class PathOverlayImageView extends SubsamplingScaleImageView {
    private Graph graph;
    private List<GraphNode> pathStores;
    private GraphNode location;
    private GraphNode middle = new GraphNode(null, "middle", 877, 1911, 0);
    private int currentFloor;
    private boolean isInitialized = false;

    public PathOverlayImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public PathOverlayImageView(Context context) {
        super(context);
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
        invalidate(); // Redraw the view when the floor changes
    }

    public int getCurrentFloor() {
        return currentFloor;
    }


    public void setPath(List<GraphNode> pathStores) {
        this.pathStores = pathStores;
        invalidate(); // Redraw the view when the path changes
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public void setLocation(GraphNode location) {
        this.location = location;
        invalidate(); // Redraw the view when the location changes
    }

    public void centerOnLocation() {
        if (location != null) {
            PointF locationCenter = new PointF(location.getXMultpyed(), location.getYMultpyed());
            setScaleAndCenter(getScale(), locationCenter);
        }
    }

    private void centerOnTheMiddle() {
        PointF locationCenter = new PointF(middle.getXMultpyed(), middle.getYMultpyed());
        setScaleAndCenter(getScale(), locationCenter);
    }

    private void drawFinishIcon(Canvas canvas, PointF point, float scale) {
        Drawable finishIcon = getResources().getDrawable(R.drawable.finish_icon, null);
        if (finishIcon != null) {
            // Adjust the icon size by changing the scaling factor
            int iconSize = (int) (200 * scale); // Increase the icon size
            int left = (int) (point.x - iconSize / 2);
            int top = (int) (point.y - iconSize);
            int right = (int) (point.x + iconSize / 2);
            int bottom = (int) (point.y );

            finishIcon.setBounds(left, top, right, bottom);
            finishIcon.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float scale = getScale();
        Paint paint = new Paint();
        paint.setStrokeWidth(30 * scale); // Scale the line width with the image

        // Draw the path
        if (pathStores != null && !pathStores.isEmpty()) {
            for (int i = 0; i < pathStores.size() - 1; i++) {
                GraphNode node = pathStores.get(i);
                GraphNode nextNode = pathStores.get(i + 1);
                PointF center = sourceToViewCoord(node.getXMultpyed(), node.getYMultpyed());
                PointF nextCenter = sourceToViewCoord(nextNode.getXMultpyed(), nextNode.getYMultpyed());

                if (center != null && nextCenter != null) {
                    if (node.getFloor() == this.currentFloor) {
                        paint.setColor(Color.BLUE);
                        canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);
                    }
                }
            }

            // Draw finish icon on the last node
            GraphNode lastNode = pathStores.get(pathStores.size() - 1);
            if (lastNode.getFloor() == this.currentFloor) {
                PointF lastCenter = sourceToViewCoord(lastNode.getXMultpyed(), lastNode.getYMultpyed());
                if (lastCenter != null) {
                    drawFinishIcon(canvas, lastCenter, scale);
                }
            }
        }

        // Draw the current location
        if (location != null) {
            paint.setColor(Color.BLUE);
            PointF locationCenter = sourceToViewCoord(location.getXMultpyed(), location.getYMultpyed());
            if (locationCenter != null && location.getFloor() == this.currentFloor) {
                canvas.drawCircle(locationCenter.x, locationCenter.y, 40 * scale, paint);
            }
        }

        // Center on the middle point only once, when the view is first displayed
        if (!isInitialized) {
            centerOnLocation();
            isInitialized = true;
        }
    }

}
