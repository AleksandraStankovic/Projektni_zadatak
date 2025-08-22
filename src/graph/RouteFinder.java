package graph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;

import model.OptimizationCriteria;
import model.PathInfo;

public class RouteFinder {

    private final MultiGraph graph;

    public RouteFinder(MultiGraph graph) {
        this.graph = graph;
    }

    public List<Node> getStationsForCity(String city) {
        List<Node> stations = new ArrayList<>();
        for (Node node : graph) {
            if (node.getAttribute("city") != null && 
                node.getAttribute("city").equals(city)) {
                stations.add(node);
            }
        }
        return stations;
    }

    public int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
        String edgeType = (String) edge.getAttribute("type");
        
        if (edgeType == null) return 1;
        
        switch (criteria) {
            case SHORTEST_TIME:
                return "transfer".equals(edgeType) ? 
                    (int) edge.getNumber("minTransferTime") : 
                    (int) edge.getNumber("duration");
            case LOWEST_COST:
                return "transfer".equals(edgeType) ? 0 : (int) edge.getNumber("price");
            case FEWEST_TRANSFERS:
                return "transfer".equals(edgeType) ? 0 : 1;
            default:
                return 1;
        }
    }

  
   public  PathInfo dijkstraShortestPath(Node start, Node end, OptimizationCriteria criteria) {
        
        class NodeDistance implements Comparable<NodeDistance> {
            Node node;
            int distance;
            NodeDistance(Node node, int distance) {
                this.node = node;
                this.distance = distance;
            }
            @Override
            public int compareTo(NodeDistance other) {
                return Integer.compare(this.distance, other.distance);
            }
        }

        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        Map<Node, Edge> previousEdges = new HashMap<>();
        PriorityQueue<NodeDistance> queue = new PriorityQueue<>();
        
        
        for (Node node : graph) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        queue.add(new NodeDistance(start, 0));
        
        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            Node currentNode = current.node;
            
            if (currentNode.equals(end)) break;
            if (current.distance > distances.get(currentNode)) continue;
            
            for (Edge edge : currentNode.leavingEdges().toArray(Edge[]::new)) {
                Node neighbor = edge.getTargetNode();
                int weight = getEdgeWeight(edge, criteria);
                int newDistance = distances.get(currentNode) + weight;
                
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousNodes.put(neighbor, currentNode);
                    previousEdges.put(neighbor, edge);
                    queue.add(new NodeDistance(neighbor, newDistance));
                }
            }
        }
        
        if (distances.get(end) == Integer.MAX_VALUE) return null;
        
        // Reconstruct path PROPERLY
        Path path = new Path();
        List<Edge> edgePath = new ArrayList<>();
        
        Node current = end;
        while (!current.equals(start)) {
            Edge edge = previousEdges.get(current);
            if (edge == null) break;
            edgePath.add(0, edge);
            current = previousNodes.get(current);
        }
        
        path.setRoot(start);
        for (Edge edge : edgePath) {
            path.add(edge);
        }
        
        // Calculate metrics PROPERLY
        int totalTime = 0, totalCost = 0, totalTransfers = 0;
        for (Edge edge : path.getEdgePath()) {
            totalTime += getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME);
            totalCost += getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST);
            totalTransfers += getEdgeWeight(edge, OptimizationCriteria.FEWEST_TRANSFERS);
        }
        
        return new PathInfo(path, totalTime, totalCost, totalTransfers, distances.get(end));
    }



    /**
     * Get detailed path information (FIXED)
     */
    public List<String> getPathDetails(PathInfo pathInfo) {
        List<String> details = new ArrayList<>();
        Path path = pathInfo.getPath();
        
        if (path.getEdgeCount() == 0) {
            details.add("Direct connection");
            return details;
        }
        
        details.add("Total: " + pathInfo.getTotalTime() + " min, " + 
                   pathInfo.getTotalCost() + " €, " + 
                   pathInfo.getTotalTransfers() + " transfers");
        
        Node current = path.getRoot();
        for (Edge edge : path.getEdgePath()) {
            Node next = edge.getTargetNode();
            String edgeType = (String) edge.getAttribute("type");
            
            if ("transfer".equals(edgeType)) {
                details.add("Transfer: " + current.getId() + " → " + next.getId() + 
                          " (" + getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min wait)");
            } else {
                String transport = current.getId().startsWith("A_") ? "Bus" : "Train";
                details.add(transport + ": " + current.getId() + " → " + next.getId() + 
                          " (" + getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min, " +
                          getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST) + " €)");
            }
            current = next;
        }
        
        return details;
    }

    /**
     * For top 5 routes requirement
     */

}