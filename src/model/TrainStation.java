package model;
/**
 * Represents a train station, extending the abstract Station class.
 *
 * <p>
 * This class provides the type of station as "voz" and can be used
 * for creating train-specific nodes in the transport graph. It includes
 * a default constructor for JSON deserialization and a parameterized
 * constructor for manual creation.
 * </p>
 */

public class TrainStation extends Station {
    public TrainStation() {}  

    public TrainStation(String city, String stationCode) {
        super(city, stationCode);
    }

    @Override
    public String getType() {
        return "voz";
    }
}
