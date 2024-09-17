package com.example.ex3.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.ex3.ConfirmPath;
import com.example.ex3.CurrentLocation;
import com.example.ex3.R;
import com.example.ex3.RedirectingDialogFragment;
import com.example.ex3.api.NavigationAPI;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.entities.Store;
import com.example.ex3.utils.UserPreferencesUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Callback;

public class PathOverlayImageView extends SubsamplingScaleImageView {
    private List<GraphNode> pathStores;
    private List<GraphNode> passed;
    private float currentAngle = 0;
    private GraphNode location;
    private HashSet<Store> destinations;
    private Set<String> destinationsStrings;
    private int currentFloor;
    private boolean isRedirecting = false;


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

    public int getCurrentFloor() {
        return currentFloor;
    }

    // Setter for the path, triggers a redraw when changed
    public void setPath(List<GraphNode> pathStores) {
        this.pathStores = pathStores;
        this.passed = new ArrayList<>();
        invalidate(); // Redraw the view when the path changes
    }

    // Setter for the current location, triggers a redraw when changed
    public void setLocation(GraphNode location) {
        // אם מתבצע כרגע REDIRECTING לא נעדכן מיקום
        if (isRedirecting) {
            return; // לא לעשות כלום אם מתבצע REDIRECTING
        }
        // הפעלת תהליך ה-Redirecting
        this.location = location;
        if(pathStores != null && !pathStores.isEmpty()) {
            GraphNode first = pathStores.get(0);
            if(distanceBetween(first, location) < 5){
                passed.add(first);
                pathStores.remove(first);
            }
        if (distanceBetween(first, location) > 100) {
            isRedirecting = true; // להגדיר שה-REDIRECTING התחיל

            // Show the redirecting dialog
            RedirectingDialogFragment dialog = new RedirectingDialogFragment();
            dialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "Redirecting");
            List<Store> stores = new ArrayList<>();
            for(GraphNode node: pathStores) {
                for(Store destination : destinations) {
                    if(destinations.contains(node)) {
                        stores.add(destination);
                    }
                }
            }
            // Fetch new path from server in the background
            fetchRedirection(location, stores, () -> {
                // Code to execute when redirection is complete, e.g., dismissing the dialog
                dialog.dismiss();
                isRedirecting = false; // להגדיר שה-REDIRECTING התחיל
            });
        }
            }

        invalidate(); // Redraw the view when the location changes
    }

    //private void fetchNewPathFromServer(Callback failedToReloadPath) {
    //    String token = UserPreferencesUtils.getToken(getContext());
    //    NavigationAPI.getInstance().createRout(token, location, UserPreferencesUtils.getStores(getContext())).thenAccept(paths -> {
    //        UserPreferencesUtils.setPaths(this, paths);
    //        runOnUiThread(() -> {
    //            UserPreferencesUtils.setPaths(this, paths);
    //            pathStores = paths;
    //            passed = !!!!!!!!;
    //        });
    //    }).exceptionally(throwable -> {
    //        runOnUiThread(() -> {
    //            Toast.makeText(this, "Error fetching optimal route", Toast.LENGTH_SHORT).show();
    //        });
    //        return null;
    //    });
    //}

    // Setter for destination stores, triggers a redraw when changed
    public void setDestinations(List<Store> destinations) {
        this.destinations = new HashSet<>(destinations); // Convert the List to a HashSet
        this.destinationsStrings = destinations.stream()
                .map(Store::getStoreName) // Extract the store names
                .collect(Collectors.toSet()); // Collect to a HashSet
        invalidate(); // Redraw the view when the destination list changes
    }

    // Method to center the view on the current location

    // Method to center the view on the current location
    public void centerOnLocation() {
        if (location != null) {
            // Calculate the adjusted center point, slightly lower than the middle
            PointF locationCenter = new PointF(location.getXMultipliedForPath(), location.getYMultipliedForPath());

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
        float tolerance = 15.0f;

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
        // If deltaX is smaller than the defined threshold, check deltaY
        if (Math.abs(deltaY) < tolerance) {
            if (deltaX > 0) {
                // The first point is above the second, need to rotate by 180 degrees
                return -90;
            } else {
                // The second point is above the first, no rotation needed
                return 90;
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
            return (float) Math.abs(Math.toDegrees(Math.atan2(deltaY, deltaX))) + 90;
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
    private void smoothRotateView(View view, float fromAngle, float toAngle, Runnable onEndCallback) {
        // Create an animation that rotates the view from the starting angle to the final angle

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", fromAngle, toAngle);

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        view.animate().rotation(toAngle).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(onEndCallback).start();


    }
    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        float scale = getScale();
        Paint paint = new Paint();
        paint.setStrokeWidth(30 * scale);



        if (passed != null && pathStores != null && !passed.isEmpty() && !pathStores.isEmpty()) {
            // Rotate only if the closest node and the next node exist
            GraphNode closestNode = passed.get(passed.size()-1);
            GraphNode nextNode1 = pathStores.get(0);
            // Calculate the angle to rotate the map
            float angle = calculateAngleBetweenNodes(closestNode, nextNode1);
            if(angle > 180) {
                angle -= 180;
            }
            if(angle < -180 ) {
                angle += 180;
            }
            // Rotate the canvas around the center of the screen
            PointF locationCenter = sourceToViewCoord(location.getXMultipliedForPath(), location.getYMultipliedForPath());

            if (location != null && locationCenter != null) {
                float finalAngle = angle;
                smoothRotateView(this, currentAngle, angle, () -> {
                    // Update currentAngle once the rotation animation is done
                    currentAngle = finalAngle;
                });
            }
        }
        super.draw(canvas);
        if (passed != null && !passed.isEmpty()) {
            for (int i = 0; i < passed.size() - 1; i++) {
                GraphNode node = passed.get(i);
                GraphNode nextNode = passed.get(i + 1);
                PointF center = sourceToViewCoord(node.getXMultipliedForPath(), node.getYMultipliedForPath());
                PointF nextCenter = sourceToViewCoord(nextNode.getXMultipliedForPath(), nextNode.getYMultipliedForPath());

                if (center != null && nextCenter != null && passed != null && !passed.isEmpty()) {
                    if (node.getFloor() == this.currentFloor) {
                        paint.setColor(Color.LTGRAY);
                        canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);

                    }

                    if (isStoreChosenByName(node.getName()) && node.getFloor() == this.currentFloor) {
                        drawDestinationIcon(canvas, center, scale, (-1) * this.currentAngle);
                    }
                }
            }
        }
        if (pathStores != null && !pathStores.isEmpty() && passed != null && !passed.isEmpty()) {
        if(pathStores.get(0).getFloor() == passed.get(passed.size()-1).getFloor() && location.getFloor() == this.currentFloor) {
            paint.setColor(Color.parseColor("#CD8055CD")); // Purple
            PointF center = sourceToViewCoord(passed.get(passed.size()-1).getXMultipliedForPath(), passed.get(passed.size()-1).getYMultipliedForPath());
            PointF nextCenter = sourceToViewCoord(pathStores.get(0).getXMultipliedForPath(), pathStores.get(0).getYMultipliedForPath());
            if(center != null && nextCenter !=null) {
                canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);

            }
        }
        }

        // Draw the path connecting nodes on the current floor
        if (pathStores != null && !pathStores.isEmpty()) {
            for (int i = 0; i < pathStores.size() - 1; i++) {
                GraphNode node = pathStores.get(i);
                GraphNode nextNode = pathStores.get(i + 1);
                PointF center = sourceToViewCoord(node.getXMultipliedForPath(), node.getYMultipliedForPath());
                PointF nextCenter = sourceToViewCoord(nextNode.getXMultipliedForPath(), nextNode.getYMultipliedForPath());

                if (center != null && nextCenter != null) {
                    if (node.getFloor() == this.currentFloor && node != pathStores.get(pathStores.size() - 1)) {
                        paint.setColor(Color.parseColor("#CD8055CD")); // Purple
                        canvas.drawLine(center.x, center.y, nextCenter.x, nextCenter.y, paint);
                        if (isStoreChosenByName(node.getName())) {
                            drawDestinationIcon(canvas, center, scale, (-1) * this.currentAngle);
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
                    drawFinishIcon(canvas, lastCenter, scale, (-1) *this.currentAngle);
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
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            location = bundle.getParcelable("location");  // Restore the location
            currentAngle = bundle.getFloat("currentAngle");  // Restore the angle
            super.onRestoreInstanceState(bundle.getParcelable("superState"));

            // Trigger a redraw and recenter after restoring the state
            if (location != null) {
                centerOnLocation();  // Recenter the view
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }
    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putParcelable("location", (Parcelable) location);  // Save the current location
        bundle.putFloat("currentAngle", currentAngle);  // Save the current angle
        return bundle;
    }

    // Helper method to check if a store is chosen based on its name
    private boolean isStoreChosenByName(String nodeName) {
            if (destinationsStrings.contains(nodeName)) {
                return true;
            }
        return false;
    }
    private void fetchRedirection(GraphNode node, List<Store> stores, Runnable onComplete) {
        String token = UserPreferencesUtils.getToken(getContext());
        NavigationAPI.getInstance().createRedirection(token, node, stores).thenAccept(nodes -> {
            UserPreferencesUtils.setNodes(getContext(), nodes);
            setPath(nodes);

            // Execute the runnable task when redirection is complete
            if (onComplete != null) {
                onComplete.run();
                // הפעלת תהליך ה-Redirecting
            }
        });
    }
}

