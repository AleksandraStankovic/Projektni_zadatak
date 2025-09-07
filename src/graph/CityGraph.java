package graph;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import generator.TransportDataGenerator;
import generator.TransportDataGenerator.Departure;
import model.PathInfo;



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

    private void addCityNodes() {
        Set<String> cities = new HashSet<>();
        // Add cities from stations
        for (Station station : data.stations) {
            cities.add(station.getCity());
        }

        for (String city : cities) {
            Node node = graph.addNode(city);
            node.setAttribute("ui.label", city);
        }
    }

    private void addCityEdges() {
        Set<String> createdEdges = new HashSet<>();

        // Map station code to city
        Map<String, String> stationToCity = new HashMap<>();
        for (Station station : data.stations) {
            stationToCity.put(station.getStationCode(), station.getCity());
        }

        for (Departure dep : data.departures) {
            String fromCity = stationToCity.get(dep.from);  // map station to city
            String toCity = dep.to;  // already city

            if (fromCity == null || fromCity.equals(toCity)) continue;

            String edgeId = fromCity + "-" + toCity;

            if (!createdEdges.contains(edgeId) && !createdEdges.contains(toCity + "-" + fromCity)) {
                Edge edge = graph.addEdge(edgeId, fromCity, toCity, true);

                // Example: set minimal attributes, can be extended
                edge.setAttribute("type", dep.type);
                edge.setAttribute("ui.class", dep.type.equals("autobus") ? "autobus" : "voz");

                createdEdges.add(edgeId);
            }
        }
    }




    private void styleGraph() {
        String css =
            "graph { padding: 50px; }" +
            "node {" +
            "   size: 30px;" +
            "   fill-color: #64B5F6;" +   // default light blue
            "   text-size: 14;" +
            "   text-style: bold;" +
            "   text-alignment: center;" +
            "   text-color: black;" +
            "} " +
            "edge {" +
            "   size: 2px;" +
            "   fill-color: gray;" +
            "   arrow-shape: none;" +
            "} " +

            /* Highlighted classes */
            "node.optimal {" +
            "   fill-color: #8E24AA;" +   // purple for optimal nodes
            "   stroke-mode: plain;" +
            "   stroke-color: #6A1B9A;" +
            "   stroke-width: 3px;" +
            "} " +
            "edge.optimal {" +
            "   fill-color: #43A047;" +   // green for optimal edges
            "   size: 4px;" +
            "} " +
            "node.nonOptimalNode {" +
            "   fill-color: lightgray;" +
            "   stroke-mode: plain;" +
            "   stroke-color: gray;" +
            "   stroke-width: 1px;" +
            "} " +
            "edge.nonOptimal {" +
            "   fill-color: lightgray;" +
            "   size: 1px;" +
            "} ";

        graph.setAttribute("ui.stylesheet", css);

        // Ensure labels are visible
        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }
    }


    ///ovo nije dobro, sama implementirati logiku koja je potrebna 
    public void highlightPath(List<String> cityPath) {
        // Reset all styles
        for (Node node : graph.nodes().toList()) {
            node.setAttribute("ui.class", "nonOptimalNode");
        }
        for (Edge edge : graph.edges().toList()) {
            edge.setAttribute("ui.class", "nonOptimal");
        }

        if (cityPath == null || cityPath.isEmpty()) return;

        // Highlight nodes
        for (String city : cityPath) {
            Node node = graph.getNode(city);
            if (node != null) node.setAttribute("ui.class", "optimal");
        }

        // Highlight edges between consecutive cities
        for (int i = 0; i < cityPath.size() - 1; i++) {
            String from = cityPath.get(i);
            String to = cityPath.get(i + 1);

            Edge edge = graph.getEdge(from + "-" + to);
            if (edge == null) edge = graph.getEdge(to + "-" + from); // in case it's reversed
            if (edge != null) edge.setAttribute("ui.class", "optimal");
        }
    }






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
