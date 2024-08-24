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

    private float angle = 0;
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

    // Helper method to calculate the distance between two nodes
    private float distanceBetween(GraphNode node1, GraphNode node2) {
        float dx = node1.getXMultipliedForPath() - node2.getXMultipliedForPath();
        float dy = node1.getYMultipliedForPath() - node2.getYMultipliedForPath();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // Helper method to calculate the angle between two nodes
    private float calculateAngleBetweenNodes(GraphNode currentNode, GraphNode nextNode) {
        if (currentNode == null || nextNode == null) {
            return 0;
        }

        float deltaX = nextNode.getXMultipliedForPath() - currentNode.getXMultipliedForPath();
        float deltaY = nextNode.getYMultipliedForPath() - currentNode.getYMultipliedForPath();

        // Tolerance is a small value defining what is considered 'almost' straight
        float tolerance = 25.0f;

        // If deltaX is smaller than the defined threshold, check deltaY
        if (Math.abs(deltaX) < tolerance) {
            if (deltaY > 0) {
                // The first point is above the second, need to rotate by 180 degrees
                return 180;
            } else {
                // The second point is above the first, no rotation needed
                return 0;
            }
        }

        if (deltaY > 0) {
            // The first point is above the second, need to rotate by 270 degrees minus the calculated angle
            return (float) (270 - Math.abs(Math.toDegrees(Math.atan2(deltaY, deltaX))));
        } else {
            if (deltaX > 0) {
                // The second point is above the first, rotate by the calculated angle minus 90 degrees
                return (float) Math.abs(Math.toDegrees(Math.atan2(deltaY, deltaX))) - 90;
            }
            // The second point is above the first, rotate by the calculated angle
            return (float) Math.abs(Math.toDegrees(Math.atan2(deltaY, deltaX)));
        }
    }

    // Method to draw the destination icon at a specific point on the canvas
    private void drawDestinationIcon(Canvas canvas, PointF point, float scale, float angle) {
        Drawable finishIcon = getResources().getDrawable(R.drawable.destination, null);
        if (finishIcon != null) {
            // Adjust the icon size by changing the scaling factor
            int iconSize = (int) (200 * scale);
            int left = (int) (point.x - (float) iconSize / 2);
            int top = (int) (point.y - iconSize);
            int right = (int) (point.x + (float) iconSize / 2);
            int bottom = (int) (point.y);

            // Save the current state of the canvas
            canvas.save();

            // Rotate the canvas around the center of the icon
            canvas.rotate(angle, point.x, point.y);

            // Set bounds and draw the icon on the canvas
            finishIcon.setBounds(left, top, right, bottom);
            finishIcon.draw(canvas);

            // Restore the canvas to its original state
            canvas.restore();
        }
    }

    // Method to draw the finish icon at a specific point on the canvas
    private void drawFinishIcon(Canvas canvas, PointF point, float scale, float angle) {
        Drawable finishIcon = getResources().getDrawable(R.drawable.finish_icon, null);
        if (finishIcon != null) {
            // Adjust the icon size by changing the scaling factor
            int iconSize = (int) (200 * scale);
            int left = (int) (point.x - (float) iconSize / 2);
            int top = (int) (point.y - iconSize);
            int right = (int) (point.x + (float) iconSize / 2);
            int bottom = (int) (point.y);

            // Save the current state of the canvas
            canvas.save();

            // Rotate the canvas around the center of the icon
            canvas.rotate(angle, point.x, point.y);

            // Set bounds and draw the icon on the canvas
            finishIcon.setBounds(left, top, right, bottom);
            finishIcon.draw(canvas);

            // Restore the canvas to its original state
            canvas.restore();
        }
    }
    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        float scale = getScale();
        Paint paint = new Paint();
        paint.setStrokeWidth(30 * scale);

        // Rotate only if the closest node and the next node exist
        GraphNode closestNode = getClosestNode();
        GraphNode nextNode1 = getNextNode(closestNode);

        if (closestNode != null && nextNode1 != null) {
            // Calculate the angle to rotate the map
            angle = calculateAngleBetweenNodes(closestNode, nextNode1);

            // Rotate the canvas around the center of the screen
            PointF locationCenter = sourceToViewCoord(location.getXMultipliedForPath(), location.getYMultipliedForPath());

            if (locationCenter != null) {
                // Apply rotation around the center of the canvas
                canvas.rotate(angle, locationCenter.x, locationCenter.y);
            }
        }
        super.draw(canvas);

        // Draw the path connecting nodes on the current floor
        if (pathStores != null && !pathStores.isEmpty()) {
            for (int i = 0; i < pathStores.size() - 1; i++) {
                GraphNode node = pathStores.get(i);
                GraphNode nextNode = pathStores.get(i + 1);
                PointF center = sourceToViewCoord(node.getXMultipliedForPath(), node.getYMultipliedForPath());
                PointF nextCenter = sourceToViewCoord(nextNode.getXMultipliedForPath(), nextNode.getYMultipliedForPath());

                if (center != null && nextCenter != null) {
                    if (node.getFloor() == this.currentFloor && node != pathStores.get(pathStores.size() - 1)) {
                        float angle = calculateAngleBetweenNodes(node, nextNode);

                        // Draw the path line
                        if (nodeHasBeenPassed(nextNode)) {
                            paint.setColor(Color.LTGRAY);
                        } else {
                            paint.setColor(Color.parseColor("#CD8055CD")); // Purple
                        }
                        canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);

                        if (isStoreChosenByName(node.getName())) {
                            drawDestinationIcon(canvas, center, scale, (-1) * this.angle);
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
                    drawFinishIcon(canvas, lastCenter, scale, (-1) *this.angle);
                }
            }
        }

        // Rest of the draw method for the location indicator
        if (location != null) {
            PointF locationCenter = sourceToViewCoord(location.getXMultipliedForPath(), location.getYMultipliedForPath());
            if (locationCenter != null && location.getFloor() == this.currentFloor) {
                paint.setColor(Color.BLACK);
                canvas.drawCircle(locationCenter.x, locationCenter.y, 68 * scale, paint);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(locationCenter.x, locationCenter.y, 60 * scale, paint);
                paint.setColor(Color.parseColor("#8055CD")); // Primary Purple
                canvas.drawCircle(locationCenter.x, locationCenter.y, 40 * scale, paint);
            }
        }

        canvas.restore();
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

    // Helper method to check if a node has been passed
    private boolean nodeHasBeenPassed(GraphNode node) {
        return location != null && distanceBetween(node, location) < 0.1;
    }

    // Helper method to find the closest node to the current location
    private GraphNode getClosestNode() {
        if (pathStores == null || pathStores.isEmpty()) {
            return null;
        }

        GraphNode closestNode = null;
        float minDistance = Float.MAX_VALUE;

        for (GraphNode node : pathStores) {
            float distance = distanceBetween(node, location);
            if (distance < minDistance) {
                minDistance = distance;
                closestNode = node;
            }
        }
        return closestNode;
    }

    // Helper method to get the next node after the closest node
    private GraphNode getNextNode(GraphNode closestNode) {
        if (closestNode == null || pathStores == null || pathStores.isEmpty()) {
            return null;
        }

        int index = pathStores.indexOf(closestNode);
        if (index == -1 || index + 1 >= pathStores.size()) {
            return null;
        }
        return pathStores.get(index + 1);
    }}