package graph;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.*;

import model.OptimizationCriteria;
import model.PathInfo;
import java.time.LocalTime;

public class RouteFinder {
	// uzme graf koji ce da pretrazuje

	private final MultiGraph graph;

	// konstruktor
	public RouteFinder(MultiGraph graph)// uzme graf
	{
		this.graph = graph;// ovaj graf ce kasnije da se pretrazuje
	}

	// uzima sve stanice iz tog grada

	public List<Node> getStationsForCity(String city) {
		List<Node> stations = new ArrayList<>();
		for (Node node : graph) {
			if (node.getAttribute("city") != null && node.getAttribute("city").equals(city)) {
				stations.add(node);
			}
		}
		return stations; // vraća listu stanica u datom gradu

	}

	public int getEdgeWeight(Edge edge, OptimizationCriteria criteria)// vraća weight nece ivice u zavisnosti od
																		// kriterijuma
	{// ,pzda ce trebati da se doda da vraca i edge weight za minTransferTime, al to
		// myb moze bit i posebna funkciaj
		String edgeType = (String) edge.getAttribute("type");// da li je samo transfer ili je vozna linija izmedju
																// gradova

		if (edgeType == null)
			return 1;

		switch (criteria) {
		case SHORTEST_TIME:// ako je kriterijum najkrace vrijeme, onda u zavisnosti da li je transfer ili
							// ne
			return "transfer".equals(edgeType) ? (int) edge.getNumber("minTransferTime") : // transfer, vraca onda
																							// vrijeme
					(int) edge.getNumber("duration");// trajanje puta ako je put
		case LOWEST_COST:
			return "transfer".equals(edgeType) ? 0 : (int) edge.getNumber("price");// cijena, ako je transfer ivica,
																					// onda 0, ako je putovanje, onda
																					// cijenu
		case FEWEST_TRANSFERS:
			// return "transfer".equals(edgeType)?0:1;//ako je transfer, nije presjedanje,
			// inda jeste
			return 1; // svako presjedanje se racuna kao jedan, nebitno da li je u istom gradu ili ne.
		default:
			return 1;
		}

	}

	// idk, sta god da je ovaj PathInfo

	// metoda djisktras shorterst path, iako nije 100% tacan naziv

	   public  PathInfo dijkstraShortestPath(Node start, Node end, OptimizationCriteria criteria) {
		      
	       class NodeDistance implements Comparable<NodeDistance> {
	           Node node;
	           int distance;
	           NodeDistance(Node node, int distance) {
	               this.node = node;
	               this.distance = distance;
	           }
	           @Override
	           public int compareTo(NodeDistance other) {
	               return Integer.compare(this.distance, other.distance);
	           }
	       }
	       Map<Node, Integer> distances = new HashMap<>();
	       Map<Node, Node> previousNodes = new HashMap<>();
	       Map<Node, Edge> previousEdges = new HashMap<>();
	       PriorityQueue<NodeDistance> queue = new PriorityQueue<>();
	      
	      
	       for (Node node : graph) {
	           distances.put(node, Integer.MAX_VALUE);
	       }
	       distances.put(start, 0);
	       queue.add(new NodeDistance(start, 0));
	      
	       while (!queue.isEmpty()) {
	           NodeDistance current = queue.poll();
	           Node currentNode = current.node;
	          
	           if (currentNode.equals(end)) break;
	           if (current.distance > distances.get(currentNode)) continue;
	          
	           for (Edge edge : currentNode.leavingEdges().toArray(Edge[]::new)) {
	               Node neighbor = edge.getTargetNode();
	               int weight = getEdgeWeight(edge, criteria);
	               int newDistance = distances.get(currentNode) + weight;
	             

	               
	               
	               //da pokusamo uzeti iz egda neko virjeme
	               LocalTime depTime =  (LocalTime)edge.getAttribute("departureTime");
	               System.out.println("Edge id: " + edge.getId() + ", departure: " + depTime);
	             
	               

	              
	               if (newDistance < distances.get(neighbor)) {
	                   distances.put(neighbor, newDistance);
	                   previousNodes.put(neighbor, currentNode);
	                   previousEdges.put(neighbor, edge);
	                   queue.add(new NodeDistance(neighbor, newDistance));
	               }
	           }
	       }
	      
	       if (distances.get(end) == Integer.MAX_VALUE) return null;
	      
	      
	       Path path = new Path();
	       List<Edge> edgePath = new ArrayList<>();
	      
	       Node current = end;
	       while (!current.equals(start)) {
	           Edge edge = previousEdges.get(current);
	           if (edge == null) break;
	           edgePath.add(0, edge);
	           current = previousNodes.get(current);
	       }
	      
	       path.setRoot(start);
	       for (Edge edge : edgePath) {
	           path.add(edge);
	       }
	      
	      
	       int totalTime = 0, totalCost = 0, totalTransfers = 0;
	       for (Edge edge : path.getEdgePath()) {
	           totalTime += getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME);
	           totalCost += getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST);
	           totalTransfers += getEdgeWeight(edge, OptimizationCriteria.FEWEST_TRANSFERS);
	       }
	      
	       return new PathInfo(path, totalTime, totalCost, totalTransfers, distances.get(end));
	   }

	
	
	
	
	// idk valjda neka metoda za dobijanje neceg za ui, idk, pojma nemam
	public List<String> getPathDetails(PathInfo pathInfo) {
		List<String> details = new ArrayList<>();
		Path path = pathInfo.getPath();

		if (path.getEdgeCount() == 0) {
			details.add("Direct connection");
			return details;
		}

		details.add("Total: " + pathInfo.getTotalTime() + " min, " + pathInfo.getTotalCost() + " €, "
				+ pathInfo.getTotalTransfers() + " transfers");

		Node current = path.getRoot();
		for (Edge edge : path.getEdgePath()) {
			Node next = edge.getTargetNode();
			String edgeType = (String) edge.getAttribute("type");

			if ("transfer".equals(edgeType)) {
				details.add("Transfer: " + current.getId() + " → " + next.getId() + " ("
						+ getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min wait)");
			} else {
				String transport = current.getId().startsWith("A_") ? "Bus" : "Train";
				details.add(transport + ": " + current.getId() + " → " + next.getId() + " ("
						+ getEdgeWeight(edge, OptimizationCriteria.SHORTEST_TIME) + " min, "
						+ getEdgeWeight(edge, OptimizationCriteria.LOWEST_COST) + " €)");
			}
			current = next;
		}

		return details;
	}

}