package kvaddakopter.assignment_planer;


public enum MissionType {
	NULL_MISSION(0, ""),
	AROUND_COORDINATE(1, "Circle coordinate"),
	ALONG_TRAJECTORY(2, "Along trajectory"),
	AREA_COVERAGE(3,"Area coverage");
	
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
