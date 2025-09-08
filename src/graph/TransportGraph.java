
package graph;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import generator.TransportDataGenerator;
import generator.TransportDataGenerator.Departure;
import java.util.HashMap;
import java.util.Map;

import model.Station;

import java.time.LocalTime;

public class TransportGraph {

	private MultiGraph graph;
	private TransportDataGenerator.TransportData data;

	public TransportGraph(TransportDataGenerator.TransportData data) {
		this.data = data;
		this.graph = new MultiGraph("Transport Network");
		System.setProperty("org.graphstream.ui", "swing");
	}

	/**
	 * Adds station nodes to the graph.
	 *
	 * <p>
	 * For each station in the data, a corresponding node is created with:
	 * </p>
	 * <ul>
	 * <li>Label set to the station code</li>
	 * <li>City attribute indicating the station's city</li>
	 * <li>Station type attribute (e.g., bus or train)</li>
	 * <li>UI class set for styling based on the station type</li>
	 * </ul>
	 *
	 * <p>
	 * This ensures that all stations are represented as nodes in the graph and can
	 * be styled and connected appropriately.
	 * </p>
	 */

	private void addNodes() {

		for (Station station : data.stations) {

			Node node = graph.addNode(station.getStationCode());
			node.setAttribute("ui.label", station.getStationCode());
			node.setAttribute("city", station.getCity());

			node.setAttribute("stationType", station.getType());
			node.setAttribute("ui.class", station.getType());

		}
	}

	/**
	 * Adds transfer edges between bus and train stations within the same city.
	 *
	 * <p>
	 * The method first separates stations by type (bus or train) and then iterates
	 * through each city to find matching bus and train stations. For each city with
	 * both types of stations, a directed edge representing a transfer is created if
	 * it does not already exist.
	 * </p>
	 *
	 * <p>
	 * Transfer edges are annotated with the following attributes:
	 * <ul>
	 * <li>type: "transfer"</li>
	 * <li>ui.class: "transfer" (for visualization)</li>
	 * <li>minTransferTime: 5 minutes</li>
	 * <li>price: 0</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * This ensures that passengers can transfer between bus and train stations in
	 * the same city within the graph representation.
	 * </p>
	 */

	private void addTransferEdges() {
		Map<String, Station> busStations = new HashMap<>();
		Map<String, Station> trainStations = new HashMap<>();

		for (Station station : data.stations) {
			if (station.getType().equals("autobus")) { // ✔ match what getType() actually returns
				busStations.put(station.getCity(), station);
			} else if (station.getType().equals("voz")) {
				trainStations.put(station.getCity(), station);
			}
		}

		for (String city : busStations.keySet()) {
			Station bus = busStations.get(city);
			Station train = trainStations.get(city);

			if (bus != null && train != null) {
				String transferEdgeId = bus.getStationCode() + "-" + train.getStationCode();
				if (graph.getEdge(transferEdgeId) == null) {
					Edge transfer = graph.addEdge(transferEdgeId, bus.getStationCode(), train.getStationCode(), true);
					transfer.setAttribute("type", "transfer");
					transfer.setAttribute("ui.class", "transfer");
					transfer.setAttribute("minTransferTime", 5);
					transfer.setAttribute("price", 0);
				}
			}
		}
	}

	/**
	 * Adds edges between stations based on departures data.
	 *
	 * <p>
	 * For each departure, the method identifies the source station and a matching
	 * destination station in the target city with the same transport type. It then
	 * creates a directed edge representing the departure if it does not already
	 * exist in the graph.
	 * </p>
	 *
	 * <p>
	 * Each edge is annotated with the following attributes:
	 * <ul>
	 * <li>type: transport type (bus or train)</li>
	 * <li>departureTime: departure time as a LocalTime object</li>
	 * <li>departureStr: departure time as a string</li>
	 * <li>arrivalTime: calculated arrival time</li>
	 * <li>duration: travel duration in minutes</li>
	 * <li>price: ticket price</li>
	 * <li>minTransferTime: minimum transfer time required</li>
	 * <li>ui.class: for visualization based on transport type</li>
	 * </ul>
	 * </p>
	 *
	 * <p>
	 * This ensures the graph accurately reflects all scheduled departures between
	 * stations, including timing, cost, and type of transport.
	 * </p>
	 */

	private void addEdges() {
		for (Departure departure : data.departures) {
			String fromStation = departure.from;
			String toCity = departure.to;
			LocalTime depTime = LocalTime.parse(departure.departureTime);
			int duration = departure.duration;
			LocalTime arrivalTime = depTime.plusMinutes(duration);

			String toStation = null;

			for (Station station : data.stations) {
				if (station.getCity().equals(toCity) && station.getType().equals(departure.type)) {
					toStation = station.getStationCode();
					break;
				}
			}

			if (toStation != null && !fromStation.equals(toStation)) {
				String edgeId = fromStation + "-" + toStation + "-" + departure.departureTime;

				if (graph.getEdge(edgeId) == null) {
					try {
						Edge edge = graph.addEdge(edgeId, fromStation, toStation, true);

						edge.setAttribute("type", departure.type);

						edge.setAttribute("departureTime", depTime);
						edge.setAttribute("departureStr", depTime.toString());
						edge.setAttribute("arrivalTime", arrivalTime);

						edge.setAttribute("duration", departure.duration);
						edge.setAttribute("price", departure.price);
						edge.setAttribute("minTransferTime", departure.minTransferTime);// minTransferTime u edgu koji
																						// povezuje stanice u različitin
																						// gradovima

						String uiClass = departure.type.equals("autobus") ? "autobus" : "voz";
						edge.setAttribute("ui.class", uiClass);

					} catch (Exception e) {
						System.out.println("Error creating edge " + edgeId + ": " + e.getMessage());
					}
				}
			}
		}
	}

	/**
	 * Applies visual styling to the graph nodes and edges.
	 *
	 * <p>
	 * This method defines a CSS stylesheet for the graph to control the appearance
	 * of nodes and edges, including default styles and special classes for
	 * different types of transport and optimal/non-optimal elements.
	 * </p>
	 *
	 *
	 *
	 * <p>
	 * The stylesheet is applied to the graph using the "ui.stylesheet" attribute.
	 * </p>
	 */

	private void styleGraph() {
		String css = "graph { padding: 50px; }" + "node { " + "   size: 20px; " + "   fill-mode: plain; "
				+ "   text-size: 12; " + "   text-alignment: at-left; " + "   text-style: bold; " + "} "
				+ "node.autobus { " + "   fill-color: #0F4B82; " + "   shape: circle; " + "} " + "node.voz { "
				+ "   fill-color: #FFA500; " + "   shape: box; " + "} " + "edge { " + "   text-size: 12; "
				+ "   text-alignment: along; " + "   text-background-mode: plain; "
				+ "   text-background-color: white; " + "} " + "edge.autobus { fill-color: #1976D2; size: 2px; } "
				+ "edge.voz { fill-color: #D84315; size: 3px; } "
				+ "edge.transfer { fill-color: black; size: 3px; shape: line; } "
				+ "node.nonOptimalNode { fill-color: lightgray; stroke-mode: plain; stroke-color: gray; stroke-width: 1px; } "
				+ "edge.nonOptimal { fill-color: lightgray; size: 2px; } "
				+ "edge.optimal { fill-color: green; size: 4px; } "
				+ "node.optimal { fill-color: purple; stroke-mode: plain; stroke-color: purple; stroke-width: 3px; }";

		graph.setAttribute("ui.stylesheet", css);
	}

	/**
	 * Constructs and returns a complete graph representing stations and
	 * connections.
	 *
	 * <p>
	 * The method performs the following steps:
	 * </p>
	 * <ol>
	 * <li>Clears any existing nodes and edges from the graph.</li>
	 * <li>Adds nodes for each station in the data set.</li>
	 * <li>Adds transfer edges connecting bus and train stations within the same
	 * city.</li>
	 * <li>Adds edges representing scheduled departures between stations.</li>
	 * <li>Applies visual styling to nodes and edges.</li>
	 * </ol>
	 *
	 * <p>
	 * After construction, the method counts the number of transfer edges and
	 * transport edges (bus or train) in the graph.
	 * </p>
	 *
	 * @return the fully constructed and styled graph representing all stations,
	 *         transfers, and transport connections
	 */

	public Graph buildGraph() {
		graph.clear();
		addNodes();
		addTransferEdges();
		addEdges();
		styleGraph();

		int transferEdges = 0;
		int transportEdges = 0;

		for (int i = 0; i < graph.getEdgeCount(); i++) {
			Edge edge = graph.getEdge(i);
			String type = (String) edge.getAttribute("type");
			if ("transfer".equals(type)) {
				transferEdges++;
			} else if (type != null) {
				transportEdges++;
			}
		}

		return graph;
	}

}
