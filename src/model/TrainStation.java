package model;

public class TrainStation extends Station {
    public TrainStation() {}  // default constructor for Jackson

    public TrainStation(String city, String stationCode) {
        super(city, stationCode);
    }

    @Override
    public String getType() {
        return "voz";
    }
}
