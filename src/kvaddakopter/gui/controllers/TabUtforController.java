package kvaddakopter.gui.controllers;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.maps.GPSCoordinate;
import kvaddakopter.maps.MissionMap;
import kvaddakopter.storage.MissionStorage;
import kvaddakopter.utils.SecToMinSec;

import com.lynden.gmapsfx.GoogleMapView;



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
    private Label lblCoverageArea;
    
    @FXML
    private Button btnArm;
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
    @FXML
    private ImageView imgMovie;
    
    
	/**
     * Properties
     */
    
    
    /**
     * Abstraction of the mission map in this controller.
     */
	public MissionMap missionMap;
	
	
	/**
	 * List of all current available missions.
	 */
    private ArrayList<String> listOfMissions;
    
    
    /**
     * Represent the current selected in the listOfMissions drop down.
     */
    private MissionObject currentSelectedMissionObject;
    
    
    /**
     * Represents the current selected Mission Name as as string.
     */
    private String currentSelectedMissionName;
    
    
    /**
     * Direct reference to the storage module.
     */
    private MissionStorage missionStorage = new MissionStorage();
    
    
    /**
     * The counter to represent the time left of a mission.
     */
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
    	this.getParent().getMainBus().setMissionObject(this.currentSelectedMissionObject);
    	this.drawMission();
    	this.lblMissionType.setText(this.currentSelectedMissionObject.getMissionType().toString());
    	this.lblEstimatedDistance.setText(String.valueOf((int) this.currentSelectedMissionObject.getTrajectoryLength()[0][0])+ " m");
    	this.lblEstimatedTime.setText(SecToMinSec.transform((long) this.currentSelectedMissionObject.getMissionTime()[0][0]));
    }
    
    
    /**
     * Event when user clicks Execute button.
     */
    @FXML
    private void startMission(){
    	this.getParent().getMainBus().setShouldStart(false);
    	this.getParent().getMainBus().setIsStarted(true);
    	synchronized (this.getParent().getMainBus()) {
			this.getParent().getMainBus().notifyAll();
		}
    }
    
    
    /**
     * Event when user presses Arm button.
     */
    @FXML
    private void arm(){
    	if(this.currentSelectedMissionObject == null) return;
    	this.timeLeft = (long) this.currentSelectedMissionObject.getMissionTime()[0][0];
    	System.out.println("Started");
    	System.out.println(this.currentSelectedMissionName);
    	this.getParent().getMainBus().setIsStarted(false);
    	this.getParent().getMainBus().setShouldStart(true);
    	synchronized (this.getParent().getMainBus()) {
			this.getParent().getMainBus().notifyAll();
		}
    }
    
    
    /**
     * Event when user clicks Abort mission button.
     */
    @FXML
    private void abortMission(){
    	this.getParent().getMainBus().setEmergencyStop(true);
    }
    
    
    /**
     * Event when user clicks the emergency button.
     */
    @FXML
    private void emergency(){
    	this.getParent().getMainBus().setEmergencyStop(true);
    }

    
    
    /**
     * Event when the user presses the toggle auto/manual button.
     */
    @FXML
    private void toggleControl(){
    	boolean automatic = this.getParent().getMainBus().toggleController();
    	String showText = (automatic) ? "Auto" : "Manual";
    	this.btnToggleControl.setText(showText);
    }

    
    /**
     * Update the current Image view to the current
     * @param currentImage
     */
    public void updateMovie(){

    	if(this.getParent().getMainBus() == null) return;
		Image image = this.getParent().getMainBus().getIPImageToShow();
		if(image != null){
			this.imgMovie.setImage(image);
			this.imgMovie.autosize();
			//this.imgMovie.setScaleX(.5);
			//this.imgMovie.setScaleY(.5);
			this.imgMovie.toFront();
		}
    }
    
    
    /**
     * Update the clock that shows the time left.
     * @param passedTime
     */
	public void updateTimeLeft(long passedTime){
		this.timeLeft -= (long) passedTime/1000;
		long newTime = this.timeLeft;
		if(newTime < 0){
			this.lblTimeLeft.setText("- s");
		} else {
			this.lblTimeLeft.setText(SecToMinSec.transform( Math.max(0, newTime)));
		}
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
     * Populates GUI default values
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
		this.lblCoverageArea.setText("");
		
		
		this.cmbListOfMissions.setItems( FXCollections.observableArrayList(
				this.listOfMissions
				));
		//this.cmbListOfMissions.getSelectionModel().select(0);
	}
	
	
	/**
	 * Uses the current available missions and update the drop down list with it's content.
	 */
	public void updateMissionList(){
		this.loadFromStorage();
		this.cmbListOfMissions.setItems( FXCollections.observableArrayList(
				this.listOfMissions
				));
	}
	
    
    /**
     * Draw the mission coordinates to the map. 
     */
    private void drawMission() {
    	this.missionMap.drawResultingTrajectory(this.currentSelectedMissionObject.getTrajectoryFullSize());
	}
    
    
    /**
     * Draw the Quad to the map. 
     * Fetches the current Quad position and draws a quadmarker on the map.
     */
    public void drawQuadMarker(){
    	if(this.getParent().getMainBus().getQuadData() == null) return;
    	GPSCoordinate gps = this.getParent().getMainBus().getCurrentQuadPosition();
    	if(gps == null || this.missionMap == null) return;
    	this.missionMap.drawQuad(gps.getLatitude(), gps.getLongitude());
    }
    
    
    /**
     * Draw targets to the Map.
     * Fetches the targetlist of the mainbus and draws all on the mission map.
     */
    public void drawTargetsOnMap(){
    	HashMap<String, GPSCoordinate> targetList = this.getParent().getMainBus().getTargets();
    	if(targetList == null || this.missionMap == null) return;
		this.missionMap.drawTargetsOnMap(this.getParent().getMainBus().getTargets());
    }
    
    
    /**
     * Sets the status text on the GPS status label.
     * @param isOk 
     */
	public void updateGPSStatus(boolean isOk) {
		String status = (isOk) ? "GPS: OK!" : "GPS: NOT OK!";
		this.lblGPS.setText(status);
	}

	
	/**
	 * Sets the status text on the WIFI status label.
	 * @param isOk
	 */
	public void updateWIFIStatus(boolean isOk) {
		String status = (isOk) ? "WIFI: OK!" : "WIFI: NOT OK!";
		this.lblWIFI.setText(status);
	}
	
	
	/**
	 * Updates the speed label to the current speed.
	 */
	public void updateSpeed(){
		if (this.getParent().getMainBus() != null){
			if(this.getParent().getMainBus().getCurrentSpeed() < 0){
				this.lblSpeed.setText("- m/s");
			} else {
				this.lblSpeed.setText(String.format("%.1f m/s", this.getParent().getMainBus().getCurrentSpeed()));
			}
		}
	}
	
	
	/**
	 * Updates the battery percent.
	 * @param newBattery
	 */
	public void updateBattery(float newBattery){
		if(newBattery < 0){
			this.lblBattery.setText("- %");
		}
		else if(newBattery < 15){
			this.lblBattery.setText(String.format("WRN! %.1f %%", newBattery));
		} else {
			this.lblBattery.setText(String.format("%.1f %%", newBattery));
		}
	}
	
	
	/**
	 * Checks the mainbus and sets the buttons disable status according to the current state.
	 */
	public void updateButtons(){
		if(this.getParent().getMainBus() == null) return;
		else if(this.getParent().getMainBus().getIsArmed()){
			btnStartMission.setDisable(false);
		}
		else{
			btnStartMission.setDisable(true);
		}
	}
	
	
	/**
	 * Updates the area coverage amount and the percentage to the current value.
	 */
	public void updateCoverage(){
		MainBusGUIInterface mainbus = this.getParent().getMainBus();
		if(mainbus == null || mainbus.getMissionObject() == null || mainbus.getMissionObject().getTrajectory() == null) return;
		double realnrofpoints = mainbus.getMissionObject().getTrajectory().length;
		
		double nrofvisitedpoints = mainbus.getVisitedPoints();
		double percent = Math.max(Math.min(nrofvisitedpoints/realnrofpoints, 1), 0)*100;	
		double coveragearea = percent*mainbus.getMissionObject().getCoverageArea()[0][0]/100;
		
		String text =  (int)percent + " % ("+ String.valueOf((int)coveragearea) +" m2)";
		this.lblCoverageArea.setText(text);
	}
    
}

