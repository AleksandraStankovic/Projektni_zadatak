package model;

/**
 * Represents a bus station within a city.
 */
public class BusStation extends Station {
    private String stationCode;

    public BusStation(String city, String busStation) {
        super(city, busStation, null); // no train station here
        this.stationCode = busStation;
    }

    public String getStationCode() {
        return stationCode;
    }
}
