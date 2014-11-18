package kvaddakopter.gui.controllers;


import com.lynden.gmapsfx.GoogleMapView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    private GoogleMapView mapViewUtfor; 
    
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
    private ComboBox<MissionObject> cmbListOfMissions;
    
    
    /**
     *  Properties
     */
    private ArrayList<MissionObject> listOfMissions;
    private MissionObject currentSelectedMissionObject;
    
    private MissionStorage missionStorage = new MissionStorage();
    
    
    private boolean shouldStart = false;
    private long timeLeft = 0;
 
    /**
     * UI Events
     */
    @FXML
    private void changeSelectedMission(){
    	this.currentSelectedMissionObject = this.cmbListOfMissions.getSelectionModel().getSelectedItem();
    	this.drawMission();
    }

    @FXML
    private void startMission(){
    	this.shouldStart = true;
    	this.timeLeft = 5000; // (long) this.currentSelectedMissionObject.getMissionTime()[0][0];
    }
    
    @FXML
    private void abortMission(){
    	this.shouldStart = false;
    }
    
	/**
     * Properties
     */
	
	private MissionMap missionMap;
	
	
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
    	
        //this.missionMap = new MissionMap(this.mapViewUtfor, this);
        this.loadFromStorage();
        this.populateDefaultLists();
    }
    
    
    /**
     * Loads all needed things from the persistence layer.
     */
    private void loadFromStorage(){
    	this.listOfMissions =  this.missionStorage.getSavedMissions();
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
		this.cmbListOfMissions.getSelectionModel().select(0);
	}
	
	
    
    /**
     * Draw the mission coordinates to the map Map
     */
    private void drawMission() {
    	//TODO: Implement drawing.
	}
    
    /**
     * Draw the Quad to the map.
     */
    public void drawQuadMarker(){
    	GPSCoordinate gps = this.getParent().getMainBus().getCurrentQuadPosition();
    	this.missionMap.drawQuad(gps.getLatitude(), gps.getLongitude());
    }
 
    

    
    

    


}