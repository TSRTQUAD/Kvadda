package kvaddakopter.storage;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.MissionObject;

public class MissionStorage {
	
	private ArrayList<MissionObject> savedMissions = new ArrayList<>();
	
	
	public MissionStorage(){
		MissionObject mission = new MissionObject();
		this.savedMissions.add(mission);
	
	}
	
	
	public boolean saveMission(MissionObject mission){
		// IMPLEMENT LOGIC TO ACTUALLY STORE A MISSION!!
		
		return savedMissions.add(mission);
		
	}
	
	public ArrayList<MissionObject> getSavedMissions(){
		return this.savedMissions;
	}
	
}
