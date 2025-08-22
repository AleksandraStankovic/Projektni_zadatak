
package graph;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import generator.TransportDataGenerator;
import generator.TransportDataGenerator.Departure;
import generator.TransportDataGenerator.Station;

public class TransportGraph {

	private MultiGraph graph;
	private TransportDataGenerator.TransportData data;

	public TransportGraph(TransportDataGenerator.TransportData data) {
		this.data = data;
		this.graph = new MultiGraph("Transport Network");
		System.setProperty("org.graphstream.ui", "swing");
	}

	private void addNodes() {
		for (Station station : data.stations) {
			// Add bus station node
			Node busNode = graph.addNode(station.busStation);
			busNode.setAttribute("ui.label", station.busStation);
			busNode.setAttribute("ui.class", "bus");
			busNode.setAttribute("city", station.city);
			busNode.setAttribute("stationType", "bus");

			// Add train station node
			Node trainNode = graph.addNode(station.trainStation);
			trainNode.setAttribute("ui.label", station.trainStation);
			trainNode.setAttribute("ui.class", "train");
			trainNode.setAttribute("city", station.city);
			trainNode.setAttribute("stationType", "train");

			// Add transfer edge between bus and train stations
			String transferEdgeId = station.busStation + "-" + station.trainStation;
			if (graph.getEdge(transferEdgeId) == null) {
				Edge transferEdge = graph.addEdge(transferEdgeId, station.busStation, station.trainStation, true);
				transferEdge.setAttribute("ui.class", "transfer");
				transferEdge.setAttribute("type", "transfer");
				transferEdge.setAttribute("minTransferTime", 5);
				transferEdge.setAttribute("price", 0);
				transferEdge.setAttribute("duration", 5);
			}
		}
	}

	private void addEdges() {
		for (Departure departure : data.departures) {
			String fromStation = departure.from;
			String toCity = departure.to;

			// Find the corresponding target station
			String toStation = null;
			for (Station station : data.stations) {
				if (station.city.equals(toCity)) {
					toStation = departure.type.equals("autobus") ? station.busStation : station.trainStation;
					break;
				}
			}

			if (toStation != null && !fromStation.equals(toStation)) {
				String edgeId = fromStation + "-" + toStation + "-" + departure.departureTime;

				if (graph.getEdge(edgeId) == null) {
					try {
						Edge edge = graph.addEdge(edgeId, fromStation, toStation, true);

						// Set all required attributes
						edge.setAttribute("type", departure.type);
						edge.setAttribute("departureTime", departure.departureTime);
						edge.setAttribute("duration", departure.duration);
						edge.setAttribute("price", departure.price);
						edge.setAttribute("minTransferTime", departure.minTransferTime);

						// Set UI class based on transport type
						String uiClass = departure.type.equals("autobus") ? "autobus" : "voz";
						edge.setAttribute("ui.class", uiClass);

					} catch (Exception e) {
						System.out.println("Error creating edge " + edgeId + ": " + e.getMessage());
					}
				}
			}
		}
	}

	private void styleGraph() {
		String css = "graph { padding: 50px; }" + 
				"node { " + "   size: 20px; " + 
				"   fill-mode: plain; " + "   text-size: 12; " + 
				"   text-alignment: at-left; " + "   text-style: bold; " + "} " + "node.bus { "
				+ "   fill-color: #4CAF50; " + 
				"   shape: circle; " + "} " + "node.train { " + "   fill-color: #FF9800; " + 
				"   shape: box; " + "} " + "edge { " + "   text-size: 12; " + "   text-alignment: along; "
				+ "   text-background-mode: plain; " + "   text-background-color: white; " + "} " + "edge.autobus { "
				+ "   fill-color: #1976D2; " + "   size: 2px; " + "} " + "edge.voz { " + "   fill-color: #D84315; "
				+ "   size: 3px; " + "} " + "edge.transfer { " + "   fill-color: black; " + "   size: 3px; "
				+ "   shape: line; " + "}"
				+ "node.nonOptimalNode { fill-color: lightgray; stroke-mode: plain; stroke-color: gray; stroke-width: 1px; }"
				+ "edge.nonOptimal { fill-color: lightgray; size: 2px; }" + "edge.optimal { " + "   fill-color: green; "
				+ "   size: 4px; " + "} " + "node.optimal { fill-color: purple; " + "   stroke-mode: plain; "
				+ "   stroke-color: purple; " + "   stroke-width: 3px; " + "} ";

		;

		graph.setAttribute("ui.stylesheet", css);
	}

	public Graph buildGraph() {
		graph.clear();
		addNodes();
		addEdges();
		styleGraph();

		// Debug: Print graph statistics using basic methods
		System.out.println("Graph built successfully:");
		System.out.println("  Nodes: " + graph.getNodeCount());
		System.out.println("  Edges: " + graph.getEdgeCount());

		// Count edges using the most basic approach
		int transferEdges = 0;
		int transportEdges = 0;

		// Use getEdge(i) method which should be available in all versions
		for (int i = 0; i < graph.getEdgeCount(); i++) {
			Edge edge = graph.getEdge(i);
			String type = (String) edge.getAttribute("type");
			if ("transfer".equals(type)) {
				transferEdges++;
			} else if (type != null) {
				transportEdges++;
			}
		}

		System.out.println("  Transfer edges: " + transferEdges);
		System.out.println("  Transport edges: " + transportEdges);

		return graph;
	}

	// Helper method for debugging using basic methods
	public void printGraphInfo() {
		System.out.println("=== GRAPH INFO ===");

		// Print nodes using getNode(i)
		for (int i = 0; i < graph.getNodeCount(); i++) {
			Node node = graph.getNode(i);
			System.out.println("Node: " + node.getId() + ", City: " + node.getAttribute("city") + ", Type: "
					+ node.getAttribute("stationType"));
		}

		// Print edges using getEdge(i)
		for (int i = 0; i < graph.getEdgeCount(); i++) {
			Edge edge = graph.getEdge(i);
			System.out.println("Edge: " + edge.getId() + ", Type: " + edge.getAttribute("type") + ", From: "
					+ edge.getSourceNode().getId() + ", To: " + edge.getTargetNode().getId() + ", Duration: "
					+ edge.getAttribute("duration"));
		}
	}
}
