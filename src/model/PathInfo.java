package model;

import org.graphstream.graph.Path;

public class PathInfo {
	private final Path path;
	private final int totalTime;
	private final int totalCost;
	private final int totalTransfers;
	private final int totalWeight;

	public PathInfo(Path path, int totalTime, int totalCost, int totalTransfers, int totalWeight) {
		this.path = path;
		this.totalTime = totalTime;
		this.totalCost = totalCost;
		this.totalTransfers = totalTransfers;
		this.totalWeight = totalWeight;

	}

	public PathInfo(Path path) {
		this(path, 0, 0, 0, 0);
	}

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

	@Override
	public String toString() {
		return String.format("PathInfo{time=%d, cost=%d, transfers=%d, weight=%d}", totalTime, totalCost,
				totalTransfers, totalWeight);
	}
}