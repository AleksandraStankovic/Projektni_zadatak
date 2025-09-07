package model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,       // Use a type name for polymorphic deserialization
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"                 // JSON property that indicates the type
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = BusStation.class, name = "autobus"),
    @JsonSubTypes.Type(value = TrainStation.class, name = "voz")
})
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
