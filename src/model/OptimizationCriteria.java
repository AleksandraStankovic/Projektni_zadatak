package model;
/**
 * Represents the available optimization criteria for route finding.
 *
 * <p>
 * The enum defines three criteria:
 * <ul>
 *   <li>SHORTEST_TIME – optimize for the shortest travel time</li>
 *   <li>LOWEST_COST – optimize for the lowest travel cost</li>
 *   <li>FEWEST_TRANSFERS – optimize for the fewest number of transfers</li>
 * </ul>
 * </p>
 *
 * <p>
 * Each enum constant has a display name in a user-friendly format.
 * Utility methods are provided to get the display name and to convert
 * from a display name string back to an enum constant.
 * </p>
 */


public enum OptimizationCriteria {
	SHORTEST_TIME("Najkraće vrijeme"), LOWEST_COST("Najniža cijena"), FEWEST_TRANSFERS("Najmanje presjedanja");

	private final String displayName;

	OptimizationCriteria(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public static OptimizationCriteria fromDisplayName(String displayName) {
		for (OptimizationCriteria criteria : values()) {
			if (criteria.displayName.equals(displayName)) {
				return criteria;
			}
		}
		return SHORTEST_TIME;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
