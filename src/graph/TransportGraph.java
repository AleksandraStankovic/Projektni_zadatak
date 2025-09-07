
package graph;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import generator.TransportDataGenerator;
import generator.TransportDataGenerator.Departure;
import java.util.HashMap;
import java.util.Map;
//import generator.TransportDataGenerator.Station;
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

	
	private void addNodes() {
		
		for (Station station : data.stations) {
			

		
			//sada dodajemo nodes samo jedna vrsta 
		  Node node = graph.addNode(station.getStationCode());
		   node.setAttribute("ui.label", station.getStationCode());
		    node.setAttribute("city", station.getCity());
//		    node.setAttribute("stationType", station.getType()); 
		    node.setAttribute("stationType", station.getType()); 
		    node.setAttribute("ui.class", station.getType()); // <-- this will apply the CSS automatically

		    

				//transferEdge.setAttribute("arrivalTime", 5);//same as min transfer time
				//arrivalTime bi onda trebao biti jedan min transfer timeu
				
				//transferEdge.setAttribute("duration", 0);//ovo ovdje mozda nije dobro!!!, mozda treba drugacije !
				//stavicemo da je 0, da ne traje, vec samo da imamo transferTime nista vise 
			}
		}
	

	private void addTransferEdges() {
	    Map<String, Station> busStations = new HashMap<>();
	    Map<String, Station> trainStations = new HashMap<>();

	    // Separate them by type
	    for (Station station : data.stations) {
	        if (station.getType().equals("autobus")) { // ✔ match what getType() actually returns
	            busStations.put(station.getCity(), station);
	        } else if (station.getType().equals("voz")) {
	            trainStations.put(station.getCity(), station);
	        }
	    }

	    // Add transfer edges (bus <-> train in same city)
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

 
	
	private void addEdges() {
		for (Departure departure : data.departures) {
			String fromStation = departure.from;
			String toCity = departure.to;
			LocalTime depTime = LocalTime.parse(departure.departureTime);
			int duration = departure.duration;
			LocalTime arrivalTime = depTime.plusMinutes(duration);
			
			
			String toStation = null;
			
//			for (Station station : data.stations) {
//				if (station.city.equals(toCity)) {
//					toStation = departure.type.equals("autobus") ? station.busStation : station.trainStation;
//					break;
//				}
//			}
			 for (Station station : data.stations) {
		            if (station.getCity().equals(toCity) && station.getType().equals(departure.type)) {
		                toStation = station.getStationCode(); // e.g. "A_0_1" or "Z_0_1"
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
						//edge.setAttribute("departureTime", departure.departureTime);
						edge.setAttribute("departureTime", depTime); //ne mijenja puno, isto atribut, samo u drugom tipu podataka
						edge.setAttribute("departureStr", depTime.toString()); //za printanje I guess //dodali smo samo jos jedan atribut koji predstavlja departure string zbog stampanja, da se ne mijenja ostatak koda
						//dodati ovjde jos atribut arrival time, da nam kasnije bude lakse
						edge.setAttribute("arrivalTime", arrivalTime);

						
						edge.setAttribute("duration", departure.duration);
						edge.setAttribute("price", departure.price);
						edge.setAttribute("minTransferTime", departure.minTransferTime);//minTransferTime u edgu koji povezuje stanice u različitin gradovima

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
	            "node { " + 
	            "   size: 20px; " + 
	            "   fill-mode: plain; " + 
	            "   text-size: 12; " + 
	            "   text-alignment: at-left; " + 
	            "   text-style: bold; " + 
	            "} " + 
	            "node.autobus { " +      // for bus stations
	            "   fill-color: #0F4B82; " + // light blue
	            "   shape: circle; " +        // round
	            "} " + 
	            "node.voz { " +          // for train stations
	            "   fill-color: #FFA500; " + // orange
	            "   shape: box; " +           // square
	            "} " +
	            "edge { " +
	            "   text-size: 12; " +
	            "   text-alignment: along; " +
	            "   text-background-mode: plain; " +
	            "   text-background-color: white; " +
	            "} " +
	            "edge.autobus { fill-color: #1976D2; size: 2px; } " +
	            "edge.voz { fill-color: #D84315; size: 3px; } " +
	            "edge.transfer { fill-color: black; size: 3px; shape: line; } " +
	            "node.nonOptimalNode { fill-color: lightgray; stroke-mode: plain; stroke-color: gray; stroke-width: 1px; } " +
	            "edge.nonOptimal { fill-color: lightgray; size: 2px; } " +
	            "edge.optimal { fill-color: green; size: 4px; } " +
	            "node.optimal { fill-color: purple; stroke-mode: plain; stroke-color: purple; stroke-width: 3px; }";

	    graph.setAttribute("ui.stylesheet", css);
	}


	public Graph buildGraph() {
		graph.clear();
		addNodes();
		addTransferEdges();
		addEdges();
		styleGraph();

		//ovo dole kasnije i nece trebati jer je samo stampanje
		System.out.println("Graph built successfully:");
		System.out.println("  Nodes: " + graph.getNodeCount());
		System.out.println("  Edges: " + graph.getEdgeCount());

		
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

		System.out.println("  Transfer edges: " + transferEdges);
		System.out.println("  Transport edges: " + transportEdges);

		return graph;
	}

	
	//OVO NE TREBA
	public void printGraphInfo() {
		System.out.println("=== GRAPH INFO ===");

		
		for (int i = 0; i < graph.getNodeCount(); i++) {
			Node node = graph.getNode(i);
			System.out.println("Node: " + node.getId() + ", City: " + node.getAttribute("city") + ", Type: "
					+ node.getAttribute("stationType"));
		}

		
		for (int i = 0; i < graph.getEdgeCount(); i++) {
			Edge edge = graph.getEdge(i);
			System.out.println("Edge: " + edge.getId() + ", Type: " + edge.getAttribute("type") + ", From: "
					+ edge.getSourceNode().getId() + ", To: " + edge.getTargetNode().getId() + ", Duration: "
					+ edge.getAttribute("duration"));
		}
	}
}
