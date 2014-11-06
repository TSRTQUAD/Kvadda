package kvaddakopter.assignment_planer;

public enum MissionType{


	AROUND_COORDINATE("Runt Koordinat"),
	ALONG_TRAJECTORY("Längs bana"),
	AREA_COVERAGE("Inom område");
	
	
	private String name;
	
	private MissionType(String name){
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}


}
