package kvaddakopter.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MissionObject;

public class MissionStorage {
	
	public ArrayList<String> getListOfSavedMissions() {
		ArrayList<String> listofstoredmissions = new ArrayList<String>();
		ArrayList<File> files = this.listMatFiles();

		for (File file : files) {
		    if (file.isFile()) {
		        listofstoredmissions.add(file.getName().replaceFirst("\\.mat$", ""));
		    }
		}
	
		return listofstoredmissions;
	}
	
	public ArrayList<MissionObject> getSavedMissions() throws FileNotFoundException, IOException{
		ArrayList<MissionObject> storedmissions = new ArrayList<MissionObject>();
		ArrayList<File> files = this.listMatFiles();
		
		for (File file : files) {
		    if (file.isFile()) {
		        storedmissions.add( loadMission(file.getName().replaceFirst("\\.mat$", "")) );
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
	
	
	private ArrayList<File> listMatFiles(){
		File[] files = new File("Missions").listFiles();
		ArrayList<File> checkedFile = new ArrayList<>();
		for (File file : files){
			String fileName = file.getName();
			if( fileName.matches(".*\\.mat$")){
				 checkedFile.add(file);
			}
		}
		return checkedFile;
	}
	
}
