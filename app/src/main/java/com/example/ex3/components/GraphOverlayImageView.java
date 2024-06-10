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

public class GraphOverlayImageView extends SubsamplingScaleImageView {
        private Graph graph;
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
            if (graph == null || graph.getNodes() == null  || graph.getNodes().size() == 0) {
                return;
            }
            float scale = getScale();
            Paint paint = new Paint();
            paint.setColor(Color.RED); // Example color
            paint.setStrokeWidth(10 * scale); // Scale the line width with the image
            // Draw edges
            for (GraphNode node : graph.getNodes()) {

                for(String neighbor : node.getNeighbors()) {
                    GraphNode target = graph.getNode(neighbor);
                    if(target == null) {
                        continue;
                    }
                    PointF start = sourceToViewCoord(node.getXMultpyed(), node.getYMultpyed());
                    PointF end = sourceToViewCoord(target.getXMultpyed(), target.getYMultpyed());
                    Log.d("TEST", "point start: " + start +" end "  + end);
                    if (start != null && end != null) {
                        if(node.getStatus()==NodeStatus.selected && target.getStatus()== NodeStatus.selected){
                            paint.setColor(Color.GREEN);
                        }else {
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
                    if(node.getStatus() == NodeStatus.none) {
                        paint.setColor(Color.RED);

                        canvas.drawCircle(center.x, center.y, 30 * scale, paint); // Scale node size with the image
                    }else if(node.getStatus() == NodeStatus.selected){
                        paint.setColor(Color.YELLOW);

                        canvas.drawCircle(center.x , center.y , 40 * scale, paint); // Scale node size with the image=
                    }
                }
            }
        }


}


/*


    public GraphOverlayImageView(Context context) {
        super(context);
    }


 */