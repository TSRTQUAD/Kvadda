package kvaddakopter.storage;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.MissionObject;

public class MissionStorage {
	
	
	public MissionStorage(){
		
	}
	
	
	public boolean saveMission(MissionObject mission){
		// IMPLEMENT LOGIC TO ACTUALLY STORE A MISSION!!
		System.out.println("Mission Saved:");
		System.out.println(mission.toString());
		return true;
	}
	
	public ArrayList<MissionObject> getSavedMissions(){
		ArrayList<MissionObject> missions = new ArrayList<MissionObject>();
		
		return missions;
	}
	
}
