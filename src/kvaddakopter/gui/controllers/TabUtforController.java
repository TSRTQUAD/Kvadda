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

    
    private boolean shouldStart = false;
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
    	this.shouldStart = true;
    	this.timeLeft = (long) this.currentSelectedMissionObject.getMissionTime()[0][0];
    	this.missionMap.drawResultingTrajectory(this.currentSelectedMissionObject.getTrajectoryFullSize());
    }
    
    @FXML
    private void abortMission(){
    	this.shouldStart = false;
    }
    
    @FXML
    private void emergency(){
    	this.getParent().getMainBus().setEmergencyStop(true);
    }

    @FXML
    private void toggleControl(){
    	this.getParent().getMainBus().toggleController();
    }

	
	public boolean shouldStart(){
		return this.shouldStart;
	}
	
	
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
     * Draw the Target to the map.
     */
    public void drawTargetMarker(){
    	GPSCoordinate gps = this.getParent().getMainBus().getCurrentQuadPosition();
    	this.missionMap.drawTarget(gps.getLatitude(), gps.getLongitude());
    }

	public void updateGPSStatus() {
		// TODO Auto-generated method stub
	}

	public void updateWIFIStatus() {
		// TODO Auto-generated method stub
		
	}
 
	public void updateSpeed(float newSpeed){
		this.lblSpeed.setText(String.format("%.1f", newSpeed));	
		System.out.println("halla");
	}

    
    

    


}