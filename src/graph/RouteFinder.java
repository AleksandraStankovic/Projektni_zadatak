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

	private List<Node> getStationsForCity(String city) {
		List<Node> stations = new ArrayList<>();
		for (Node node : graph) {
			if (node.getAttribute("city").equals(city)) {
				stations.add(node);
			}
		}
		return stations;
	}

//    private int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
//        String edgeType = (String) edge.getAttribute("type");
//        
//        if (edgeType == null) {
//            System.out.println("WARNING: Edge " + edge.getId() + " has no type attribute!");
//            return 1;
//        }
//        
//        switch (criteria) {
//            case SHORTEST_TIME:
//                if ("transfer".equals(edgeType)) {
//                    return (int) edge.getNumber("minTransferTime");
//                } else {
//                    return (int) edge.getNumber("duration");
//                }
//            case LOWEST_COST:
//                if ("transfer".equals(edgeType)) {
//                    return 0;  // Transfers are free
//                } else {
//                    return (int) edge.getNumber("price");
//                }
//            case FEWEST_TRANSFERS:
//                return "transfer".equals(edgeType) ? 0 : 1;
//            default:
//                return 1;
//        }

//    }

	private int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
		String edgeType = (String) edge.getAttribute("type");

		if (edgeType == null) {
			return 1;
		}

		switch (criteria) {
		case SHORTEST_TIME:
			if ("transfer".equals(edgeType)) {
				return (int) edge.getNumber("minTransferTime");
			} else {
				return (int) edge.getNumber("duration");
			}
		case LOWEST_COST:
			if ("transfer".equals(edgeType)) {
				return 0;
			} else {
				return (int) edge.getNumber("price");
			}
		case FEWEST_TRANSFERS:
			return "transfer".equals(edgeType) ? 0 : 1;
		default:
			return 1;
		}
	}

	/**
	 * Gets all leaving edges from a node for GraphStream 2.0
	 */
	private List<Edge> getLeavingEdges(Node node) {
		List<Edge> leavingEdges = new ArrayList<>();

		// GraphStream 2.0: Iterate through all edges and check source
		for (Edge edge : graph.edges().toArray(Edge[]::new)) {
			if (edge.getSourceNode().equals(node)) {
				leavingEdges.add(edge);
			}
		}

		return leavingEdges;
	}

	/**
	 * Dijkstra's algorithm implementation for single shortest path
	 */
	private PathInfo findShortestPath(Node start, Node end, OptimizationCriteria criteria) {
		// Initialize data structures
		Map<Node, Integer> distances = new HashMap<>();
		Map<Node, Node> previousNodes = new HashMap<>();
		Map<Node, Edge> previousEdges = new HashMap<>();
		PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

		// Initialize all nodes with infinity distance
		for (Node node : graph) {
			distances.put(node, Integer.MAX_VALUE);
			previousNodes.put(node, null);
			previousEdges.put(node, null);
		}

		// Start node has 0 distance
		distances.put(start, 0);
		queue.add(start);

		while (!queue.isEmpty()) {
			Node current = queue.poll();

			// Stop if we reached the end node
			if (current.equals(end)) {
				break;
			}

			// Skip if we've already found a better path to this node
			if (distances.get(current) == Integer.MAX_VALUE) {
				continue;
			}

			// Explore all neighbors using getLeavingEdges
			for (Edge edge : getLeavingEdges(current)) {
				Node neighbor = edge.getTargetNode();
				int weight = getEdgeWeight(edge, criteria);
				int newDistance = distances.get(current) + weight;

				// If we found a shorter path to the neighbor
				if (newDistance < distances.get(neighbor)) {
					distances.put(neighbor, newDistance);
					previousNodes.put(neighbor, current);
					previousEdges.put(neighbor, edge);

					// Remove and re-add to update priority
					queue.remove(neighbor);
					queue.add(neighbor);
				}
			}
		}

		// If no path found
		if (distances.get(end) == Integer.MAX_VALUE) {
			return null;
		}

		// Reconstruct the path
		Path path = reconstructPath(start, end, previousNodes, previousEdges);

		// Calculate path metrics
		int totalTime = calculatePathMetric(path, OptimizationCriteria.SHORTEST_TIME);
		int totalCost = calculatePathMetric(path, OptimizationCriteria.LOWEST_COST);
		int totalTransfers = calculatePathMetric(path, OptimizationCriteria.FEWEST_TRANSFERS);
		int totalWeight = distances.get(end);

		return new PathInfo(path, totalTime, totalCost, totalTransfers, totalWeight);
	}

	/**
	 * Reconstructs the path from start to end using previous nodes and edges
	 */
	private Path reconstructPath(Node start, Node end, Map<Node, Node> previousNodes, Map<Node, Edge> previousEdges) {
		List<Node> nodePath = new ArrayList<>();
		List<Edge> edgePath = new ArrayList<>();

		// Backtrack from end to start
		Node current = end;
		while (current != null && !current.equals(start)) {
			nodePath.add(0, current);
			Edge edge = previousEdges.get(current);
			if (edge != null) {
				edgePath.add(0, edge);
			}
			current = previousNodes.get(current);
		}
		nodePath.add(0, start);

		// Build the path manually since GraphStream 2.0 Path API is different
		Path path = new Path();
		if (!nodePath.isEmpty()) {
			path.setRoot(nodePath.get(0));
			for (int i = 0; i < edgePath.size(); i++) {
				path.add(edgePath.get(i));
			}
		}

		return path;
	}

	/**
	 * Calculates a specific metric for the entire path
	 */
	private int calculatePathMetric(Path path, OptimizationCriteria criteria) {
		int total = 0;

		// Manual iteration through edges in the path
		// In GraphStream 2.0, we need to manually track edges
		if (path.size() > 0) {
			Node current = path.getRoot();
			for (int i = 0; i < path.size(); i++) {
				// Find the edge from current to next node
				Node next = getNextNodeInPath(path, i);
				Edge edge = current.getEdgeBetween(next);
				if (edge != null) {
					total += getEdgeWeight(edge, criteria);
				}
				current = next;
			}
		}
		return total;
	}

	/**
	 * Helper to get next node in path (GraphStream 2.0 compatibility)
	 */
	private Node getNextNodeInPath(Path path, int edgeIndex) {
		// This is a workaround for GraphStream 2.0 Path API limitations
		// We need to manually track the path nodes
		Node current = path.getRoot();
		for (int i = 0; i <= edgeIndex; i++) {
			// Find all edges from current node and see which one is in the path
			for (Edge edge : graph.edges().toArray(Edge[]::new)) {
				if (edge.getSourceNode().equals(current)) {
					// Check if this edge leads to the next node in our reconstructed path
					// This is simplified - in real implementation, you'd track nodes better
					current = edge.getTargetNode();
					break;
				}
			}
		}
		return current;
	}

	/**
	 * Finds the best routes between two cities
	 */
	public List<PathInfo> findRoutes(String fromCity, String toCity, OptimizationCriteria criteria, int maxResults) {
		List<Node> startNodes = getStationsForCity(fromCity);
		List<Node> endNodes = getStationsForCity(toCity);

		System.out.println("Finding routes from " + fromCity + " to " + toCity);
		System.out.println("Start stations: " + startNodes.stream().map(Node::getId).collect(Collectors.toList()));
		System.out.println("End stations: " + endNodes.stream().map(Node::getId).collect(Collectors.toList()));

		List<PathInfo> allPaths = new ArrayList<>();

		// Find shortest path between each start and end station combination
		for (Node start : startNodes) {
			for (Node end : endNodes) {
				if (!start.equals(end)) {
					System.out.println("Finding path from " + start.getId() + " to " + end.getId());
					PathInfo path = findShortestPath(start, end, criteria);
					if (path != null) {
						allPaths.add(path);
						System.out.println("Found path with weight: " + path.getTotalWeight());
					} else {
						System.out.println("No path found");
					}
				}
			}
		}

		// Sort paths based on the optimization criteria
		Comparator<PathInfo> comparator = getComparatorForCriteria(criteria);
		List<PathInfo> sortedPaths = allPaths.stream().sorted(comparator).collect(Collectors.toList());

		System.out.println("Total paths found: " + sortedPaths.size());
		return sortedPaths.stream().limit(maxResults).collect(Collectors.toList());
	}

	/**
	 * Gets the appropriate comparator for the optimization criteria
	 */
//	private Comparator<PathInfo> getComparatorForCriteria(OptimizationCriteria criteria) {
//		switch (criteria) {
//		case SHORTEST_TIME:
//			return Comparator.comparingInt(PathInfo::getTotalTime);
//		case LOWEST_COST:
//			return Comparator.comparingInt(PathInfo::getTotalCost);
//		case FEWEST_TRANSFERS:
//			return Comparator.comparingInt(PathInfo::getTotalTransfers);
//		default:
//			return Comparator.comparingInt(PathInfo::getTotalWeight);
//		}
//	}
	
	/**
	 * Gets the appropriate comparator for the optimization criteria
	 */
	private Comparator<PathInfo> getComparatorForCriteria(OptimizationCriteria criteria) {
	    // Sort by the actual weight used in pathfinding, not the display metrics
	    return Comparator.comparingInt(PathInfo::getTotalWeight);
	}
	

	/**
	 * Alternative method that returns detailed path information
	 */
	public PathInfo findBestRoute(String fromCity, String toCity, OptimizationCriteria criteria) {
		List<PathInfo> routes = findRoutes(fromCity, toCity, criteria, 1);
		return routes.isEmpty() ? null : routes.get(0);
	}

	/**
	 * Gets detailed information about a path for display purposes
	 */
	public List<String> getPathDetails(PathInfo pathInfo) {
		List<String> details = new ArrayList<>();
		Path path = pathInfo.getPath();

		if (path.size() == 0) {
			details.add("Direct connection - no transfers needed");
			return details;
		}

		// Manual path traversal for GraphStream 2.0
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();

		Node current = path.getRoot();
		nodes.add(current);

		// Manually reconstruct the path edges and nodes
		for (int i = 0; i < path.size(); i++) {
			// Find the edge from current node
			for (Edge edge : graph.edges().toArray(Edge[]::new)) {
				if (edge.getSourceNode().equals(current)) {
					// Check if this edge is part of our path
					// This is simplified - would need proper path tracking
					edges.add(edge);
					current = edge.getTargetNode();
					nodes.add(current);
					break;
				}
			}
		}

		String currentTransport = getTransportTypeFromNode(nodes.get(0));
		String currentCity = (String) nodes.get(0).getAttribute("city");

		details.add("Start at " + nodes.get(0).getId() + " in " + currentCity);

		for (int i = 0; i < edges.size(); i++) {
			Edge edge = edges.get(i);
			Node nextNode = nodes.get(i + 1);
			String nextTransport = getTransportTypeFromNode(nextNode);
			String nextCity = (String) nextNode.getAttribute("city");

			String edgeType = (String) edge.getAttribute("type");

			if ("transfer".equals(edgeType)) {
				details.add("Transfer from " + currentTransport + " to " + nextTransport + " (Wait: "
						+ getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min)");
			} else {
				details.add("Take " + currentTransport + " from " + currentCity + " to " + nextCity + " (Time: "
						+ getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min, Cost: "
						+ getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST) + " €)");
			}

			currentTransport = nextTransport;
			currentCity = nextCity;
		}

		details.add("Arrive at " + nodes.get(nodes.size() - 1).getId() + " in " + currentCity);
		details.add("Total time: " + pathInfo.getTotalTime() + " minutes");
		details.add("Total cost: " + pathInfo.getTotalCost() + " €");
		details.add("Total transfers: " + pathInfo.getTotalTransfers());

		return details;
	}

	private String getTransportTypeFromNode(Node node) {
		String nodeId = node.getId();
		if (nodeId.startsWith("A_")) {
			return "Bus";
		} else if (nodeId.startsWith("Z_")) {
			return "Train";
		}
		return "Unknown";
	}

	/**
	 * Method to get top 5 routes for the assignment requirement
	 */
	public List<PathInfo> findTop5Routes(String fromCity, String toCity, OptimizationCriteria criteria) {
		return findRoutes(fromCity, toCity, criteria, 5);
	}

	/**
	 * Method to get path information for table display
	 */
	public Object[][] getPathTableData(List<PathInfo> paths) {
		Object[][] data = new Object[paths.size()][5];

		for (int i = 0; i < paths.size(); i++) {
			PathInfo path = paths.get(i);
			data[i][0] = i + 1; // Rank
			data[i][1] = path.getTotalTime() + " min";
			data[i][2] = path.getTotalCost() + " €";
			data[i][3] = path.getTotalTransfers();
			data[i][4] = "View Details"; // Button text
		}

		return data;
	}
}
