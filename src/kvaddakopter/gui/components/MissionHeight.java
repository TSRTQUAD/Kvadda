package kvaddakopter.gui.components;

public enum MissionHeight {
	ONE_METER("1 meter", 1), 
	FIVE_METERS("5 meter", 5), 
	TEN_METERS("10 meter", 10);

	private String displayName;

	private int value;

	private MissionHeight(String displayName, int value){
		this.displayName = displayName;
		this.value = value;
	}


	public String toString(){
		return this.displayName;
	}

	public int getValue(){
		return this.value;
	}
}
