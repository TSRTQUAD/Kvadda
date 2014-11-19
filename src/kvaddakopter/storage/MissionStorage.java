package kvaddakopter.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MissionObject;

public class MissionStorage {
	
	public List<String> getSavedMissions(){
		List<String> storedmissions = new ArrayList<String>();
		File[] files = new File("Missions").listFiles();

		for (File file : files) {
		    if (file.isFile()) {
		        storedmissions.add(file.getName());
		    }
		}
	
		return storedmissions;
	}
	
	
	public void saveMission(MissionObject mission) throws IOException{
		MatFileHandler missionstorage = new MatFileHandler();
		missionstorage.createMatFile(mission.getMissionName(), mission);
		
	}
	
	public MissionObject loadMission(String missionname) throws FileNotFoundException, IOException {
		MatFileHandler missionstorage = new MatFileHandler();
		MissionObject mission = new MissionObject();
		missionstorage.readMatFile(missionname, mission);
		
		return mission;
	}
	
}
