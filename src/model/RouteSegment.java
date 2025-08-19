package model;

public class RouteSegment {
    private String from;
    private String to;
    private String transportType;
    private String departureTime;
    private int duration;
    private int price;

    public RouteSegment(String from, String to, String transportType, String departureTime, int duration, int price) {
        this.from = from;
        this.to = to;
        this.transportType = transportType;
        this.departureTime = departureTime;
        this.duration = duration;
        this.price = price;
    }

    // Getters
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTransportType() {
        return transportType;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getPrice() {
        return price;
    }

    // Utility methods for display
    public String getFormattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;
        return String.format("%dh %02dmin", hours, minutes);
    }

    public String getFormattedPrice() {
        return price + " KM";
    }

    public String getArrivalTime() {
        // Calculate arrival time based on departure time and duration
        String[] timeParts = departureTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        int totalMinutes = hour * 60 + minute + duration;
        int arrivalHour = (totalMinutes / 60) % 24;
        int arrivalMinute = totalMinutes % 60;
        
        return String.format("%02d:%02d", arrivalHour, arrivalMinute);
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%s) - %s", from, to, transportType, departureTime);
    }
}