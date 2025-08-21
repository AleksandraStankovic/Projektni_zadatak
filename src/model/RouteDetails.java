package model;

import org.graphstream.graph.Path;
import java.util.ArrayList;
import java.util.List;

public class RouteDetails {
    private Path path;
    private int totalTime;
    private int totalCost;
    private int totalTransfers;
    private int totalWeight;  // Add this field
    private List<RouteSegment> segments;
    
    // Constructor that accepts PathInfo
    public RouteDetails(PathInfo pathInfo) {
        this.path = pathInfo.getPath();
        this.totalTime = pathInfo.getTotalTime();
        this.totalCost = pathInfo.getTotalCost();
        this.totalTransfers = pathInfo.getTotalTransfers();
        this.segments = new ArrayList<>(); // Empty segments for now
        this.totalWeight = pathInfo.getTotalWeight();  // Add this

    }
    
    // Constructor for Path (legacy)
    public RouteDetails(Path path) {
        this.path = path;
        this.totalTime = 0;
        this.totalCost = 0;
        this.totalTransfers = 0;
        this.segments = new ArrayList<>(); // Empty segments for now
    }
    
    // Getters
    public Path getPath() { return path; }
    public int getTotalTime() { return totalTime; }
    public int getTotalCost() { return totalCost; }
    public int getTotalTransfers() { return totalTransfers; }
    public List<RouteSegment> getSegments() { return segments; }
    
    // Add this method to fix the error
    public int getTransferCount() {
        return totalTransfers;
    }
    
    // Convenience methods
    public String getFormattedTotalTime() {
        return totalTime + " min";
    }
    
    public String getFormattedTotalCost() {
        return totalCost + " €";
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











//package model;
//
//import org.graphstream.graph.Path;
//import org.graphstream.graph.Edge;
//import org.graphstream.graph.Node;
//import java.util.ArrayList;
//import java.util.List;
//
//public class RouteDetails {
//    private Path path;
//    private int totalTime;
//    private int totalCost;
//    private int totalTransfers;
//    private List<RouteSegment> segments;
//    
//    // Existing constructor
//    public RouteDetails(Path path) {
//        this.path = path;
//        this.segments = new ArrayList<>();
//        calculateRouteMetrics();
//    }
//    
//    // Constructor that accepts PathInfo
//    public RouteDetails(PathInfo pathInfo) {
//        this.path = pathInfo.getPath();
//        this.totalTime = pathInfo.getTotalTime();
//        this.totalCost = pathInfo.getTotalCost();
//        this.totalTransfers = pathInfo.getTotalTransfers();
//        this.segments = extractSegmentsFromPath();
//    }
//    
//    // Extract segments from the path
//    private List<RouteSegment> extractSegmentsFromPath() {
//        List<RouteSegment> segmentList = new ArrayList<>();
//        
//        if (path == null || path.size() == 0) {
//            return segmentList;
//        }
//        
//        Node currentNode = path.getRoot();
//        
//        for (int i = 0; i < path.size(); i++) {
//            // Get the edge (this needs to be adapted based on your GraphStream version)
//            Edge edge = null;
//            try {
//                // Try different GraphStream API methods
//                edge = path.getEdge(i);
//            } catch (Exception e) {
//                // If getEdge(i) doesn't work, try alternative approach
//                break;
//            }
//            
//            if (edge != null) {
//                Node nextNode = edge.getTargetNode();
//                
//                RouteSegment segment = new RouteSegment();
//                segment.setFromStation(currentNode.getId());
//                segment.setToStation(nextNode.getId());
//                segment.setTransportType(getTransportTypeFromNode(currentNode));
//                
//                // Extract edge attributes
//                String edgeType = (String) edge.getAttribute("type");
//                if (edgeType != null) {
//                    if ("transfer".equals(edgeType)) {
//                        segment.setTransportType("Transfer");
//                        segment.setDuration((int) edge.getNumber("minTransferTime"));
//                        segment.setPrice(0);
//                    } else {
//                        segment.setTransportType(getTransportTypeFromNode(currentNode));
//                        segment.setDuration((int) edge.getNumber("duration"));
//                        segment.setPrice((int) edge.getNumber("price"));
//                    }
//                }
//                
//                segmentList.add(segment);
//                currentNode = nextNode;
//            }
//        }
//        
//        return segmentList;
//    }
//    
//    // Calculate metrics for the old constructor
//    private void calculateRouteMetrics() {
//        this.totalTime = 0;
//        this.totalCost = 0;
//        this.totalTransfers = 0;
//        
//        if (path != null) {
//            for (Edge edge : path.getEdgeSet()) {
//                String edgeType = (String) edge.getAttribute("type");
//                if (edgeType != null) {
//                    if ("transfer".equals(edgeType)) {
//                        totalTime += (int) edge.getNumber("minTransferTime");
//                        totalTransfers++;
//                    } else {
//                        totalTime += (int) edge.getNumber("duration");
//                        totalCost += (int) edge.getNumber("price");
//                    }
//                }
//            }
//        }
//        this.segments = extractSegmentsFromPath();
//    }
//    
//    private String getTransportTypeFromNode(Node node) {
//        String nodeId = node.getId();
//        if (nodeId.startsWith("A_")) {
//            return "Bus";
//        } else if (nodeId.startsWith("Z_")) {
//            return "Train";
//        }
//        return "Unknown";
//    }
//    
//    // Getters
//    public Path getPath() { return path; }
//    public int getTotalTime() { return totalTime; }
//    public int getTotalCost() { return totalCost; }
//    public int getTotalTransfers() { return totalTransfers; }
//    public List<RouteSegment> getSegments() { return segments; }
//    
//    // Add this method to fix the error
//    public int getTransferCount() {
//        return totalTransfers;
//    }
//    
//    // Convenience methods
//    public String getFormattedTotalTime() {
//        return totalTime + " min";
//    }
//    
//    public String getFormattedTotalCost() {
//        return totalCost + " €";
//    }
//    
//    @Override
//    public String toString() {
//        return "Time: " + getFormattedTotalTime() + 
//               " | Cost: " + getFormattedTotalCost() + 
//               " | Transfers: " + totalTransfers;
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////package model;
////
////import org.graphstream.graph.Path;
////import org.graphstream.graph.Edge;
////import java.util.ArrayList;
////import java.util.List;
////
////public class RouteDetails {
////    private List<RouteSegment> segments;
////    private int totalTime;
////    private int totalCost;
////    private int transferCount;
////
////    public RouteDetails(Path path) {
////        this.segments = new ArrayList<>();
////        this.totalTime = 0;
////        this.totalCost = 0;
////        this.transferCount = 0;
////
////        boolean lastWasTransfer = false;
////
////        for (Edge edge : path.getEdgeSet()) {
////            String type = (String) edge.getAttribute("type");
////
////            if (type.equals("transfer")) {
////                if (!lastWasTransfer) {
////                    transferCount++;
////                }
////                lastWasTransfer = true;
////            } else {
////                // Extract attributes with proper type casting
////                String from = (String) edge.getSourceNode().getId();
////                String to = (String) edge.getTargetNode().getId();
////                String departureTime = (String) edge.getAttribute("departureTime");
////                int duration = (int) edge.getAttribute("duration");
////                int price = (int) edge.getAttribute("price");
////
////                RouteSegment segment = new RouteSegment(from, to, type, departureTime, duration, price);
////                segments.add(segment);
////
////                totalTime += duration;
////                totalCost += price;
////                lastWasTransfer = false;
////            }
////        }
////    }
////
////    // Getters
////    public List<RouteSegment> getSegments() {
////        return segments;
////    }
////
////    public int getTotalTime() {
////        return totalTime;
////    }
////
////    public int getTotalCost() {
////        return totalCost;
////    }
////
////    public int getTransferCount() {
////        return transferCount;
////    }
////
////    // Utility method for display
////    public String getFormattedTotalTime() {
////        int hours = totalTime / 60;
////        int minutes = totalTime % 60;
////        return String.format("%dh %02dmin", hours, minutes);
////    }
////
////    public String getFormattedTotalCost() {
////        return totalCost + " KM";
////    }
////}