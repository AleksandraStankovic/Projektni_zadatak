package model;

import org.graphstream.graph.Path;
import java.util.ArrayList;
import java.util.List;

public class RouteDetails {
    private Path path;
    private int totalTime;
    private int totalCost;
    private int totalTransfers;
    private int totalWeight; 
    private List<RouteSegment> segments;
    

    public RouteDetails(PathInfo pathInfo) {
        this.path = pathInfo.getPath();
        this.totalTime = pathInfo.getTotalTime();
        this.totalCost = pathInfo.getTotalCost();
        this.totalTransfers = pathInfo.getTotalTransfers();
        this.segments = new ArrayList<>(); 
        this.totalWeight = pathInfo.getTotalWeight(); 
        
        
        if (path != null) {
            
            path.getEdgeSet().forEach(edge -> {
                String from = edge.getSourceNode().getId();
                String to = edge.getTargetNode().getId();
                String type = edge.getAttribute("type") != null ? (String) edge.getAttribute("type") : "Unknown";
                int duration = edge.getAttribute("duration") != null ? (int) edge.getAttribute("duration") : 0;
                int price = edge.getAttribute("price") != null ? (int) edge.getAttribute("price") : 0;
                String departure = edge.getAttribute("departureStr") != null ? (String) edge.getAttribute("departureStr") : null; 
                

                

                RouteSegment segment = new RouteSegment();
                segment.setFromStation(from);
                segment.setToStation(to);
                segment.setTransportType(type);
                segment.setDuration(duration);
                segment.setPrice(price);
                segment.setDepartureTime(departure);

                
                if (departure != null) {
                    String[] parts = departure.split(":");
                    int depHour = Integer.parseInt(parts[0]);
                    int depMin = Integer.parseInt(parts[1]);
                    int totalMin = depHour * 60 + depMin + duration;
                    segment.setArrivalTime(String.format("%02d:%02d", (totalMin / 60) % 24, totalMin % 60));
                }

                segments.add(segment);
            });
        }

    }
    
    // Constructor for Path (legacy)
    public RouteDetails(Path path) {
        this.path = path;
        this.totalTime = 0;
        this.totalCost = 0;
        this.totalTransfers = 0;
        this.segments = new ArrayList<>(); // Empty segments for now
    }
    
   
    public Path getPath() { return path; }
    public int getTotalTime() { return totalTime; }
    public int getTotalCost() { return totalCost; }
    public int getTotalTransfers() { return totalTransfers; }
    public List<RouteSegment> getSegments() { return segments; }
    
   
    public int getTransferCount() {
        return totalTransfers;
    }
    
    
    public String getFormattedTotalTime() {
        return totalTime + " min";
    }
    
    public String getFormattedTotalCost() {
        return totalCost + " n.j";
    }
    
    public int getTotalWeight() {
        return totalWeight;
    }
    
    @Override
    public String toString() {
        return "Time: " + getFormattedTotalTime() + 
               " | Cost: " + getFormattedTotalCost() + 
               " | Transfers: " + totalTransfers;
    }
}





