package model;

public class BusStation extends Station {
    public BusStation() {}  // default constructor for Jackson

    public BusStation(String city, String stationCode) {
        super(city, stationCode);
    }

    @Override
    public String getType() {
        return "autobus";
    }
}
