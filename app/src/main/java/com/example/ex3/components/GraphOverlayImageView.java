package com.example.ex3.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.graph.NodeStatus;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class GraphOverlayImageView extends SubsamplingScaleImageView {
    private Graph graph;

    public GraphOverlayImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        invalidate(); // Redraw the view when the graph changes
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float scale = getScale();
        Paint paint = new Paint();
        paint.setStrokeWidth(10 * scale); // Scale the line width with the image

        // Draw the graph edges and nodes
        if (graph != null && graph.getNodes() != null && !graph.getNodes().isEmpty()) {
            paint.setColor(Color.RED);
            for (GraphNode node : graph.getNodes()) {
                for (String neighbor : node.getNeighbors()) {
                    GraphNode target = graph.getNode(neighbor);
                    if (target == null) continue;
                    PointF start = sourceToViewCoord(node.getXMultpyed(), node.getYMultpyed());
                    PointF end = sourceToViewCoord(target.getXMultpyed(), target.getYMultpyed());
                    Log.d("TEST", "point start: " + start +" end "  + end);
                    if (start != null && end != null) {
                        if (node.getStatus() == NodeStatus.selected && target.getStatus() == NodeStatus.selected) {
                            paint.setColor(Color.GREEN);
                        } else {
                            paint.setColor(Color.RED);
                        }
                       canvas.drawLine(start.x, start.y, end.x, end.y, paint);
                    }
                }
            }
            // Draw nodes
            paint.setStyle(Paint.Style.FILL);
            for (GraphNode node : graph.getNodes()) {
                PointF center = sourceToViewCoord(node.getXMultpyed(), node.getYMultpyed());
                if (center != null) {
                    if (node.getStatus() == NodeStatus.none) {
                        paint.setColor(Color.RED);
                        canvas.drawCircle(center.x, center.y, 30 * scale, paint);
                    } else if (node.getStatus() == NodeStatus.selected) {
                        paint.setColor(Color.YELLOW);
                        canvas.drawCircle(center.x, center.y, 40 * scale, paint);
                    }
                }
            }
        }

    }
}
