























//package graph;
//
//import org.graphstream.graph.Edge;
//import org.graphstream.graph.Node;
//import org.graphstream.graph.Path;
//import org.graphstream.graph.implementations.MultiGraph;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import model.OptimizationCriteria;
//import model.PathInfo;
//
//public class RouteFinder {
//
//	private final MultiGraph graph;
//
//	public RouteFinder(MultiGraph graph)// dakle, kad ovo budemo trazili, moraće se prosliejediti graf ovdje
//	{
//		this.graph = graph;
//	}
//
//	/**
//	 * Finds all stations in given city.
//	 * 
//	 * @param city
//	 * @return
//	 */
//	private List<Node> getStationsForCity(String city)// uzima grad i onda uzima sve stanice za taj grad iz grafa,
//														// idk...
//	{// moza ima bolji nacin? //Mozda ovo moze na neko drugo mjesto?
//		// idemo kroz graf i nodes, uzimamo taj grad pronajdemo stanice za njega i
//		// stavimo u listu stanica za taj grad
//		// bukvalno pronalazi sve stanice u datom gradu
//
//		List<Node> stations = new ArrayList<>();
//		for (Node node : graph) {
//			if (node.getAttribute("city").equals(city)) {
//				stations.add(node);
//			}
//		}
//		return stations;
//
//	}// ovo kasnije koristimo da uzmemo sve stanice iz prvog i pocetnog grada, gdje
//		// dakle uzmemo pocetne i krajnje stanice
//
//	private int getEdgeWeight(Edge edge, OptimizationCriteria criteria) {
//		String edgeType = (String) edge.getAttribute("type");
//
//		if (edgeType == null) {
//			System.out.println("WARNING: Edge " + edge.getId() + " has no type attribute!");
//			return 1;
//		}
//
//		switch (criteria) {
//		case SHORTEST_TIME:
//			if ("transfer".equals(edgeType)) {
//				return (int) edge.getNumber("minTransferTime");
//			} else {
//				return (int) edge.getNumber("duration");
//			}
//		case LOWEST_COST:
//			if ("transfer".equals(edgeType)) {
//				return 0; // Transfers are free
//			} else {
//				return (int) edge.getNumber("price");
//			}
//		case FEWEST_TRANSFERS:
//			return "transfer".equals(edgeType) ? 0 : 1;
//		default:
//			return 1;
//		}
//	}
//
//	// metoda za odlucivanje da li je nesto bolji path ili ne
//	// provjerava da li je novi path bolji od postojeceg, bukvalno za poredjenje
//	// putanja
//	// za razlicite kriterijume vraca true ako je kod novog manje ukupno vrijeme i
//	// ostali kriterijumi
//	private boolean isBetterPath(PathInfo newPath, PathInfo existingPath, OptimizationCriteria criteria) {
//		switch (criteria) {
//		case SHORTEST_TIME:
//			return newPath.getTotalTime() < existingPath.getTotalTime();
//		case LOWEST_COST:
//			return newPath.getTotalCost() < existingPath.getTotalCost();// myb
//		case FEWEST_TRANSFERS:
//			return newPath.getTotalTransfers() < existingPath.getTotalTransfers();
//		default:
//			return false;
//		}
//	}// vraca true ako je novi put bolji od prethodnog po nekom kriterijumu
//
//	// metoda za odlucicanje da li terba putanja da se doda ili ne, idk....
//	// odlicuje da li da se novootkrivena putanja zadrzi do nekog cvora u odnosu na
//	// postojece
////    //uzima mapu sa cvorovima i list sa najboljom putanjom da se dodje do tog cvora
////    destination node do kog dolatimo
////    novi path koji gledamo da li zelimo da uvrstimo 
////    po kom kriterijumu gledamo. 
//	// provjeravamo koji put je bolji, ako je novi bolji, dodajemo ga u listu novih
//	private boolean shouldAddPath(Map<Node, List<PathInfo>> bestPaths, Node node, PathInfo newPath,
//			OptimizationCriteria criteria) {
//
//		List<PathInfo> existingPaths = bestPaths.getOrDefault(node, Collections.emptyList());
//		// dobicemo mapu sa parovima cvor-najbolje putanje, ekstrahujemo najbolje
//		// putanje za dati node
//
//		if (existingPaths.size() < 5) {// ako imamo manje od 5, sigurno cemo da zadrzimo. Ako nemamo provjeravamo dalje
//										// idk
//			return true;
//		}
//
//		for (PathInfo existing : existingPaths) {
//			if (isBetterPath(newPath, existing, criteria)) {
//				return true;
//			}
//		}
//
//		return false;
//
//	}
//
//	// idk, uglavnom racuna ukupono kolika je tezina putanje
//	// metoda za pronalazenje weigta ukuponog za citavu putanju
//	private int getPathWeight(Path path, OptimizationCriteria criteria) {
//		return path.getEdgeSet().stream()// path uzima sve ivice iz putanje
//				.mapToInt(edge -> getEdgeWeight(edge, criteria))// mapira na njegovu tezinu i sumira
//				.sum();
//	}
//
//	// idk, bukvalno sortiranje putanja i guess....
//	private void sortPaths(List<Path> paths, OptimizationCriteria criteria) {
//		paths.sort((p1, p2) -> {
//			int weight1 = getPathWeight(p1, criteria);
//			int weight2 = getPathWeight(p2, criteria); // Fixed: p2 instead of p1
//			return Integer.compare(weight1, weight2);
//		});
//	}
//
////	private List<Edge> getLeavingEdges(Node node) {
////	    List<Edge> edges = new ArrayList<>();
////	    Iterator<? extends Edge> iterator = node.getLeavingEdgeIterator();
////	    while (iterator.hasNext()) {
////	        edges.add(iterator.next());
////	    }
////	    return edges;
////	}
//
//	// ova bitna metoda za pronalazenje puteva
//
//	private List<Path> findPaths(Node start, Node end, OptimizationCriteria criteria) {
//		PriorityQueue<PathInfo> queue = new PriorityQueue<>(Comparator.comparingInt(pi -> pi.getTotalWeight()));
//		Map<Node, List<PathInfo>> bestPaths = new HashMap<>();
//
//		PathInfo initialPath = new PathInfo(new Path(), 0, 0, 0, 0);
//		initialPath.getPath().setRoot(start);
//		queue.add(initialPath);
//		bestPaths.put(start, new ArrayList<>(Collections.singletonList(initialPath)));
//
//		while (!queue.isEmpty()) {
//			PathInfo current = queue.poll();
//			Path currentPath = current.getPath();
//
//			if (currentPath.getNodeCount() == 0) {
//				continue;
//			}
//
//			List<Node> nodePath = currentPath.getNodePath();
//			if (nodePath.isEmpty())
//				continue;
//			Node currentNode = nodePath.get(nodePath.size() - 1);
//
//			if (currentNode.equals(end)) {
//				continue;
//			}
//
//			// Get edges using the most basic method - compatible with all GraphStream
//			// versions
//			for (int i = 0; i < currentNode.getDegree(); i++) {
//				Edge edge = currentNode.getEdge(i);
//
//				// Determine direction and get neighbor
//				Node neighbor;
//				if (edge.getSourceNode().equals(currentNode)) {
//					neighbor = edge.getTargetNode();
//				} else if (edge.getTargetNode().equals(currentNode)) {
//					neighbor = edge.getSourceNode();
//				} else {
//					continue; // Shouldn't happen
//				}
//
//				// Calculate weights
//				int timeDelta = getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME);
//				int costDelta = getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST);
//				int transfersDelta = getEdgeWeight(edge, OptimizationCriteria.FEWEST_TRANSFERS);
//				int totalWeightDelta = getEdgeWeight(edge, criteria);
//
//				int newTotalTime = current.getTotalTime() + timeDelta;
//				int newTotalCost = current.getTotalCost() + costDelta;
//				int newTotalTransfers = current.getTotalTransfers() + transfersDelta;
//				int newTotalWeight = current.getTotalWeight() + totalWeightDelta;
//
//				// Create new path
//				Path newPath = new Path();
//				newPath.setRoot(currentPath.getRoot());
//
//				// Copy all edges from current path
//				for (Edge existingEdge : currentPath.getEdgePath()) {
//					newPath.add(existingEdge);
//				}
//
//				// Add the new edge
//				newPath.add(edge);
//
//				PathInfo newPathInfo = new PathInfo(newPath, newTotalTime, newTotalCost, newTotalTransfers,
//						newTotalWeight);
//
//				if (shouldAddPath(bestPaths, neighbor, newPathInfo, criteria)) {
//					queue.add(newPathInfo);
//					bestPaths.computeIfAbsent(neighbor, k -> new ArrayList<>()).add(newPathInfo);
//				}
//			}
//		}
//
//		return bestPaths.getOrDefault(end, Collections.emptyList()).stream().map(PathInfo::getPath)
//				.collect(Collectors.toList());
//	}
//
//	// metoda za pronalazenje ruta
//	public List<Path> findRoutes(String fromCity, String toCity, OptimizationCriteria criteria, int maxResults) {
//		// idk, ima ovjde nekih metoda, idk...koje su pomocne, koje cemo gore definisati
//
//		List<Node> startNodes = getStationsForCity(fromCity);// uzmemo pocetne stanice
//		List<Node> endNodes = getStationsForCity(toCity);
//
//		System.out.println("Start stations: " + startNodes.stream().map(Node::getId).collect(Collectors.toList()));
//		System.out.println("End stations: " + endNodes.stream().map(Node::getId).collect(Collectors.toList()));
//
//		List<Path> allPaths = new ArrayList<>();
//
//		// sad ide pronalazenje kombinacija tj pronalazak svih puteva za kombinaciju
//		// stanica
//
//		for (Node start : startNodes) {
//			for (Node end : endNodes) {
//				if (!start.equals(end))// ako pocetak i start nisu isti I guess
//				{
//					System.out.println("Finding paths from " + start.getId() + " to " + end.getId());
//					List<Path> paths = findPaths(start, end, criteria);
//					System.out.println("Found " + paths.size() + " paths");
//					allPaths.addAll(paths);
//				}
//			}
//		}
//		System.out.println("Total paths found: " + allPaths.size());
//		sortPaths(allPaths, criteria);
//
//		List<Path> result = allPaths.stream().limit(maxResults).collect(Collectors.toList());
//		System.out.println("Returning " + result.size() + " results");
//		return result;
//
//	}
//
//}
