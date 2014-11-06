package kvaddakopter.assignment_planer;


public enum MissionType {
	AROUND_COORDINATE(1, "Runt Koordinat"),
	ALONG_TRAJECTORY(2, "Längs bana"),
	AREA_COVERAGE(3,"Inom område");
	
    private final int value;
    private final String name;

    private MissionType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }
    
    public String toString(){
    	return name;
    }
}
