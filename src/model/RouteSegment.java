package model;
/**
 * Represents a segment of a route between two stations.
 *
 * <p>
 * Each segment contains information about the departure and arrival stations,
 * the type of transport (bus or train), departure and arrival times, duration,
 * and price. Optional fields store the cities of the departure and arrival stations.
 * </p>
 *
 * <p>
 * The class provides constructors for creating a segment with or without initial values,
 * as well as getters and setters for all fields. Utility methods are available to
 * get formatted duration and price.
 * </p>
 */

public class RouteSegment {
	private String fromStation;
	private String toStation;
	private String transportType;
	private String departureTime;
	private String arrivalTime;
	private int duration;
	private int price;
	
	private String fromCity;
	private String toCity;

	public RouteSegment() {

	}

	public RouteSegment(String fromStation, String toStation, String transportType, int duration, int price) {
		this.fromStation = fromStation;
		this.toStation = toStation;
		this.transportType = transportType;
		this.duration = duration;
		this.price = price;
	}

	public String getFromStation() {
		return fromStation;
	}

	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}

	public String getToStation() {
		return toStation;
	}

	public void setToStation(String toStation) {
		this.toStation = toStation;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getFormattedDuration() {
		return duration + " min";
	}

	public String getFormattedPrice() {
		return price + " n.j.";
	}
	
	public String getFromCity() {
	    return fromCity;
	}

	public void setFromCity(String fromCity) {
	    this.fromCity = fromCity;
	}

	public String getToCity() {
	    return toCity;
	}

	public void setToCity(String toCity) {
	    this.toCity = toCity;
	}
}
