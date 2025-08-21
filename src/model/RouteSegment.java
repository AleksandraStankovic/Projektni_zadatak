package model;

public class RouteSegment {
    private String fromStation;
    private String toStation;
    private String transportType;
    private String departureTime;
    private String arrivalTime;
    private int duration;
    private int price;
    
    // Default constructor
    public RouteSegment() {
        // Default constructor
    }
    
    // Parameterized constructor
    public RouteSegment(String fromStation, String toStation, String transportType, int duration, int price) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.transportType = transportType;
        this.duration = duration;
        this.price = price;
    }
    
    // Getters and setters
    public String getFromStation() { return fromStation; }
    public void setFromStation(String fromStation) { this.fromStation = fromStation; }
    
    public String getToStation() { return toStation; }
    public void setToStation(String toStation) { this.toStation = toStation; }
    
    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }
    
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    
    // Formatted methods for display
    public String getFormattedDuration() {
        return duration + " min";
    }
    
    public String getFormattedPrice() {
        return price + " €";
    }
}


























//package model;
//
//public class RouteSegment {
//    private String from;
//    private String to;
//    private String transportType;
//    private String departureTime;
//    private int duration;
//    private int price;
//
//    public RouteSegment(String from, String to, String transportType, String departureTime, int duration, int price) {
//        this.from = from;
//        this.to = to;
//        this.transportType = transportType;
//        this.departureTime = departureTime;
//        this.duration = duration;
//        this.price = price;
//    }
//
//    // Getters
//    public String getFrom() {
//        return from;
//    }
//
//    public String getTo() {
//        return to;
//    }
//
//    public String getTransportType() {
//        return transportType;
//    }
//
//    public String getDepartureTime() {
//        return departureTime;
//    }
//
//    public int getDuration() {
//        return duration;
//    }
//
//    public int getPrice() {
//        return price;
//    }
//
//    // Utility methods for display
//    public String getFormattedDuration() {
//        int hours = duration / 60;
//        int minutes = duration % 60;
//        return String.format("%dh %02dmin", hours, minutes);
//    }
//
//    public String getFormattedPrice() {
//        return price + " KM";
//    }
//
//    public String getArrivalTime() {
//        // Calculate arrival time based on departure time and duration
//        String[] timeParts = departureTime.split(":");
//        int hour = Integer.parseInt(timeParts[0]);
//        int minute = Integer.parseInt(timeParts[1]);
//        
//        int totalMinutes = hour * 60 + minute + duration;
//        int arrivalHour = (totalMinutes / 60) % 24;
//        int arrivalMinute = totalMinutes % 60;
//        
//        return String.format("%02d:%02d", arrivalHour, arrivalMinute);
//    }
//
//    @Override
//    public String toString() {
//        return String.format("%s -> %s (%s) - %s", from, to, transportType, departureTime);
//    }
//}