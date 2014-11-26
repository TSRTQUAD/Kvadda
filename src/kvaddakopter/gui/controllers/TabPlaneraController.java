package kvaddakopter.gui.controllers;


import com.lynden.gmapsfx.GoogleMapView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.assignment_planer.MissionType;
import kvaddakopter.gui.components.MissionHeight;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.maps.PlanningMap;
import kvaddakopter.storage.MissionStorage;
import kvaddakopter.utils.SecToMinSec;


public class TabPlaneraController extends BaseController implements Initializable {


	/**
	 * UI ELEMENTS
	 */
    @FXML
    public AnchorPane mapContainer;

    public GoogleMapView mapView;
    
    @FXML
    private TextField txtMissionName;
    @FXML
    private ComboBox<MissionType> listMissionType;
    @FXML
    private ComboBox<MissionHeight> listMissionHeight;
    @FXML
    private TextField txtEstimatedDistance;
    @FXML
    private TextField txtEstimatedTime;
    
    /**
     * Properties
     */
	public PlanningMap planningMap;
    
    protected boolean canEnterMissionCoordinates = false;
	protected boolean canEnterForbiddenAreaCoordinates = false;
	protected boolean canEnterQuadStartPosition = true;
	
	
	protected MissionStorage storage;
	
	
	protected MissionType currentSelectedMissionType;
	
	
    /**
     * GUI events
     */
	
	
	/**
	 * Triggered when missionType Combobox is changed
	 */
    @FXML
    private void missionTypeChanged()
    {
    	this.currentSelectedMissionType = this.listMissionType.getSelectionModel().getSelectedItem();
    	if (this.planningMap != null){
                this.planningMap.clearNavigationCoordinates();
    	}
    }
    
    /**
     * Triggered when user presses btn "Clear mission areas"
     */
    @FXML
    private void btnClickedClearNagivationCoordinates(){
    	this.planningMap.clearNavigationCoordinates();
    }
    
    /**
     * Triggered when user presses btn "Clear forbidden areas"
     */
    @FXML
    private void btnClickedClearForbiddenAreasCoodinates(){
    	this.planningMap.clearForbiddenAreasCoordinates();
    }
    
    /**
     * Clear the set quad position
     */
    @FXML
    private void btnClickedClearQuadStartPosition(){
    	this.planningMap.clearQuadStartPosition();
    }
    
    /**
     * Triggered when user presses btn "Mark new mission coordinates"
     */
    @FXML
    private void btnStartMissionCoordinates(){
    	this.canEnterMissionCoordinates = true;
    	this.canEnterForbiddenAreaCoordinates = false;	
    	this.canEnterQuadStartPosition = false;	
    	this.planningMap.createNewMapShape();
    }
    
    /**
     * Triggered when user presses btn "Mark forbidden areas"
     */
    @FXML
    private void btnStartMarkForbiddenAreas(){
    	this.canEnterForbiddenAreaCoordinates = true;
    	this.canEnterMissionCoordinates = false;
    	this.canEnterQuadStartPosition = false;	
    	this.planningMap.createNewForbiddenArea();
    }

    /**
     * Triggered when user presses btn "New Quad start postion"
     */
    @FXML
    private void btnStartMarkQuadStartPosition(){
    	this.canEnterQuadStartPosition = true;	
    	this.canEnterForbiddenAreaCoordinates = false;
    	this.canEnterMissionCoordinates = false;
    } 
    
    @FXML
    private void btnGenerateTrajectory(){
    	//Get mission data from GUI and set it to MainBus
    	setMissionDataFromGUI();
    	
    	//Get MainBus
    	MainBusGUIInterface mainbus = this.getParent().getMainBus();
    	
    	//Start AssignmentPlaner
    	mainbus.setAssignmentPlanerOn(true);
		synchronized(mainbus){
			mainbus.notify ();
		}
    	
    	//Check for results from AssignmentPlaner
		while ( mainbus.isAssignmentPlanerOn() )
			try{
				synchronized(mainbus){
					mainbus.wait();
				}

			}
		catch (InterruptedException e) {}
		
		System.out.println("Results retrived");
		MissionObject mission = mainbus.getMissionObject();
		
		this.txtEstimatedTime.setText(String.format("%s" , SecToMinSec.transform( (long) mission.getMissionTime()[0][0])));
		this.txtEstimatedDistance.setText(String.format("%s m", (int)mission.getTrajectoryLength()[0][0]));
		this.planningMap.drawResultingTrajectory(mission.getTrajectoryFullSize());
    }
    
    /**
     * Triggered when the user clicks "Save mission"
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    @FXML 
    private void btnSaveMission() {
    	
    	//Get current mission from MainBus
    	MissionObject mission = this.getParent().getMainBus().getMissionObject();

    	//Save mission
    	try {
			this.storage.saveMission(mission);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    
	/**
	 * Public Methods
	 */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	System.out.println("Planera DONE");
    	
        this.storage = new MissionStorage();
        this.populateListsAndDefaults();
    }
    
    
    /**
     * Used by map to determine if the user specified that he/she want to add Mission Coordinates.
     * @return boolean If we are in this Mode
     */
    public boolean addMissionCoordinatesMode(){
    	return this.canEnterMissionCoordinates;
    }
    
    /**
     * Used by map to determine if the user specified that he/she want to add Forbidden Area Coordinates.
     * @return boolean If we are on this Mode
     */
    public boolean addForbiddenAreasMode(){
    	return this.canEnterForbiddenAreaCoordinates;
    }
    
    /**
     * Used by map to determine if the user specified that he/she want to add Forbidden Area Coordinates.
     * @return boolean If we are on this Mode
     */
    public boolean addQuadStartPositionMode(){
    	return this.canEnterQuadStartPosition;
    }

    
    public MissionType getCurrentSelectedMissionType(){
    	return this.currentSelectedMissionType;
    }
    
    /**
     * Private Methods
     */
    
    /**
     * Loads and sets the default values for all GUI options.
     */
    private void populateListsAndDefaults() {
    	
    	//  Populate list of MissionTypes
    	this.listMissionType.setItems( FXCollections.observableArrayList(
    			MissionType.AROUND_COORDINATE,
    			MissionType.ALONG_TRAJECTORY,
    			MissionType.AREA_COVERAGE
    			));
    	this.listMissionType.getSelectionModel().select(0);
    	
    	this.listMissionHeight.setItems( FXCollections.observableArrayList(
    			MissionHeight.ONE_METER,
    			MissionHeight.THREE_METERS,
    			MissionHeight.FIVE_METERS,
    			MissionHeight.TEN_METERS
    			));
    	this.listMissionHeight.getSelectionModel().select(1);
    	    	
	}
    
    
    
    /**
     * Get all the mission data from the GUI
     * @return
     */
	public void setMissionDataFromGUI() {
		
    	//Create new mission object
    	MissionObject mission = new MissionObject();
    	
    	//Temporary set startcoordinates
    	mission.setStartCoordinate(this.planningMap.getStartCoordinate());
    	
    	//Mission Name
    	mission.setMissionName(this.txtMissionName.getText());
    	
    	//MissionType
    	mission.setMissionType(this.listMissionType.getValue());
    	
    	//Mission Height
    	double[] height = {(double) this.listMissionHeight.getValue().getValue()};
    	mission.setHeight(height);
    	
    	//Mission Radius
    	double[] radiusValue = this.planningMap.getCircleRadius();
    	mission.setRadius(radiusValue);
    	
    	//GPS AREAS
    	mission.setSearchAreas(this.planningMap.allNavigationCoordinates());
    	mission.setForbiddenAreas(this.planningMap.allForbiddenAreaCoordinates());
    	
    	//Set mission to MainBus
    	this.getParent().getMainBus().setMissionObject(mission);
		
	}




}