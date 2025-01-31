package com.example.ex3.adapters;
import android.content.Context;
import android.util.Xml;

import com.example.ex3.devtool.graph.Graph;
import com.example.ex3.devtool.graph.GraphNode;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class GrapthDataAdapter {
    Context context;
    Graph graph;
    private int level;
    private final static String LOG_TAG = "GraphDataAdapter";

    public GrapthDataAdapter(Context context, int level) {
        this.context = context;
        this.level = level;
         graph = new Graph(new ArrayList<>());
    }


    private InputStream getInputStream(int resource) {
        InputStream is;
            is = context.getResources().openRawResource(resource);
        return is;
    }

public void createNode(XmlPullParser parser) throws XmlPullParserException, IOException {
    String id = "";
    String name = "";
    float x = 0, y = 0;

    int eventType = parser.getEventType();
    while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("section"))) {
        if (eventType == XmlPullParser.START_TAG && parser.getName().equals("attribute")) {
            String key = parser.getAttributeValue(null, "key");
            if (parser.next() == XmlPullParser.TEXT) {  // Move to the next element which should be the text node
                String value = parser.getText();
                switch (key) {
                    case "id":
                        id = level + value;
                        break;
                    case "label":
                        name = value;
                        break;
                    case "x":
                        x = Float.parseFloat(value);
                        break;
                    case "y":
                        y = Float.parseFloat(value);
                        break;
                }
            }
        }
        eventType = parser.next();
    }

    GraphNode node = new GraphNode(id, name, x, y, level);
    graph.addNode(node);
}

    public void createEdge(XmlPullParser parser) throws XmlPullParserException, IOException {
        String sourceId = "";
        String targetId = "";

        int eventType = parser.getEventType();
        while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("section"))) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("attribute")) {
                String key = parser.getAttributeValue(null, "key");
                if (parser.next() == XmlPullParser.TEXT) {  // Move to the next element which should be the text node
                    String value = parser.getText();
                    switch (key) {
                        case "source":
                            sourceId = level + value;
                            break;
                        case "target":
                            targetId = level + value;
                            break;
                    }
                }
            }
            eventType = parser.next();
        }



        if ((sourceId != null && sourceId.equals("") )|| (targetId != null && !targetId.equals("") )) {
            graph.addEdge(sourceId, targetId);  // Assuming a method to add edges to the graph
        }
    }
    public Graph loadGrapthData(int resource) {
        InputStream is = getInputStream(resource);
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = parser.getName();
                        switch (tagName) {
                            case "section":
                                String elementType = parser.getAttributeValue(null, "name");
                                switch (elementType) {
                                    case "node":
                                        createNode(parser);
                                        break;
                                    case "edge":
                                        createEdge(parser);
                                        break;
                                }

                                // Additional logic to handle node element
                                // You might want to create a GraphNode object here and add it to your graph
                                break;
                            case "test":

                                String sourceId = parser.getAttributeValue(null, "source");
                                String targetId = parser.getAttributeValue(null, "target");
                                break;
                        }
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return graph;
        }

    public Graph getGraph() {
        return this.graph;
    }
}

