package controller;

import generator.TransportDataGenerator;
import org.graphstream.graph.implementations.MultiGraph;
import graph.TransportGraph;
import graph.RouteFinder;
import model.RouteDetails;
import model.OptimizationCriteria;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import java.util.List;
import java.util.ArrayList;
import model.PathInfo;
import graph.YenKShortestPaths;
import org.graphstream.graph.Node;


public class RouteController {

	private TransportDataGenerator.TransportData transportData;
	private TransportGraph transportGraph;
	private RouteFinder routeFinder;
	private List<RouteDetails> currentRoutes; 
	
	
	public List<RouteDetails> findTopKRoutes(String fromCity, String toCity, OptimizationCriteria criteria, int k) {
	    MultiGraph graph = (MultiGraph) transportGraph.buildGraph();
	    YenKShortestPaths yen = new YenKShortestPaths(graph);

	    List<PathInfo> pathInfos = yen.findTopKPaths(fromCity, toCity, criteria, k);

	    return convertPathInfosToRouteDetails(pathInfos);
	}



	public RouteController(TransportDataGenerator.TransportData transportData)

	{
		this.transportData = transportData;
		this.transportGraph = new TransportGraph(transportData);
		
		this.routeFinder = new RouteFinder((MultiGraph) transportGraph.buildGraph());
		this.currentRoutes = new ArrayList<>();

		// printParsedData();
	}

	/**
	 * 
	 * @return List of all cities
	 */
	public String[] getAllCities() {
		if (transportData == null || transportData.countryMap == null)
			return new String[0];

		int rows = transportData.countryMap.length;
		int cols = transportData.countryMap[0].length;
		String[] cities = new String[rows * cols];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cities[index++] = transportData.countryMap[i][j];
			}
		}
		return cities;
	}

	/**
	 * Initializes origin and departure combo boxes with list of cities
	 * 
	 * @param cbPolaziste Combo box with list of possible origin cities
	 * @param cbOdrediste Combo box with list of possible destination cities
	 */
	public void initializeComboBoxes(JComboBox<String> cbPolaziste, JComboBox<String> cbOdrediste) {
		String[] cities = getAllCities();
		cbPolaziste.setModel(new DefaultComboBoxModel<>(cities));
		cbOdrediste.setModel(new DefaultComboBoxModel<>(cities));
	}

	

	/**
	 * Removes city selected in Polaziste Combo box from Odrediste Combo Box
	 * preventing user from choosing same city as both origin and destination.
	 * 
	 * @param cbPolaziste Combo box with list of possible origin cities
	 * @param cbOdrediste Combo box with list of possible destination cities
	 * 
	 */
	public void updateEndComboBox(JComboBox<String> cbPolaziste, JComboBox<String> cbOdrediste) {
		String selectedStart = (String) cbPolaziste.getSelectedItem();// prebacimo u string selektovani item iz cboxa
		String[] cities = getAllCities();

		java.util.List<String> endCities = new java.util.ArrayList<>();
		for (String c : cities) {
			if (!c.equals(selectedStart))
				endCities.add(c);
		}
		cbOdrediste.setModel(new DefaultComboBoxModel<>(endCities.toArray(new String[0])));

	}

	public RouteDetails getBestRoute() {
		return currentRoutes.isEmpty() ? null : currentRoutes.get(0);
	}

	private List<RouteDetails> convertPathInfosToRouteDetails(List<PathInfo> pathInfos) {
		List<RouteDetails> routeDetailsList = new ArrayList<>();
		for (PathInfo pathInfo : pathInfos) {
			routeDetailsList.add(new RouteDetails(pathInfo));
		}
		return routeDetailsList;
	}
	
	
	
	

	public List<RouteDetails> getCurrentRoutes() {
		return currentRoutes;
	}

	public RouteDetails findBestRoute(String fromCity, String toCity, OptimizationCriteria criteria) {
	    List<Node> startNodes = routeFinder.getStationsForCity(fromCity);
	    List<Node> endNodes = routeFinder.getStationsForCity(toCity);

	    PathInfo bestPathInfo = null;

	    for (Node start : startNodes) {
	        for (Node end : endNodes) {
	            if (!start.equals(end)) {
	                PathInfo path = routeFinder.dijkstraShortestPath(start, end, criteria);
	                if (path != null) {
	                    
	                    if (bestPathInfo == null) {
	                        bestPathInfo = path;
	                    } else {
	                        switch (criteria) {
	                            case SHORTEST_TIME:
	                                if (path.getTotalTime() < bestPathInfo.getTotalTime()) bestPathInfo = path;
	                                break;
	                            case LOWEST_COST:
	                                if (path.getTotalCost() < bestPathInfo.getTotalCost()) bestPathInfo = path;
	                                break;
	                            case FEWEST_TRANSFERS:
	                                if (path.getTotalTransfers() < bestPathInfo.getTotalTransfers()) bestPathInfo = path;
	                                break;
	                        }
	                    }
	                }
	            }
	        }
	    }

	    if (bestPathInfo == null) return null;

	 
	    RouteDetails bestRoute = new RouteDetails(bestPathInfo);
	    currentRoutes = new ArrayList<>();
	    currentRoutes.add(bestRoute); 
	    return bestRoute;
	}




	
	/**
	 * Gets the transport graph for visualization
	 */
	public TransportGraph getTransportGraph() {
		return transportGraph;
	}

	/**
	 * Gets the route finder instance
	 */
	public RouteFinder getRouteFinder() {
		return routeFinder;
	}

	

//	}
	/**
	 * Helper method to convert display text to OptimizationCriteria
	 */
	public OptimizationCriteria getCriteriaFromDisplayText(String displayText) {
	    if (displayText == null) return OptimizationCriteria.SHORTEST_TIME;
	    
	    switch (displayText) {
	        case "Najkraće vrijeme":
	            return OptimizationCriteria.SHORTEST_TIME;
	        case "Najniža cijena":
	            return OptimizationCriteria.LOWEST_COST;
	        case "Najmanje presjedanja":
	            return OptimizationCriteria.FEWEST_TRANSFERS;
	        default:
	            return OptimizationCriteria.SHORTEST_TIME; // default
	    }
	}
	

	/**
	 * Validates if two cities are different
	 */
	public boolean validateCities(String fromCity, String toCity) {
		return fromCity != null && toCity != null && !fromCity.equals(toCity);
	}

	/**
	 * Gets statistics about the transport network
	 */
	public String getNetworkStatistics() {
		if (transportData == null)
			return "No data loaded";

		int totalCities = transportData.countryMap.length * transportData.countryMap[0].length;
		int totalStations = transportData.stations.size() * 2; // Bus + train per city
		int totalDepartures = transportData.departures.size();

		return String.format("Cities: %d | Stations: %d | Departures: %d", totalCities, totalStations, totalDepartures);
	}

}


