package model;

//idk, za modelovanje info o putanji idk...
import org.graphstream.graph.Path;

public class PathInfo {
//ima osnovne info o putanji, info koji nam treba kasnije, u sustini ukupno sve 
	Path path;
	private int totalTime;
	private int totalCost;
	private int totalTransfers;
	private int totalWeight;

	// I guess da ovako ide konstruktror...
	public PathInfo(Path path, int totalTime, int totalCost, int totalTransfers, int totalWeight) {
		this.path = path;
		this.totalTime = totalTime;
		this.totalCost = totalCost;
		this.totalTransfers = totalTransfers;
		this.totalWeight = totalWeight;

	}

	// Getter methods
	public Path getPath() {
		return path;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public int getTotalCost() {
		return totalCost;
	}

	public int getTotalTransfers() {
		return totalTransfers;
	}

	public int getTotalWeight() {
		return totalWeight;
	}
}
