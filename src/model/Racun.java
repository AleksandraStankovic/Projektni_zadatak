package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.graphstream.graph.Node;
import java.util.List;

public class Racun {



	private String polazak; 
	private String odrediste;
	
	private String cijena;
	private String vrijemePutovanja;
	private LocalDateTime datumKupovine;
	
	private String getFirstCityName(RouteDetails route)
	{
		List<Node> nodes = route.getPath().getNodePath();
		

		if (!nodes.isEmpty()) {
		    Node firstNode = nodes.get(0);
		    String firstCity = (String)firstNode.getAttribute("city");
		    return firstCity;
		}
		
		return null;
		
		
		    
	}
	
	
	private String getLastCityName(RouteDetails route)
	{
		List<Node> nodes = route.getPath().getNodePath();
		//String foCity; 

		if (!nodes.isEmpty()) {
		    Node lastNode =  nodes.get(nodes.size() - 1);
		    String lastCity = (String)lastNode.getAttribute("city");
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

	
	
	public String generisiRacun()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		return String.format(
			    "Relacija: %s → %s\n" +
			    "Vrijeme putovanja: %s\n" +
			    "Cijena: %s\n" +
			    "Datum kupovine: %s\n",
			    polazak, odrediste, vrijemePutovanja, cijena, datumKupovine.format(formatter)
			);
	}

}
