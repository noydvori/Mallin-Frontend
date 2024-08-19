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
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Store;
import java.util.List;

public class PathOverlayImageView extends SubsamplingScaleImageView {
    private List<GraphNode> pathStores;
    private GraphNode location;
    private List<Store> destinations;
    private int currentFloor;

    // Constructors to initialize the view
    public PathOverlayImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public PathOverlayImageView(Context context) {
        super(context);
    }

    // Setter for the current floor, triggers a redraw when changed
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
        invalidate(); // Redraw the view when the floor changes
    }

    // Setter for the path, triggers a redraw when changed
    public void setPath(List<GraphNode> pathStores) {
        this.pathStores = pathStores;
        invalidate(); // Redraw the view when the path changes
    }

    // Setter for the current location, triggers a redraw when changed
    public void setLocation(GraphNode location) {
        this.location = location;
        invalidate(); // Redraw the view when the location changes
    }

    // Setter for destination stores, triggers a redraw when changed
    public void setDestinations(List<Store> destinations) {
        this.destinations = destinations;
        invalidate(); // Redraw the view when the destination list changes
    }

    // Method to center the view on the current location
    public void centerOnLocation() {
        if (location != null) {
            // Calculate the adjusted center point, slightly lower than the middle
            PointF locationCenter = new PointF(location.getXMultipliedForPath(), location.getYMultipliedForPath() - 1200);

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
    // Method to draw the destination icon at a specific point on the canvas
    private void drawDestinationIcon(Canvas canvas, PointF point, float scale) {
        Drawable finishIcon = getResources().getDrawable(R.drawable.destination, null);
        if (finishIcon != null) {
            // Adjust the icon size by changing the scaling factor
            int iconSize = (int) (200 * scale);
            int left = (int) (point.x - (float) iconSize / 2);
            int top = (int) (point.y - iconSize);
            int right = (int) (point.x + (float) iconSize / 2);
            int bottom = (int) (point.y );

            // Set bounds and draw the icon on the canvas
            finishIcon.setBounds(left, top, right, bottom);
            finishIcon.draw(canvas);
        }
    }

    // Method to draw the finish icon at a specific point on the canvas
    private void drawFinishIcon(Canvas canvas, PointF point, float scale) {
        Drawable finishIcon = getResources().getDrawable(R.drawable.finish_icon, null);
        if (finishIcon != null) {
            // Adjust the icon size by changing the scaling factor
            int iconSize = (int) (200 * scale);
            int left = (int) (point.x - (float) iconSize / 2);
            int top = (int) (point.y - iconSize);
            int right = (int) (point.x + (float) iconSize / 2);
            int bottom = (int) (point.y );

            // Set bounds and draw the icon on the canvas
            finishIcon.setBounds(left, top, right, bottom);
            finishIcon.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float scale = getScale();
        Paint paint = new Paint();
        paint.setStrokeWidth(30 * scale);

        // Draw the path connecting nodes on the current floor
        if (pathStores != null && !pathStores.isEmpty()) {
            for (int i = 0; i < pathStores.size() - 1; i++) {
                GraphNode node = pathStores.get(i);
                GraphNode nextNode = pathStores.get(i + 1);
                PointF center = sourceToViewCoord(node.getXMultipliedForPath(), node.getYMultipliedForPath());
                PointF nextCenter = sourceToViewCoord(nextNode.getXMultipliedForPath(), nextNode.getYMultipliedForPath());
                if (center != null && nextCenter != null) {
                    if (node.getFloor() == this.currentFloor && node != pathStores.get(pathStores.size() - 1)) {
                        paint.setColor(Color.parseColor("#CD8055CD")); // Set path color to purple
                        canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);

                        // Draw an icon if this node represents a chosen store
                        if (isStoreChosenByName(node.getName())) {
                            drawDestinationIcon(canvas, center, scale);
                        }
                    }
                }
            }
        }

        // Draw the finish icon on the last node of the path
        if (pathStores != null && !pathStores.isEmpty()) {
            GraphNode lastNode = pathStores.get(pathStores.size() - 1);
            if (lastNode.getFloor() == this.currentFloor) {
                PointF lastCenter = sourceToViewCoord(lastNode.getXMultipliedForPath(), lastNode.getYMultipliedForPath());
                if (lastCenter != null) {
                    drawFinishIcon(canvas, lastCenter, scale);
                }
            }
        }

        // Draw the current location as a series of concentric circles
        if (location != null) {
            PointF locationCenter = sourceToViewCoord(location.getXMultipliedForPath(), location.getYMultipliedForPath());
            if (locationCenter != null && location.getFloor() == this.currentFloor) {
                paint.setColor(Color.BLACK);
                canvas.drawCircle(locationCenter.x, locationCenter.y, 68 * scale, paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(locationCenter.x, locationCenter.y, 60 * scale, paint);
                paint.setColor(Color.parseColor("#8055CD")); // Purple circle
                canvas.drawCircle(locationCenter.x, locationCenter.y, 40 * scale, paint);
            }
        }
    }

    // Helper method to check if a store is chosen based on its name
    private boolean isStoreChosenByName(String nodeName) {
        for (Store store : destinations) {
            if (store.getStoreName().equalsIgnoreCase(nodeName)) {
                return true;
            }
        }
        return false;
    }
}
