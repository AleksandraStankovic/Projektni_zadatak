package graph;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;//vidjeli da kasnije uzezem samo ono sto mi treba

import generator.TransportDataGenerator;
import generator.TransportDataGenerator.Departure;
import generator.TransportDataGenerator.Station;

//ovdje cemo definisati sve potrebno za graf, da ga napravimo i ostalo
public class TransportGraph {

	private MultiGraph graph;
	private TransportDataGenerator.TransportData data; // podaci koji ce nam biti potrebni

	// konsturktor za konstruisanje grafa
	public TransportGraph(TransportDataGenerator.TransportData data)// uzece daja odnekud..
	{

		this.data = data;

		// init grafa, tj da instanciramo taj objekat
		this.graph = new MultiGraph("Transport Network");
		System.setProperty("org.graphstream.ui", "swing");// idk, sta god da je ovo
		// ovo dakle samo u konstruktoru, kasnije bildujemo graf

	}

	private void addNodes() {
		for (Station station : data.stations) {
			// dodajemo cvorove za buseve
			Node busNode = graph.addNode(station.busStation);
			busNode.setAttribute("ui.label", station.busStation);
			//busNode.setAttribute("type", "bus");
			busNode.setAttribute("ui.class", "bus");
			
			busNode.setAttribute("city", station.city);

			// dodajemo nodes for vozove, radimo ovako jer zelimo posebnu oznaku za type,a i
			// svakako svaka stanica ima posebnu bus i voz
			Node trainNode = graph.addNode(station.trainStation);
			trainNode.setAttribute("ui.label", station.trainStation);
			//trainNode.setAttribute("type", "train");
	        trainNode.setAttribute("ui.class", "train");  // Changed from type attribute
			trainNode.setAttribute("city", station.city);

			// odmah ovdje dodajemo i edges izmedju stanica u istom gradu jer svakako
			// prolazimo kroz sve stanice koje su u istom gradu i guesss.

			//idk valjda ovdje nesto nije dobro zbog stilizovanja idk...
//			Edge transferEdge = graph.addEdge(station.busStation + "-" + station.trainStation, station.busStation,
//					station.trainStation);// stavicemo za sad da je neusmejreno ?
//
//			transferEdge.setAttribute("type", "transfer");
//			transferEdge.setAttribute("transfer_time", 5); // i guess da cemo za sad ovako da ostavimo, provjeriti da li
//															// je okej to ovako
//			transferEdge.setAttribute("cost", 0); // i guess da i ovo treba?
			
			
			// Add transfer edge with class
	        Edge transferEdge = graph.addEdge(station.busStation + "-" + station.trainStation, 
	                                        station.busStation, station.trainStation, true);
	        transferEdge.setAttribute("ui.class", "transfer");
	        transferEdge.setAttribute("transfer_time", 5);
	        transferEdge.setAttribute("cost", 0);
		}

	}

	private void addEdges ()//za dodavanje ivica izmedju razlicith stanica u razlicitim gradovima
	{
		for(Departure departure : data.departures)//generisemo iz departures liste
		{
		     String from = departure.from;
	         String toStation = departure.to;//ovo je  oznaka grada
			
			//moramo iz oznake grada pretvoriti u stanicu tj dobiti odg stanicu (nekako, ne kontam ni ja full)
            // Find the corresponding station IDs
            String to = data.stations.stream()
                .filter(s -> s.city.equals(toStation))
                .findFirst()
                .map(s -> departure.type.equals("autobus") ? s.busStation : s.trainStation)
                .orElse(null);
            
            if (to != null) {
                String edgeId = from + "-" + to + "-" + departure.departureTime;
                
                //sad dodajemo ivice
                //prvo projveravamo da li postoji ili ne
                if(graph.getEdge(edgeId)==null)
                {
                	Edge edge = graph.addEdge(edgeId, from, to, true);//dodaje ivicu u graf
                	
                    edge.setAttribute("type", departure.type);
                    edge.setAttribute("departureTime", departure.departureTime);
                    edge.setAttribute("duration", departure.duration);
                    edge.setAttribute("price", departure.price);
                    edge.setAttribute("minTransferTime", departure.minTransferTime);
                    //dodavanje atributa
                    
                    // Set weight based on different criteria
                    edge.setAttribute("timeWeight", departure.duration);
                    edge.setAttribute("priceWeight", departure.price);
                    edge.setAttribute("transfersWeight", 1); // Each trip counts as 1
                    //skontati sta kasnije sa ovim zadnjim za br presjedanja
                	
                }
                	
                
            }
                
		}
		
	}

	
	
	private void styleGraph() {
	    // CSS styling for the graph - using class selectors instead of attribute selectors
	    String css = 
	        "node { " +
	        "   size: 15px; " +
	        "   fill-mode: plain; " +
	        "   text-size: 12; " +
	        "   text-alignment: above; " +
	        "} " +
	        "node.bus { " +  // Changed from node[type='bus']
	        "   fill-color: blue; " +
	        "   shape: circle; " +
	        "} " +
	        "node.train { " +  // Changed from node[type='train']
	        "   fill-color: red; " +
	        "   shape: box; " +
	        "} " +
	        "edge { " +
	        "   text-size: 10; " +
	        "   text-alignment: along; " +
	        "   text-background-mode: plain; " +
	        "   text-background-color: white; " +
	        "} " +
	        "edge.autobus { " +  // Changed from edge[type='autobus']
	        "   fill-color: blue; " +
	        "   size: 1px; " +
	        "} " +
	        "edge.voz { " +  // Changed from edge[type='voz']
	        "   fill-color: red; " +
	        "   size: 2px; " +
	        "} " +
	        "edge.transfer { " +
	        "   fill-color: gray; " +
	        "   size: 1px; " +
	        "   shape: line; " +
	        "}";
	    
	    graph.setAttribute("ui.stylesheet", css);
	}

	public Graph buildGraph()// metoda koja pravi graf tako sto ce dodati sve ivice, sve nodes i sve atribute
	{

		graph.clear();
		addNodes();
		addEdges();
		styleGraph();

		return graph;

	}

}
