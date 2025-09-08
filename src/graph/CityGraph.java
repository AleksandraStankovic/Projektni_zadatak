package graph;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import generator.TransportDataGenerator;
import generator.TransportDataGenerator.Departure;

import java.util.HashSet;
import java.util.Set;

import model.Station;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CityGraph {

	private MultiGraph graph;
	private TransportDataGenerator.TransportData data;

	public CityGraph(TransportDataGenerator.TransportData data) {
		this.data = data;
		this.graph = new MultiGraph("City Network");
		System.setProperty("org.graphstream.ui", "swing");
	}

	/**
	 * Adds city nodes to the graph based on the list of stations.
	 *
	 * <p>
	 * The method first collects all unique city names from the available stations
	 * and then creates a graph node for each city. Each node is labeled with the
	 * corresponding city name for visualization.
	 * </p>
	 *
	 * <p>
	 * This ensures that each city is represented only once in the graph, even if
	 * multiple stations belong to the same city.
	 * </p>
	 */
	private void addCityNodes() {
		Set<String> cities = new HashSet<>();

		for (Station station : data.stations) {
			cities.add(station.getCity());
		}

		for (String city : cities) {
			Node node = graph.addNode(city);
			node.setAttribute("ui.label", city);
		}
	}

    /**
     * Adds directed edges between cities in the graph based on departures.
     *
     * <p>
     * The method maps each station to its corresponding city and then
     * iterates through all departures. For each departure, it creates a
     * directed edge from the origin city to the destination city, ensuring
     * that duplicate edges (including reversed pairs) are not added.
     * </p>
     *
     * <p>
     * Each edge is annotated with attributes:
     * <ul>
     *   <li><b>type</b> – the transport type of the departure (for example, "autobus" or "voz").</li>
     *   <li><b>ui.class</b> – the same value, used to support graph visualization styling.</li>
     * </ul>
     * </p>
     *
     */

	private void addCityEdges() {
		Set<String> createdEdges = new HashSet<>();

		Map<String, String> stationToCity = new HashMap<>();
		for (Station station : data.stations) {
			stationToCity.put(station.getStationCode(), station.getCity());
		}

		for (Departure dep : data.departures) {
			String fromCity = stationToCity.get(dep.from);
			String toCity = dep.to;

			if (fromCity == null || fromCity.equals(toCity))
				continue;

			String edgeId = fromCity + "-" + toCity;

			if (!createdEdges.contains(edgeId) && !createdEdges.contains(toCity + "-" + fromCity)) {
				Edge edge = graph.addEdge(edgeId, fromCity, toCity, true);

				edge.setAttribute("type", dep.type);
				edge.setAttribute("ui.class", dep.type.equals("autobus") ? "autobus" : "voz");

				createdEdges.add(edgeId);
			}
		}
	}
    /**
     * Applies visual styling to the graph and its elements.
     *
     * <p>
     * A CSS stylesheet is defined and attached to the graph to control
     * the appearance of nodes and edges. The stylesheet specifies default
     * sizes, colors, text properties, and special styles for nodes and
     * edges marked as optimal or non-optimal.
     * </p>
     *
     * <p>
     * Key style rules include:
     * <ul>
     *   <li>Nodes: fixed size, blue fill color, centered bold text.</li>
     *   <li>Edges: gray color with no arrowheads.</li>
     *   <li>Optimal nodes and edges: highlighted with purple and green styles.</li>
     *   <li>Non-optimal nodes and edges: displayed in light gray.</li>
     * </ul>
     * </p>
     *
     * <p>
     * In addition, each node is labeled with its identifier for clarity
     * in the graph visualization.
     * </p>
     */

	private void styleGraph() {
		String css = "graph { padding: 50px; }" + "node {" + "   size: 30px;" + "   fill-color: #64B5F6;"
				+ "   text-size: 14;" + "   text-style: bold;" + "   text-alignment: center;" + "   text-color: black;"
				+ "} " + "edge {" + "   size: 2px;" + "   fill-color: gray;" + "   arrow-shape: none;" + "} " +

				"node.optimal {" + "   fill-color: #8E24AA;" + "   stroke-mode: plain;" + "   stroke-color: #6A1B9A;"
				+ "   stroke-width: 3px;" + "} " + "edge.optimal {" + "   fill-color: #43A047;" + "   size: 4px;" + "} "
				+ "node.nonOptimalNode {" + "   fill-color: lightgray;" + "   stroke-mode: plain;"
				+ "   stroke-color: gray;" + "   stroke-width: 1px;" + "} " + "edge.nonOptimal {"
				+ "   fill-color: lightgray;" + "   size: 1px;" + "} ";

		graph.setAttribute("ui.stylesheet", css);

		for (Node node : graph) {
			node.setAttribute("ui.label", node.getId());
		}
	}

	/// ovo nije dobro, sama implementirati logiku koja je potrebna
	public void highlightPath(List<String> cityPath) {

		for (Node node : graph.nodes().toList()) {
			node.setAttribute("ui.class", "nonOptimalNode");
		}
		for (Edge edge : graph.edges().toList()) {
			edge.setAttribute("ui.class", "nonOptimal");
		}

		if (cityPath == null || cityPath.isEmpty())
			return;

		for (String city : cityPath) {
			Node node = graph.getNode(city);
			if (node != null)
				node.setAttribute("ui.class", "optimal");
		}

		for (int i = 0; i < cityPath.size() - 1; i++) {
			String from = cityPath.get(i);
			String to = cityPath.get(i + 1);

			Edge edge = graph.getEdge(from + "-" + to);
			if (edge == null)
				edge = graph.getEdge(to + "-" + from);
			if (edge != null)
				edge.setAttribute("ui.class", "optimal");
		}
	}
    /**
     * Builds and returns the complete city graph.
     *
     * <p>
     * The method clears any existing content from the graph, then
     * sequentially performs the following steps:
     * </p>
     * <ul>
     *   <li>Adds nodes representing unique cities.</li>
     *   <li>Adds edges representing connections between cities based on departures.</li>
     *   <li>Applies visual styling to nodes and edges.</li>
     * </ul>
     *
     * <p>
     * After construction, the method prints the total number of nodes and
     * edges to the console for verification, and finally returns the
     * resulting graph instance.
     * </p>
     *
     * @return the fully constructed and styled graph representing cities
     *         and their connections
     */

	public Graph buildGraph() {
		graph.clear();
		addCityNodes();
		addCityEdges();
		styleGraph();

		System.out.println("City graph built successfully:");
		System.out.println("  Nodes: " + graph.getNodeCount());
		System.out.println("  Edges: " + graph.getEdgeCount());

		return graph;
	}

	public void displayGraph() {
		buildGraph();
		graph.display();
	}

}
