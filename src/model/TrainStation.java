package model;

/**
 * Represents a train station within a city.
 */
public class TrainStation extends Station {
    private String stationCode;

    public TrainStation(String city, String trainStation) {
        super(city, null, trainStation); // no bus station here
        this.stationCode = trainStation;
    }

    public String getStationCode() {
        return stationCode;
    }
}
