package kvaddakopter.gui.controllers;


import com.lynden.gmapsfx.GoogleMapView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.assignment_planer.MissionType;
import kvaddakopter.gui.components.MissionHeight;
import kvaddakopter.maps.PlanningMap;
import kvaddakopter.storage.MissionStorage;


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
    private ComboBox<String> listTargetTemplate;
    @FXML
    private ComboBox<String> listTargetColor;
    @FXML
    private ToggleGroup descriporRadioGroup;
    @FXML
    private RadioButton radioDescriptor1;
    @FXML
    private RadioButton radioDescriptor2;

    
    /**
     * Properties
     */
	public PlanningMap planningMap;
    
    protected boolean canEnterMissionCoordinates = false;
	protected boolean canEnterForbiddenAreaCoordinates = false;
	
	protected ArrayList<String> targetTemplates = new ArrayList<String>();
	protected ArrayList<String> colorTemplates = new ArrayList<String>();
	
	
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
     * Triggered when user presses btn "Mark new mission coordinates"
     */
    @FXML
    private void btnStartMissionCoordinates(){
    	this.canEnterMissionCoordinates = true;
    	this.canEnterForbiddenAreaCoordinates = false;	
    	this.planningMap.createNewMapShape();
    }
    
    /**
     * Triggered when user presses btn "Mark forbidden areas"
     */
    @FXML
    private void btnStartMarkForbiddenAreas(){
    	this.canEnterForbiddenAreaCoordinates = true;
    	this.canEnterMissionCoordinates = false;
    	this.planningMap.createNewForbiddenArea();
    }
     
    /**
     * Triggered when the user clicks "Save mission"
     */
    @FXML 
    private void btnSaveMission(){
    	
    	
    	//Create new mission object
    	MissionObject mission = new MissionObject();
    	
    	
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
    	
    	//Image template
    	String selectedTemplate = this.listTargetTemplate.getValue();
    	int templateId = this.targetTemplates.indexOf(selectedTemplate);
    	mission.setImageTemplate(templateId);
    	
    	//Image color
    	String selectedColor = this.listTargetColor.getValue();
    	int colorId = this.colorTemplates.indexOf(selectedColor);
    	mission.setColorTemplate(colorId);
    	
    	//Image Descriptor
    	int descriptorId = (int) this.descriporRadioGroup.getSelectedToggle().getUserData();
    	mission.setDescriptor(descriptorId);
    	
    	//GPS AREAS
    	mission.setSearchAreas(this.planningMap.allNavigationCoordinates());
    	mission.setForbiddenAreas(this.planningMap.allForbiddenAreaCoordinates());
    	
    	
    	//Save mission
    	try {
			this.storage.saveMission(mission);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println(this.storage.getSavedMissions().size());
    }
    
    
	/**
	 * Public Methods
	 */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
    			MissionHeight.FIVE_METERS,
    			MissionHeight.TEN_METERS
    			));
    	this.listMissionHeight.getSelectionModel().select(0);
    	
    	// Populate template targets
    	this.targetTemplates.add("Av");
    	this.targetTemplates.add("Cirkel");
    	this.targetTemplates.add("Kvadrat");
    	this.targetTemplates.add("Triangel");
    	this.listTargetTemplate.setItems(FXCollections.observableArrayList(
    			this.targetTemplates
    			));
    	this.listTargetTemplate.getSelectionModel().select(0);

    	// Populate color targets
    	this.colorTemplates.add("Av");
    	this.colorTemplates.add("Röd");
    	this.colorTemplates.add("Grön");
    	this.colorTemplates.add("Blå");
    	this.listTargetColor.setItems(FXCollections.observableArrayList(
    			this.colorTemplates
    			));
    	this.listTargetColor.getSelectionModel().select(0);
    	
    	
    	//Set up descriptors Radios
    	this.radioDescriptor1.setUserData(0);
    	this.radioDescriptor2.setUserData(1);
	}


    


}