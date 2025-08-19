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
//kontroleer koji ce da kontrolise svu logiku vezanu za rute, da nije u gui i u main frame klasi

/**
 * 
 */
public class RouteController {

	private TransportDataGenerator.TransportData transportData;// podaci koji ce se koristiti
	private TransportGraph transportGraph;
	private RouteFinder routeFinder;
	private List<RouteDetails> currentRoutes; // Store current search results+

	/// ovo nam mece trebati ovo je samo za testiranje da li podaci dobro parsiraju
	private void printParsedData() {
		if (transportData == null) {
			System.out.println("No transport data loaded!");
			return;
		}

		System.out.println("=== Stations ===");
		for (TransportDataGenerator.Station s : transportData.stations) {
			System.out.println("City: " + s.city + ", Bus: " + s.busStation + ", Train: " + s.trainStation);
		}

		System.out.println("\n=== Departures ===");
		for (TransportDataGenerator.Departure d : transportData.departures) {
			System.out.println(d.type + " | From: " + d.from + " | To: " + d.to + " | Time: " + d.departureTime
					+ " | Duration: " + d.duration + " | Price: " + d.price + " | MinTransfer: " + d.minTransferTime);
		}

		System.out.println("\n=== Country Map ===");
		for (int i = 0; i < transportData.countryMap.length; i++) {
			for (int j = 0; j < transportData.countryMap[i].length; j++) {
				System.out.print(transportData.countryMap[i][j] + " ");
			}
			System.out.println();
		}
	}

	public RouteController(TransportDataGenerator.TransportData transportData)

	{
		this.transportData = transportData;
		this.transportGraph = new TransportGraph(transportData);
		// In RouteController.java line 57
		this.routeFinder = new RouteFinder((MultiGraph) transportGraph.buildGraph());
		this.currentRoutes = new ArrayList<>();

		// printParsedData();// ovdje imamo ovo za testiranje
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

	// metoda za update ako korisnik prvo izabere krajnji grad
	public void updateStartComboBox(JComboBox<String> cbPolaziste, JComboBox<String> cbOdrediste) {
		String selectedEnd = (String) cbOdrediste.getSelectedItem();
		String[] cities = getAllCities();

		java.util.List<String> startCities = new java.util.ArrayList<>();
		for (String c : cities) {
			if (!c.equals(selectedEnd))
				startCities.add(c);
		}
		cbPolaziste.setModel(new DefaultComboBoxModel<>(startCities.toArray(new String[0])));

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
	
	
	
	
    public List<RouteDetails> findRoutes(String fromCity, String toCity, OptimizationCriteria criteria) {
        try {
            List<org.graphstream.graph.Path> paths = routeFinder.findRoutes(fromCity, toCity, criteria, 5);
            currentRoutes = convertToRouteDetails(paths);
            return currentRoutes;
        } catch (Exception e) {
            System.err.println("Error finding routes: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Converts GraphStream Paths to RouteDetails objects
     */
    private List<RouteDetails> convertToRouteDetails(List<org.graphstream.graph.Path> paths) {
        List<RouteDetails> routeDetailsList = new ArrayList<>();
        for (org.graphstream.graph.Path path : paths) {
            routeDetailsList.add(new RouteDetails(path));
        }
        return routeDetailsList;
    }

    /**
     * Gets the current routes (from last search)
     */
    public List<RouteDetails> getCurrentRoutes() {
        return currentRoutes;
    }

    /**
     * Gets the top 5 routes from last search
     */
    public List<RouteDetails> getTopRoutes() {
        return currentRoutes.size() > 5 ? currentRoutes.subList(0, 5) : currentRoutes;
    }

    /**
     * Gets the best route from last search
     */
    public RouteDetails getBestRoute() {
        return currentRoutes.isEmpty() ? null : currentRoutes.get(0);
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

    /**
     * Helper method to convert display text to OptimizationCriteria
     */
    public OptimizationCriteria getCriteriaFromDisplayText(String displayText) {
        return OptimizationCriteria.fromDisplayName(displayText);
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
        if (transportData == null) return "No data loaded";
        
        int totalCities = transportData.countryMap.length * transportData.countryMap[0].length;
        int totalStations = transportData.stations.size() * 2; // Bus + train per city
        int totalDepartures = transportData.departures.size();
        
        return String.format("Cities: %d | Stations: %d | Departures: %d", 
                           totalCities, totalStations, totalDepartures);
    }

}
