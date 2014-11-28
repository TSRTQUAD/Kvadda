package kvaddakopter.gui.controllers;


import com.lynden.gmapsfx.GoogleMapView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.maps.GPSCoordinate;
import kvaddakopter.maps.MissionMap;
import kvaddakopter.storage.MissionStorage;
import kvaddakopter.utils.SecToMinSec;



public class TabUtforController extends BaseController implements Initializable {

	
	/**
	 * UI ELEMENTS
	 */
    @FXML
    public AnchorPane mapContainer;

    public GoogleMapView mapViewUtfor; 

    @FXML 
    private Label lblMissionType;
    @FXML
    private Label lblEstimatedTime;
    @FXML
    private Label lblEstimatedDistance;
    @FXML
    private Label lblTimeLeft;
    @FXML
    private Label lblSpeed;
    @FXML
    private Label lblBattery;
    @FXML
    private Label lblGPS;
    @FXML
    private Label lblWIFI;
    
    @FXML
    private Button btnStartMission;
    @FXML
    private Button btnAbortMission;
    @FXML
    private Button btnToggleControl;
    @FXML
    private Button btnEmergency;
    @FXML
    private ComboBox<String> cmbListOfMissions;
    
	/**
     * Properties
     */
	
	public MissionMap missionMap;
	 
    private ArrayList<String> listOfMissions;
    private MissionObject currentSelectedMissionObject;
    private String currentSelectedMissionName;
    
    private MissionStorage missionStorage = new MissionStorage();

    
    private long timeLeft = 0;
 
    /**
     * UI Events
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @FXML
    private void changeSelectedMission() throws FileNotFoundException, IOException{
    	this.currentSelectedMissionName = this.cmbListOfMissions.getSelectionModel().getSelectedItem();
    	this.currentSelectedMissionObject = this.missionStorage.loadMission(this.currentSelectedMissionName);
    	
    	this.drawMission();
    	this.lblMissionType.setText(this.currentSelectedMissionObject.getMissionType().toString());
    	this.lblEstimatedDistance.setText(String.valueOf((int) this.currentSelectedMissionObject.getTrajectoryLength()[0][0])+ " m");
    	this.lblEstimatedTime.setText(SecToMinSec.transform((long) this.currentSelectedMissionObject.getMissionTime()[0][0]));
    }

    @FXML
    private void startMission(){
    	this.timeLeft = (long) this.currentSelectedMissionObject.getMissionTime()[0][0];
    	System.out.println("Started");
    	System.out.println(this.currentSelectedMissionName);
    	this.getParent().getMainBus().setShouldStart(true);
    	synchronized (this.getParent().getMainBus()) {
			this.getParent().getMainBus().notifyAll();
		}
    }
    
    
    @FXML
    private void abortMission(){
    	this.getParent().getMainBus().setIsStarted(false);
    }
    
    
    @FXML
    private void emergency(){
    	this.getParent().getMainBus().setEmergencyStop(true);
    }

    
    @FXML
    private void toggleControl(){
    	this.getParent().getMainBus().toggleController();
    }

    
    /**
     * Update the clock that shows the time left.
     * @param passedTime
     */
	public void updateTimeLeft(long passedTime){
		 this.timeLeft -= (long) passedTime/1000;
		long newTime = this.timeLeft;
		
		this.lblTimeLeft.setText( SecToMinSec.transform( Math.max(0, newTime)));
	}
	

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    	// this.missionMap = new MissionMap(this.mapViewUtfor, this);
    	this.loadFromStorage();
    	this.populateDefaultLists();
    }
    
    
    /**
     * Loads all needed things from the persistence layer.
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private void loadFromStorage() {
    	this.listOfMissions =  this.missionStorage.getListOfSavedMissions();
    }
    
    
    /**
     * Populates GUI elementsValues
     */
	private void populateDefaultLists() {
		
		//Info Labels
		this.lblMissionType.setText("");
		this.lblEstimatedTime.setText("");
		this.lblEstimatedDistance.setText("");
		this.lblTimeLeft.setText("");
		this.lblSpeed.setText("");
		this.lblBattery.setText("");
		this.lblGPS.setText("");
		this.lblWIFI.setText("");
		
		
		this.cmbListOfMissions.setItems( FXCollections.observableArrayList(
				this.listOfMissions
				));
		//this.cmbListOfMissions.getSelectionModel().select(0);
	}
	
	public void updateMissionList(){
		this.loadFromStorage();
		this.cmbListOfMissions.setItems( FXCollections.observableArrayList(
				this.listOfMissions
				));
	}
	
    
    /**
     * Draw the mission coordinates to the map Map
     */
    private void drawMission() {
    	this.missionMap.drawResultingTrajectory(this.currentSelectedMissionObject.getTrajectoryFullSize());
	}
    
    /**
     * Draw the Quad to the map.
     */
    public void drawQuadMarker(){
    	GPSCoordinate gps = this.getParent().getMainBus().getCurrentQuadPosition();
    	this.missionMap.drawQuad(gps.getLatitude(), gps.getLongitude());
    }
    
    /**
     * Draw targets to the Map
     */
    public void drawTargetsOnMap(){
    	if (this.getParent().getMainBus().getTargets() != null){
                    this.missionMap.drawTargetsOnMap(this.getParent().getMainBus().getTargets());
    	}
    }
    
//    /**
//     * TODO Draw the Target to the map.
//     */  
//    public void drawTargetMarker(){
//		HashMap<String,GPSCoordinate> targetMap = this.getParent().getMainBus().getTargets();
//    	GPSCoordinate gps = this.getParent().getMainBus().getCurrentQuadPosition();
//    	this.missionMap.drawTarget(gps.getLatitude(), gps.getLongitude());
//    }

	public void updateGPSStatus(boolean isOk) {
		String status = (isOk) ? "GPS: OK!" : "GPS: NOT OK!";
		this.lblGPS.setText(status);
	}

	public void updateWIFIStatus(boolean isOk) {
		String status = (isOk) ? "WIFI: OK!" : "WIFI: NOT OK!";
		this.lblWIFI.setText(status);
	}
	
	public void updateSpeed(float newSpeed){
		this.lblSpeed.setText(String.format("%.1f m/s", newSpeed));
	}

	 
	public void updateBattery(float newBattery){
		if(newBattery < 15){
			this.lblBattery.setText(String.format("WRN! %.1f %%", newBattery));
		} else {
			this.lblBattery.setText(String.format("%.1f %%", newBattery));
		}
	}
	
	
    
}

