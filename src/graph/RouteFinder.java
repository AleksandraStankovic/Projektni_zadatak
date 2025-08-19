package graph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;
import java.util.stream.Collectors;
import model.OptimizationCriteria;
import model.PathInfo;

public class RouteFinder {
	
    private final MultiGraph graph;
	
	//prvo moramo inicijalizovati i ucitati graf ovjde
    
    //init u konstruktoru
    public RouteFinder(MultiGraph graph)//dakle, kad ovo budemo trazili, moraće se prosliejediti graf ovdje
    {
    	this.graph=graph;
    }

    
    /**Finds all stations in given city. 
     * @param city
     * @return
     */
    private List<Node> getStationsForCity(String city)//uzima grad i onda uzima sve stanice za taj grad iz grafa, idk... 
    {//moza ima bolji nacin? //Mozda ovo moze na neko drugo mjesto? 
    	//idemo kroz graf i nodes, uzimamo taj grad pronajdemo stanice za njega i stavimo u listu stanica za taj grad
    	//bukvalno pronalazi sve stanice u datom gradu
    	
    	List<Node> stations = new ArrayList<>();
    	for(Node node : graph)
    	{
    		if(node.getAttribute("city").equals(city))
    		{
    			stations.add(node);
    		}
    	}
    	return stations; 
    	
    }//ovo kasnije koristimo da uzmemo sve stanice iz prvog i pocetnog grada, gdje dakle uzmemo pocetne i krajnje stanice 
    
    
    
//    /*IDK, OVO MOZDA NIJE DOBRO
//    //idk, metoda koja uzima tj pronalazi tezinu odredjenog edga
//    //bukvalno samo uzima ivicu i za nju nam daje njen weight na neki nacin idk, 
    
//    private int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
//        switch (criteria) {
//            case SHORTEST_TIME:
//                // Cast to Number (works for Integer, Double, etc.) and get int value
//                return ((Number) edge.getAttribute("duration")).intValue();
//            case LOWEST_COST:
//                return ((Number) edge.getAttribute("price")).intValue();
//            case FEWEST_TRANSFERS:
//                // For transfers, we need to handle the String "type" attribute
//                return edge.getAttribute("type").equals("transfer") ? 0 : 1;
//            default:
//                return 1;
//        }
//    }
//    
    
    
    private int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
        switch (criteria) {
            case SHORTEST_TIME:
                return edge.hasAttribute("timeWeight") ? 
                       (int) edge.getNumber("timeWeight") : 60;
                
            case LOWEST_COST:
                return edge.hasAttribute("priceWeight") ? 
                       (int) edge.getNumber("priceWeight") : 100;
                
            case FEWEST_TRANSFERS:
                return edge.hasAttribute("transfersWeight") ? 
                       (int) edge.getNumber("transfersWeight") : 1;
                
            default:
                return 1;
        }
    }
    
    
    
    
    
    
    
    //i ovo mozda nije dobro, idk....
//    private int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
//        String edgeType = edge.getAttribute("type");
//        
//        switch (criteria) {
//            case SHORTEST_TIME:
//                // For transport edges, use duration; for transfers, use transfer_time
//                if ("transfer".equals(edgeType)) {
//                    return (int) edge.getNumber("transfer_time", 5); // Default 5 mins for transfers
//                } else {
//                    return (int) edge.getNumber("duration", 60); // Default 60 mins if missing
//                }
//                
//            case LOWEST_COST:
//                // For transport edges, use price; for transfers, use cost (which is 0)
//                if ("transfer".equals(edgeType)) {
//                    return (int) edge.getNumber("cost", 0); // Transfers are free
//                } else {
//                    return (int) edge.getNumber("price", 100); // Default 100 if missing
//                }
//                
//            case FEWEST_TRANSFERS:
//                // Count each transport edge as 1, transfers don't count
//                return "transfer".equals(edgeType) ? 0 : 1;
//                
//            default:
//                return 1;
//        }
//    }
    
    
    
    //metoda za odlucivanje da li je nesto bolji path ili ne
    //provjerava da li je novi path bolji od postojeceg, bukvalno za poredjenje putanja
    //za razlicite kriterijume vraca true ako je kod novog manje ukupno vrijeme i ostali kriterijumi 
    private boolean isBetterPath(PathInfo newPath, PathInfo existingPath,OptimizationCriteria criteria )
    {
    	 switch (criteria) {
         case SHORTEST_TIME:
             return newPath.getTotalTime() < existingPath.getTotalTime();
         case LOWEST_COST:
             return newPath.getTotalCost() < existingPath.getTotalCost();//myb 
         case FEWEST_TRANSFERS:
             return newPath.getTotalTransfers() < existingPath.getTotalTransfers();
         default:
             return false;
     }
    }//vraca true ako je novi put bolji od prethodnog po nekom kriterijumu 
    
    
    
    
    
    
    //metoda za odlucicanje da li terba putanja da se doda ili ne, idk....
    //odlicuje da li da se novootkrivena putanja zadrzi do nekog cvora u odnosu na postojece 
//    //uzima mapu sa cvorovima i list sa najboljom putanjom da se dodje do tog cvora
//    destination node do kog dolatimo
//    novi path koji gledamo da li zelimo da uvrstimo 
//    po kom kriterijumu gledamo. 
    //provjeravamo koji put je bolji, ako je novi bolji, dodajemo ga u listu novih 
    private boolean shouldAddPath(Map<Node, List<PathInfo>> bestPaths, Node node, PathInfo newPath, OptimizationCriteria criteria) {
    	
        List<PathInfo> existingPaths = bestPaths.getOrDefault(node, Collections.emptyList());
        //dobicemo mapu sa parovima cvor-najbolje putanje, ekstrahujemo najbolje putanje za dati node
        
        if (existingPaths.size() < 5) {//ako imamo manje od 5, sigurno cemo da zadrzimo. Ako nemamo provjeravamo dalje idk
            return true;
        }
        
        for(PathInfo existing:existingPaths)
        {
        	if(isBetterPath(newPath, existing, criteria))
        	{
        		return true;
        	}
        }
        
        return false;
        	
        
    }
    
    
    //idk, uglavnom racuna ukupono kolika je tezina putanje 
    //metoda za pronalazenje weigta ukuponog za citavu putanju 
    private int getPathWeight(Path path, OptimizationCriteria criteria)
    {
    	return path.getEdgeSet().stream()//path uzima sve ivice iz putanje 
    			.mapToInt(edge->getEdgeWeight(edge, criteria))//mapira na njegovu tezinu i sumira
    			.sum();
    }
    
    //idk, bukvalno sortiranje putanja i guess....
    private void sortPaths(List<Path> paths,OptimizationCriteria criteria )//sortiranje putanja based on criteria
    //bukvalno se iz svakog puta uzima njegov ukupni weight i poredi se , pa se sortira na osnovu tog 
    {
    	paths.sort((p1,p2)->{
    		int weight1=getPathWeight(p1, criteria); 
    		int weight2 = getPathWeight(p1, criteria);
    		return Integer.compare(weight1, weight2);
    	});
    	
    }
    
    
    
    //metoda za pronalazenje putanja
    //uzima pocetke i krajnje stanice, a vraća listu Path objekata
    /*
     * Svi validne rute od pocetka do kraja, sortiranje zbog reda. i to po zadatom kriterijumu
     */
    private List<Path> findPaths(Node start, Node end, OptimizationCriteria criteria)
    {
    	
    	PriorityQueue<PathInfo> queue = new PriorityQueue<>(Comparator.comparingInt(pi->pi.totalWeight))
    	
    }
    
    
    
    
    
    //metoda za pronalazenje ruta 
    
   public List<Path> findRoutes (String fromCity, String toCity, OptimizationCriteria criteria, int maxResults) 
   {
	   //idk, ima ovjde nekih metoda, idk...koje su pomocne, koje cemo gore definisati
	   
	   List<Node> startNodes=getStationsForCity(fromCity);//uzmemo pocetne stanice
	   List<Node> endNodes=getStationsForCity(toCity);
	   
	   //lista svih mogucih puteva I guess
	   List<Path> allPaths = new ArrayList<>();
	   
	   //sad ide pronalazenje kombinacija tj pronalazak svih puteva za kombinaciju stanica 
	   
	   for(Node start:startNodes)
	   {
		   for(Node end: endNodes)
		   {
			   if(!start.equals(end))//ako pocetak i start nisu isti I guess
			   {
				   //idk, neka metoda za nesto 
			   }
		   }
	   }
   }
   
   

}
