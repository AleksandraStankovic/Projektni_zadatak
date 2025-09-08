package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.graphstream.graph.Node;
import java.util.List;

/**
 * Represents a receipt for a purchased route.
 *
 * <p>
 * The class stores information about the departure city, destination city,
 * total price, travel time, and purchase date. It provides methods to generate
 * a formatted receipt string.
 * </p>
 *
 * <p>
 * The constructor initializes the receipt using a {@code RouteDetails} object,
 * extracting the first and last cities from the path, total cost, and total
 * travel time. The purchase date is set to the current date and time.
 * </p>
 *
 * <p>
 * Private helper methods are used to retrieve the first and last city names
 * from the route path.
 * </p>
 */

public class Racun {

	private String polazak;
	private String odrediste;

	private String cijena;
	private String vrijemePutovanja;
	private LocalDateTime datumKupovine;

	private String getFirstCityName(RouteDetails route) {
		List<Node> nodes = route.getPath().getNodePath();

		if (!nodes.isEmpty()) {
			Node firstNode = nodes.get(0);
			String firstCity = (String) firstNode.getAttribute("city");
			return firstCity;
		}

		return null;

	}

	private String getLastCityName(RouteDetails route) {
		List<Node> nodes = route.getPath().getNodePath();

		if (!nodes.isEmpty()) {
			Node lastNode = nodes.get(nodes.size() - 1);
			String lastCity = (String) lastNode.getAttribute("city");
			return lastCity;
		}

		return null;

	}

	public Racun(RouteDetails route) {
		this.polazak = this.getFirstCityName(route);
		this.odrediste = this.getLastCityName(route);
		this.cijena = route.getFormattedTotalCost();
		this.vrijemePutovanja = route.getFormattedTotalTime();
		this.datumKupovine = LocalDateTime.now();

	}

	public String generisiRacun() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		return String.format(
				"Relacija: %s → %s\n" + "Vrijeme putovanja: %s\n" + "Cijena: %s\n" + "Datum kupovine: %s\n", polazak,
				odrediste, vrijemePutovanja, cijena, datumKupovine.format(formatter));
	}

}
