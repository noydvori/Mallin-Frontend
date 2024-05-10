package com.example.ex3.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.ex3.objects.Graph;
import com.example.ex3.objects.GraphEdge;
import com.example.ex3.objects.GraphNode;

public class GraphOverlayImageView extends SubsamplingScaleImageView {
        private Graph graph;
        float multpler = 2;
        public GraphOverlayImageView(Context context, AttributeSet attr) {
            super(context, attr);
        }

        public GraphOverlayImageView(Context context) {
            super(context);
        }

        public void setGraph(Graph graph) {
            this.graph = graph;
            invalidate(); // Redraw the view when the graph changes
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (graph == null || graph.getNodes() == null  || graph.getNodes().size() == 0) return;

            // Get the current scale and translation from the image view
            float scale = getScale();

            Paint paint = new Paint();
            paint.setColor(Color.RED); // Example color
            paint.setStrokeWidth(5 * scale); // Scale the line width with the image

            // Draw edges
            for (GraphNode node : graph.getNodes()) {
                for(GraphEdge edge : node.getEdges()) {
                    PointF start = sourceToViewCoord(node.getX(), node.getY());
                    PointF end = sourceToViewCoord(edge.getTarget().getX(), edge.getTarget().getY());
                    if (start != null && end != null) {
                        canvas.drawLine(start.x, start.y, end.x, end.y, paint);
                    }
                }

            }

            // Draw nodes
            paint.setStyle(Paint.Style.FILL);
            for (GraphNode node : graph.getNodes()) {
                PointF center = sourceToViewCoord(node.getX(), node.getY());
                if (center != null) {
                    canvas.drawCircle(center.x, center.y, 20 * scale, paint); // Scale node size with the image
                }
            }
        }
}


/*


    public GraphOverlayImageView(Context context) {
        super(context);
    }


 */