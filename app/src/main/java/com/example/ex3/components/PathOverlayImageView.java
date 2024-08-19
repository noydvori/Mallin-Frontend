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
import com.example.ex3.entities.Store;
import com.example.ex3.utils.UserPreferencesUtils;

import java.util.List;

public class PathOverlayImageView extends SubsamplingScaleImageView {
    private Graph graph;
    private List<GraphNode> pathStores;
    private GraphNode location;
    private List<Store> destinations = UserPreferencesUtils.getStores(this.getContext());
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
        invalidate();
    }

    public void setLocation(GraphNode location) {
        this.location = location;
        invalidate(); // Redraw the view when the location changes
    }

    public void centerOnLocation() {
        if (location != null) {
            // Calculate the adjusted center point, slightly lower than the middle
            PointF locationCenter = new PointF(location.getXMultipliedForPath(), location.getYMultipliedForPath() - 1200); // Adjust the 200 value as needed

            // Attempt to create the animation builder
            AnimationBuilder animationBuilder = animateScaleAndCenter(getScale(), locationCenter);

            // Check if the animationBuilder is not null before starting the animation
            if (animationBuilder != null) {
                animationBuilder.withDuration(1000) // Duration of 1 second for the animation
                        .start();
            } else {
                // Fallback to immediate centering if animation is not possible
                setScaleAndCenter(getScale(), locationCenter);
            }
        }
    }


    private void centerOnTheMiddle() {
        PointF locationCenter = new PointF(middle.getXMultipliedForPath(), middle.getYMultipliedForPath());
        setScaleAndCenter(getScale(), locationCenter);
    }

    private void drawFinishIcon(Canvas canvas, PointF point, float scale) {
        Drawable finishIcon = getResources().getDrawable(R.drawable.destination, null);
        if (finishIcon != null) {
            // Adjust the icon size by changing the scaling factor
            int iconSize = (int) (250 * scale); // Increase the icon size
            int left = (int) (point.x - iconSize / 2);
            int top = (int) (point.y - iconSize);
            int right = (int) (point.x + iconSize / 2);
            int bottom = (int) (point.y );

            finishIcon.setBounds(left, top, right, bottom);
            finishIcon.draw(canvas);
        }
    }
    private void drawDiamond(Canvas canvas, PointF center, float size, Paint paint) {
        float halfSize = size / 2;

        // Define the points of the diamond
        float[] points = {
                center.x, center.y - halfSize,  // Top
                center.x + halfSize, center.y,  // Right
                center.x, center.y + halfSize,  // Bottom
                center.x - halfSize, center.y   // Left
        };

        // Draw the diamond
        canvas.drawLines(new float[] {
                points[0], points[1], points[2], points[3],  // Top to Right
                points[2], points[3], points[4], points[5],  // Right to Bottom
                points[4], points[5], points[6], points[7],  // Bottom to Left
                points[6], points[7], points[0], points[1]   // Left to Top
        }, paint);
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
                PointF center = sourceToViewCoord(node.getXMultipliedForPath(), node.getYMultipliedForPath());
                PointF nextCenter = sourceToViewCoord(nextNode.getXMultipliedForPath(), nextNode.getYMultipliedForPath());
                if (center != null && nextCenter != null) {
                    if (node.getFloor() == this.currentFloor) {
                        paint.setColor(Color.parseColor("#CD8055CD"));
                        canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);

                        // Check if this node represents a chosen store by matching names
                        if (isStoreChosenByName(node.getName())) {
                            // Draw an icon or mark the chosen store
                            drawFinishIcon(canvas, center, scale);
                        }
                    }
                }
            }
        }
        if (pathStores != null && !pathStores.isEmpty()) {
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
            PointF locationCenter = sourceToViewCoord(location.getXMultipliedForPath(), location.getYMultipliedForPath());
            if (locationCenter != null && location.getFloor() == this.currentFloor) {
                paint.setColor(Color.BLACK);
                canvas.drawCircle(locationCenter.x, locationCenter.y, 65 * scale, paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(locationCenter.x, locationCenter.y, 60 * scale, paint);
                paint.setColor(Color.parseColor("#8055CD"));
                canvas.drawCircle(locationCenter.x, locationCenter.y, 40 * scale, paint);

            }
        }

        // Center on the middle point only once, when the view is first displayed
        if (!isInitialized) {
            centerOnLocation();
            isInitialized = true;
        }
    }
    private boolean isStoreChosenByName(String nodeName) {
        for (Store store : destinations) {
            if (store.getStoreName().equalsIgnoreCase(nodeName)) {
                return true;
            }
        }
        return false;
    }


}
