package model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,      
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"                 
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BusStation.class, name = "autobus"),
    @JsonSubTypes.Type(value = TrainStation.class, name = "voz")
})
/**
 * Abstract base class representing a transport station.
 *
 * <p>
 * This class is designed for polymorphic JSON deserialization using Jackson.
 * The {@code type} property in JSON indicates the concrete subclass:
 * "autobus" for BusStation and "voz" for TrainStation.
 * </p>
 *
 * <p>
 * Each station stores the city it belongs to and a unique station code.
 * Subclasses must implement the {@code getType()} method to indicate the type of station.
 * </p>
 */

public abstract class Station {
    protected String city;
    protected String stationCode;

    public Station() {}  

    public Station(String city, String stationCode) {
        this.city = city;
        this.stationCode = stationCode;
    }

    public String getCity() {
        return city;
    }

    public String getStationCode() {
        return stationCode;
    }

    public abstract String getType();
}
