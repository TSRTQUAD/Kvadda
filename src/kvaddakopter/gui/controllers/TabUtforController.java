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
    private Button btnStartMission;
    @FXML
    private Button btnAbortMission;
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
 
    

    
    

    


}