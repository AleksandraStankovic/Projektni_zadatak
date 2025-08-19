package model;

import org.graphstream.graph.Path;
import org.graphstream.graph.Edge;
import java.util.ArrayList;
import java.util.List;

public class RouteDetails {
    private List<RouteSegment> segments;
    private int totalTime;
    private int totalCost;
    private int transferCount;

    public RouteDetails(Path path) {
        this.segments = new ArrayList<>();
        this.totalTime = 0;
        this.totalCost = 0;
        this.transferCount = 0;

        boolean lastWasTransfer = false;

        for (Edge edge : path.getEdgeSet()) {
            String type = (String) edge.getAttribute("type");

            if (type.equals("transfer")) {
                if (!lastWasTransfer) {
                    transferCount++;
                }
                lastWasTransfer = true;
            } else {
                // Extract attributes with proper type casting
                String from = (String) edge.getSourceNode().getId();
                String to = (String) edge.getTargetNode().getId();
                String departureTime = (String) edge.getAttribute("departureTime");
                int duration = (int) edge.getAttribute("duration");
                int price = (int) edge.getAttribute("price");

                RouteSegment segment = new RouteSegment(from, to, type, departureTime, duration, price);
                segments.add(segment);

                totalTime += duration;
                totalCost += price;
                lastWasTransfer = false;
            }
        }
    }

    // Getters
    public List<RouteSegment> getSegments() {
        return segments;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getTransferCount() {
        return transferCount;
    }

    // Utility method for display
    public String getFormattedTotalTime() {
        int hours = totalTime / 60;
        int minutes = totalTime % 60;
        return String.format("%dh %02dmin", hours, minutes);
    }

    public String getFormattedTotalCost() {
        return totalCost + " KM";
    }
}