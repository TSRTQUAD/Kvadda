package kvaddakopter.assignment_planer;

/**
 * Defines the mission type. Meaning among other things for example witch calculation mode <br>
 * the assignmentplaner should use.
 * @author tobiashammarling
 *
 */
public enum MissionType {
	NULL_MISSION(0, ""),
	AROUND_COORDINATE(1, "Circle coordinate"),
	ALONG_TRAJECTORY(2, "Along trajectory"),
	AREA_COVERAGE(3,"Area coverage");
	
    private final int value;
    private final String name;

    /**
     * Constructor for the MissionType class.
     * @param value
     * @param name
     */
    private MissionType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Returns the ID-value connected to the mission type.
     * @return
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Returns the name connected to the mission type.
     * @return
     */
    public String toString(){
    	return name;
    }
}
