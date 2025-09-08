package model;

/**
 * Represents a bus station, extending the generic {@link Station} class.
 *
 * <p>
 * This class provides the type of station as "autobus" and can be used for
 * creating bus-specific nodes in the transport graph. It includes a default
 * constructor for JSON deserialization and a parameterized constructor for
 * manual creation.
 * </p>
 */

public class BusStation extends Station {
	public BusStation() {
	}

	public BusStation(String city, String stationCode) {
		super(city, stationCode);
	}

	@Override
	public String getType() {
		return "autobus";
	}
}
