package graph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;
import java.util.stream.Collectors;
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
     * SIMPLIFIED method to find top routes (without complex Yen's algorithm)
     */
    public List<PathInfo> findRoutes(String fromCity, String toCity, OptimizationCriteria criteria, int maxResults) {
        List<Node> startNodes = getStationsForCity(fromCity);
        List<Node> endNodes = getStationsForCity(toCity);
        
        List<PathInfo> allPaths = new ArrayList<>();
        
        // Find paths between all station combinations
        for (Node start : startNodes) {
            for (Node end : endNodes) {
                if (!start.equals(end)) {
                    PathInfo path = dijkstraShortestPath(start, end, criteria);
                    if (path != null) {
                        allPaths.add(path);
                    }
                }
            }
        }
        
        // Sort by the actual criteria
        Comparator<PathInfo> comparator = switch (criteria) {
            case SHORTEST_TIME -> Comparator.comparingInt(PathInfo::getTotalTime);
            case LOWEST_COST -> Comparator.comparingInt(PathInfo::getTotalCost);
            case FEWEST_TRANSFERS -> Comparator.comparingInt(PathInfo::getTotalTransfers);
        };
        
        return allPaths.stream()
                .sorted(comparator)
                .limit(maxResults)
                .collect(Collectors.toList());
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
    public List<PathInfo> findTop5Routes(String fromCity, String toCity, OptimizationCriteria criteria) {
        return findRoutes(fromCity, toCity, criteria, 5);
    }

    /**
     * Table data for GUI
     */
    public Object[][] getPathTableData(List<PathInfo> paths) {
        Object[][] data = new Object[paths.size()][5];
        for (int i = 0; i < paths.size(); i++) {
            PathInfo path = paths.get(i);
            data[i][0] = i + 1;
            data[i][1] = path.getTotalTime() + " min";
            data[i][2] = path.getTotalCost() + " €";
            data[i][3] = path.getTotalTransfers();
            data[i][4] = "View Details";
        }
        return data;
    }
}