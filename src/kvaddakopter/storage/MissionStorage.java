package kvaddakopter.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MissionObject;

public class MissionStorage {
	
	private ArrayList<MissionObject> savedMissions = new ArrayList<>();
	
	
	public MissionStorage(){
		MissionObject mission = new MissionObject();
		this.savedMissions.add(mission);
	
	}
	
	
	public boolean saveMission(MissionObject mission) throws IOException{
		// IMPLEMENT LOGIC TO ACTUALLY STORE A MISSION!!
		MatFileHandler missionstorage = new MatFileHandler();
		missionstorage.createMatFile(mission.getMissionName(), mission);
		
		return savedMissions.add(mission);
		
	}
	
	public MissionObject loadMission(String missionname) throws FileNotFoundException, IOException {
		MatFileHandler missionstorage = new MatFileHandler();
		MissionObject mission = new MissionObject();
		missionstorage.readMatFile(missionname, mission);
		
		return mission;
	}
	
	public ArrayList<MissionObject> getSavedMissions(){
		return this.savedMissions;
	}
	
}
