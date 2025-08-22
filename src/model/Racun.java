package model;
//OVO SVE JE SREDJENO, MOZE SE KUPITI KARTA ZA RUTU TJ MOZE SE UPISATI U FAJL, KASNIJE JE POTREBNO DA SE OVO PREPRAVI TAKO DA RADI ZA GENERISANE RUTE I OSTALO

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.RouteDetails;
import org.graphstream.graph.Node;
import java.util.List;

public class Racun {



	private String polazak; // odnosi se na polaznu stanicu
	private String odrediste; // krajnja stanica (da li se odnori na grad ili na sta=?
	
	private String cijena;
	private String vrijemePutovanja;
	private LocalDateTime datumKupovine;
	
	private String getFirstCityName(RouteDetails route)
	{
		List<Node> nodes = route.getPath().getNodePath();
		//String foCity; 

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
	

	//ovo treba da primi nesto drugo, vjerovatno da primi best route sto pronadje
	public Racun(RouteDetails route) {
		this.polazak = this.getFirstCityName(route); // koristi se this ako su ime parametra i ime atributa isti
		this.odrediste = this.getLastCityName(route);
		this.cijena = route.getFormattedTotalCost();
		this.vrijemePutovanja = route.getFormattedTotalTime();
		this.datumKupovine = LocalDateTime.now();// uzima se trenutno vrijeme kupovine
		//mozda se moze jos negdje dodati neko formatiranje nesto, ali je bolje da ne? 

	}

	
	
	public String generisiRacun()
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");//formatiranje datuma
		return String.format(
			    "Relacija: %s → %s\n" +
			    "Vrijeme putovanja: %s\n" +
			    "Cijena: %s\n" +
			    "Datum kupovine: %s\n",
			    polazak, odrediste, vrijemePutovanja, cijena, datumKupovine.format(formatter)
			);
	}

}
