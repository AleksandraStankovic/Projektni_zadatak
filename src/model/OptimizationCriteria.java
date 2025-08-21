package model;

public enum OptimizationCriteria {
    SHORTEST_TIME("Najkraće vrijeme"),
    LOWEST_COST("Najniža cijena"), 
    FEWEST_TRANSFERS("Najmanje presjedanja");
    
    private final String displayName;
    
    OptimizationCriteria(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static OptimizationCriteria fromDisplayName(String displayName) {
        for (OptimizationCriteria criteria : values()) {
            if (criteria.displayName.equals(displayName)) {
                return criteria;
            }
        }
        return SHORTEST_TIME; // default
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}















//package model;
//
//public enum OptimizationCriteria {
//
//	SHORTEST_TIME("Najkraće vrijeme"), 
//	LOWEST_COST("Najniža cijena"),
//	FEWEST_TRANSFERS("Najmanje presjedanja");
//	
//	private final String displayName; 
//	
//	OptimizationCriteria(String displayName)
//	{
//		this.displayName=displayName;
//	}
//	
//	@Override
//	public String toString()
//	{
//		return displayName; 
//	}
//	
//	public static OptimizationCriteria fromDisplayName(String text) {
//        for (OptimizationCriteria criteria : values()) {
//            if (criteria.displayName.equals(text)) {
//                return criteria;
//            }
//        }
//        throw new IllegalArgumentException("Unknown criteria: " + text);
//    }
//	
//	
//}
