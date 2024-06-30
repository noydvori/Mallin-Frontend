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
import com.example.ex3.devtool.graph.GraphEdge;
import com.example.ex3.devtool.graph.GraphNode;
import com.example.ex3.devtool.graph.NodeStatus;

import org.w3c.dom.Node;

import java.util.List;

public class PathOverlayImageView extends SubsamplingScaleImageView {
    private Graph graph;
    private List<GraphNode> pathStores;
    public PathOverlayImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public PathOverlayImageView(Context context) {
        super(context);
    }

    public void setGraph(Graph graph, List<GraphNode> pathStores) {
        this.graph = graph;
        this.pathStores = pathStores;
        invalidate(); // Redraw the view when the graph changes
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (graph == null || graph.getNodes() == null || graph.getNodes().isEmpty() || pathStores.isEmpty()) {
            return;
        }

        float scale = getScale();
        Paint paint = new Paint();
        paint.setStrokeWidth(10 * scale); // Scale the line width with the image

        // Draw edges and nodes in pathStores
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        for (int i = 0; i < pathStores.size() - 2; i++) {
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

    }
}