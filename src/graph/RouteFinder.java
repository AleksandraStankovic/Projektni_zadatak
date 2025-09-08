package graph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;

import model.OptimizationCriteria;
import model.PathInfo;
import java.time.LocalTime;

public class RouteFinder {

	private final MultiGraph graph;

	public RouteFinder(MultiGraph graph) {
		this.graph = graph;
	}

	/**
	 * Retrieves all station nodes that belong to a given city.
	 *
	 * <p>
	 * The method iterates over all nodes in the graph and collects those that have
	 * a "city" attribute matching the specified city name.
	 * </p>
	 *
	 * @param city the name of the city whose stations should be retrieved
	 * @return a list of nodes representing the stations in the specified city; an
	 *         empty list if no stations are found
	 */

	public List<Node> getStationsForCity(String city) {
		List<Node> stations = new ArrayList<>();
		for (Node node : graph) {
			if (node.getAttribute("city") != null && node.getAttribute("city").equals(city)) {
				stations.add(node);
			}
		}
		return stations;

	}

	/**
	 * Calculates the weight of an edge based on the selected optimization criteria.
	 *
	 * <p>
	 * The weight is derived from edge attributes such as duration, price, or
	 * transfer time, depending on the optimization strategy. If the edge type is
	 * not specified, a default weight of 1 is returned.
	 * </p>
	 *
	 *
	 * @param edge     the edge whose weight is to be calculated
	 * @param criteria the optimization criteria to apply when determining the
	 *                 weight
	 * @return the calculated weight of the edge, or 1 if no matching rule applies
	 */

	public int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
		String edgeType = (String) edge.getAttribute("type");

		if (edgeType == null)
			return 1;

		switch (criteria) {
		case SHORTEST_TIME:
			return "transfer".equals(edgeType) ? (int) edge.getNumber("minTransferTime")
					: (int) edge.getNumber("duration");
		case LOWEST_COST:
			return "transfer".equals(edgeType) ? 0 : (int) edge.getNumber("price");
		case FEWEST_TRANSFERS:

			return 1;
		default:
			return 1;
		}

	}

	/**
	 * Finds the shortest path between two nodes using Dijkstra's algorithm based on
	 * the specified optimization criteria.
	 *
	 * <p>
	 * The method considers edge weights according to the given criteria, which can
	 * be shortest time, lowest cost, or fewest transfers. It also accounts for
	 * transfer times and departure/arrival schedules when computing earliest
	 * reachable times for each node.
	 * </p>
	 *
	 *
	 * 
	 * <p>
	 * Once the target node is reached or the queue is empty, the method
	 * reconstructs the path and calculates the total time, cost, and number of
	 * transfers along it. If the destination is unreachable, {@code null} is
	 * returned.
	 * </p>
	 *
	 * @param start    the starting node of the path
	 * @param end      the target node of the path
	 * @param criteria the optimization criteria to determine edge weights
	 * @return a PathInfo object containing the computed path, total time, total
	 *         cost, total transfers, and final distance, or null if no path exists
	 *         between the nodes
	 */

	public PathInfo dijkstraShortestPath(Node start, Node end, OptimizationCriteria criteria) {

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
		LocalTime arrivalTime;

		for (Node node : graph) {
			distances.put(node, Integer.MAX_VALUE);
		}
		distances.put(start, 0);
		queue.add(new NodeDistance(start, 0));

		int minTransferTime;

		LocalTime prevEarliest;
		LocalTime startDepartureTime = LocalTime.of(8, 0); // 8:00 AM

		LocalTime earliestDepartureTime = startDepartureTime;

		Map<Node, LocalTime> arrivalTimes = new HashMap<>();
		arrivalTimes.put(start, startDepartureTime);

		while (!queue.isEmpty()) {
			NodeDistance current = queue.poll();
			Node currentNode = current.node;

			Edge edgeIntoCurrent = previousEdges.get(currentNode);// ivica koja je dovela u trenutni najbolji I guess

			if (currentNode.equals(end))
				break;
			if (current.distance > distances.get(currentNode))
				continue;

			if (edgeIntoCurrent == null) {

				earliestDepartureTime = startDepartureTime;
			} else if (edgeIntoCurrent != null) {

				String edgeType = (String) edgeIntoCurrent.getAttribute("type");

				if ("transfer".equals(edgeType)) {
					minTransferTime = (int) edgeIntoCurrent.getAttribute("minTransferTime");
					prevEarliest = arrivalTimes.get(previousNodes.get(currentNode));
					earliestDepartureTime = prevEarliest.plusMinutes(minTransferTime);

				} else {

					arrivalTime = (LocalTime) edgeIntoCurrent.getAttribute("arrivalTime");
					minTransferTime = (int) edgeIntoCurrent.getAttribute("minTransferTime");
					earliestDepartureTime = arrivalTime.plusMinutes(minTransferTime);
				}
				arrivalTimes.put(currentNode, earliestDepartureTime);
			}

			for (Edge edge : currentNode.leavingEdges().toArray(Edge[]::new)) {
				Node neighbor = edge.getTargetNode();
				int weight = getEdgeWeight(edge, criteria);
				int newDistance = distances.get(currentNode) + weight;

				LocalTime deptTime = (LocalTime) edge.getAttribute("departureTime");

				LocalTime earliestForThisEdge = arrivalTimes.getOrDefault(currentNode, startDepartureTime);

				Integer minTransfer = (Integer) edge.getAttribute("minTransferTime");
				if (minTransfer != null) {
					earliestForThisEdge = earliestForThisEdge.plusMinutes(minTransfer);
				}

				if (deptTime != null && deptTime.isBefore(earliestForThisEdge)) {
					System.out.println("Skipping edge " + edge.getId() + " (dep " + deptTime + " < earliest "
							+ earliestForThisEdge + ")");
					continue;
				}

				LocalTime arrTime = (LocalTime) edge.getAttribute("arrivalTime");
				if (arrTime != null) {
					arrivalTimes.put(neighbor, arrTime);
				}

				if (newDistance < distances.get(neighbor)) {
					distances.put(neighbor, newDistance);
					previousNodes.put(neighbor, currentNode);
					previousEdges.put(neighbor, edge);
					queue.add(new NodeDistance(neighbor, newDistance));
				}
			}

		}

		if (distances.get(end) == Integer.MAX_VALUE)
			return null;

		Path path = new Path();
		List<Edge> edgePath = new ArrayList<>();

		Node current = end;
		while (!current.equals(start)) {
			Edge edge = previousEdges.get(current);
			if (edge == null)
				break;
			edgePath.add(0, edge);
			current = previousNodes.get(current);
		}

		path.setRoot(start);
		for (Edge edge : edgePath) {
			path.add(edge);
		}

		int totalTime = 0, totalCost = 0, totalTransfers = 0;
		for (Edge edge : path.getEdgePath()) {
			totalTime += getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME);
			totalCost += getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST);
			totalTransfers += getEdgeWeight(edge, OptimizationCriteria.FEWEST_TRANSFERS);
		}

		return new PathInfo(path, totalTime, totalCost, totalTransfers, distances.get(end));
	}

	/**
	 * Generates a list of human-readable strings describing the path.
	 *
	 * <p>
	 * For each edge in the provided {@link PathInfo}, the method creates a
	 * descriptive string indicating the type of movement (transfer, bus, or train),
	 * the nodes involved, the travel time, cost, and number of transfers where
	 * applicable.
	 * </p>
	 *
	 * <p>
	 * If the path has no edges, a single entry "Direct connection" is returned.
	 * Otherwise, the first entry summarizes the total time, cost, and transfers for
	 * the entire path.
	 * </p>
	 *
	 * @param pathInfo the PathInfo object containing the path and aggregated
	 *                 statistics
	 * @return a list of strings providing step-by-step details of the path
	 */

	public List<String> getPathDetails(PathInfo pathInfo) {
		List<String> details = new ArrayList<>();
		Path path = pathInfo.getPath();

		if (path.getEdgeCount() == 0) {
			details.add("Direct connection");
			return details;
		}

		details.add("Total: " + pathInfo.getTotalTime() + " min, " + pathInfo.getTotalCost() + " €, "
				+ pathInfo.getTotalTransfers() + " transfers");

		Node current = path.getRoot();
		for (Edge edge : path.getEdgePath()) {
			Node next = edge.getTargetNode();
			String edgeType = (String) edge.getAttribute("type");

			if ("transfer".equals(edgeType)) {
				details.add("Transfer: " + current.getId() + " → " + next.getId() + " ("
						+ getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min wait)");
			} else {
				String transport = current.getId().startsWith("A_") ? "Bus" : "Train";
				details.add(transport + ": " + current.getId() + " → " + next.getId() + " ("
						+ getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min, "
						+ getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST) + " €)");
			}
			current = next;
		}

		return details;
	}

	/**
	 * Extracts the sequence of cities from a given path.
	 *
	 * <p>
	 * The method iterates through all edges in the provided PathInfo and collects
	 * the city names of the source and target nodes. Each city appears only once in
	 * the returned list, preserving the order of traversal.
	 * </p>
	 *
	 * <p>
	 * If the pathInfo or its path is null, an empty list is returned.
	 * </p>
	 *
	 * @param pathInfo the PathInfo object containing the path
	 * @return a list of city names representing the traversal order along the path
	 */

	public List<String> getCityPath(PathInfo pathInfo) {
		List<String> cityPath = new ArrayList<>();
		if (pathInfo == null || pathInfo.getPath() == null)
			return cityPath;

		for (Edge edge : pathInfo.getPath().getEdgePath()) {
			Node from = edge.getSourceNode();
			Node to = edge.getTargetNode();
			String fromCity = (String) from.getAttribute("city");
			String toCity = (String) to.getAttribute("city");

			if (!cityPath.contains(fromCity))
				cityPath.add(fromCity);
			if (!cityPath.contains(toCity))
				cityPath.add(toCity);
		}
		return cityPath;
	}

}