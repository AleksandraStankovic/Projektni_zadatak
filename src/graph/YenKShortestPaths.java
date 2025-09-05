package graph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;
import model.OptimizationCriteria;
import model.PathInfo;

import java.util.*;

public class YenKShortestPaths {

    //private final MultiGraph graph;
    private final RouteFinder routeFinder;

    public YenKShortestPaths(MultiGraph graph) {
        //this.graph = graph;
        this.routeFinder = new RouteFinder(graph);
    }

    /**
     * Find top K shortest paths from fromCity to toCity
     */
    public List<PathInfo> findTopKPaths(String fromCity, String toCity, OptimizationCriteria criteria, int K) {
        List<Node> startNodes = routeFinder.getStationsForCity(fromCity);
        List<Node> endNodes = routeFinder.getStationsForCity(toCity);

        List<PathInfo> resultPaths = new ArrayList<>();
        PriorityQueue<PathInfo> candidates = new PriorityQueue<>(Comparator.comparingInt(p -> getWeight(p, criteria)));

        
        for (Node start : startNodes) {
            for (Node end : endNodes) {
                if (!start.equals(end)) {
                    PathInfo shortest = routeFinder.dijkstraShortestPath(start, end, criteria);
                    if (shortest != null) {
                        candidates.add(shortest);
                    }
                }
            }
        }

        if (candidates.isEmpty()) return resultPaths;

      
        resultPaths.add(candidates.poll());

        
        while (resultPaths.size() < K && !candidates.isEmpty()) {
            PathInfo lastPath = resultPaths.get(resultPaths.size() - 1);
            Path lastGraphPath = lastPath.getPath();
            List<Node> nodePath = lastGraphPath.getNodePath();

            for (int i = 0; i < nodePath.size() - 1; i++) {
                Node spurNode = nodePath.get(i);

               
                List<Edge> rootEdges = new ArrayList<>(lastGraphPath.getEdgePath().subList(0, i));

                
                Set<Edge> removedEdges = new HashSet<>();
                for (PathInfo p : resultPaths) {
                    List<Edge> edgePath = p.getPath().getEdgePath();
                    boolean matchesRoot = true;
                    for (int j = 0; j < rootEdges.size(); j++) {
                        if (!rootEdges.get(j).equals(edgePath.get(j))) {
                            matchesRoot = false;
                            break;
                        }
                    }
                    if (matchesRoot && edgePath.size() > i) {
                        Edge edgeToRemove = edgePath.get(i);
                        removedEdges.add(edgeToRemove);
                        edgeToRemove.setAttribute("disabled", true); // mark as disabled
                    }
                }

               
                Node targetNode = nodePath.get(nodePath.size() - 1);
                PathInfo spurPathInfo = routeFinder.dijkstraShortestPath(spurNode, targetNode, criteria);

                if (spurPathInfo != null) {
                    
                    Path combinedPath = new Path();
                    combinedPath.setRoot(nodePath.get(0));
                    for (Edge e : rootEdges) combinedPath.add(e);
                    for (Edge e : spurPathInfo.getPath().getEdgePath()) combinedPath.add(e);

                   
                    int totalTime = 0, totalCost = 0, totalTransfers = 0;
                    for (Edge e : combinedPath.getEdgePath()) {
                        totalTime += routeFinder.getEdgeWeight(e, OptimizationCriteria.SHORTEST_TIME);
                        totalCost += routeFinder.getEdgeWeight(e, OptimizationCriteria.LOWEST_COST);
                        totalTransfers += routeFinder.getEdgeWeight(e, OptimizationCriteria.FEWEST_TRANSFERS);
                    }

                    PathInfo candidate = new PathInfo(combinedPath, totalTime, totalCost, totalTransfers, totalTime);
                    if (!candidates.contains(candidate) && !resultPaths.contains(candidate)) {
                        candidates.add(candidate);
                    }
                }

                
                for (Edge e : removedEdges) {
                    e.removeAttribute("disabled");
                }
            }

            if (!candidates.isEmpty()) {
                resultPaths.add(candidates.poll());
            } else {
                break;
            }
        }

        return resultPaths;
    }

    private int getWeight(PathInfo path, OptimizationCriteria criteria) {
        return switch (criteria) {
            case SHORTEST_TIME -> path.getTotalTime();
            case LOWEST_COST -> path.getTotalCost();
            case FEWEST_TRANSFERS -> path.getTotalTransfers();
        };
    }
    
    
    

}
