package model;

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
