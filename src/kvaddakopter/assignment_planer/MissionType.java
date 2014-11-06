package kvaddakopter.assignment_planer;

public enum MissionType {
	AROUND_COORDINATE(1),
	ALONG_TRAJECTORY(2),
	AREA_COVERAGE(3);
	
    private final int value;
    private MissionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
