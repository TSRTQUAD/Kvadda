package kvaddakopter.gui.controllers;


import com.lynden.gmapsfx.GoogleMapView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import storage.MissionStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.assignment_planer.MissionType;
import kvaddakopter.gui.components.MissionHeight;
import kvaddakopter.maps.PlanningMap;


public class TabPlaneraController implements Initializable {


	/**
	 * UI ELEMENTS
	 */
    @FXML
    private GoogleMapView mapView;

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
	private PlanningMap planningMap;
    
    protected boolean canEnterMissionCoordinates = false;
	protected boolean canEnterForbiddenAreaCoordinates = false;
	
	protected ArrayList<String> targetTemplates = new ArrayList<String>();
	protected ArrayList<String> colorTemplates = new ArrayList<String>();
	
	
	protected MissionStorage storage;
	
	
	protected MissionType currentSelectedMissionType;
	
	
    /**
     * GUI events
     */
	
	
    @FXML
    private void missionTypeChanged()
    {
    	this.currentSelectedMissionType = this.listMissionType.getSelectionModel().getSelectedItem();
    	this.planningMap.clearNavigationCoordinates();
    }
    
    @FXML
    private void btnClickedClearNagivationCoordinates(){
    	this.planningMap.clearNavigationCoordinates();
    }
    
    @FXML
    private void btnClickedClearForbiddenAreasCoodinates(){
    	this.planningMap.clearForbiddenAreasCoordinates();
    }
    
    @FXML
    private void btnStartMissionCoordinates(){
    	this.canEnterMissionCoordinates = true;
    	this.canEnterForbiddenAreaCoordinates = false;	
    }
    
    @FXML
    private void btnStartMarkForbiddenAreas(){
    	this.canEnterForbiddenAreaCoordinates = true;
    	this.canEnterMissionCoordinates = false;
    }
     
    
    @FXML 
    private void btnSaveMission(){
    	
    	MissionObject mission = new MissionObject();
    	
    	mission.setMissionName(this.txtMissionName.getText());
    	
    	mission.mission(this.listMissionType.getValue());
    	
    	double[] height = {(double) this.listMissionHeight.getValue().getValue()};
    	mission.setHeight(height);
    	
    	double[] radiusValue = {(double) 5};
    	mission.setRadius(radiusValue);
    	
    	mission.setSearchAreas(this.planningMap.allNavigationCoordinates());
    	mission.setForbiddenAreas(this.planningMap.allForbiddenAreaCoordinates());
    	
    	String selectedTemplate = this.listTargetTemplate.getValue();
    	int templateId = this.targetTemplates.indexOf(selectedTemplate);
    	mission.setImageTemplate(templateId);
    	
    	String selectedColor = this.listTargetColor.getValue();
    	int colorId = this.colorTemplates.indexOf(selectedColor);
    	mission.setColorTemplate(colorId);
    	
    	
    	int descriptorId = (int) this.descriporRadioGroup.getSelectedToggle().getUserData();
    	mission.setDescriptor(descriptorId);
    	
    	mission.setSearchAreas(this.planningMap.allNavigationCoordinates());
    	mission.setForbiddenAreas(this.planningMap.allForbiddenAreaCoordinates());
    	
    	
    	//Save mission
    	this.storage.saveMission(mission);
    }
    
    
	/**
	 * Public Methods
	 */
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
        this.planningMap = new PlanningMap(this.mapView, this);
        this.storage = new MissionStorage();
        
        this.populateListsAndDefaults();
    }
    
    
    /**
     * Used by map to determine if the user specified that he/she want to add Mission Coordinates.
     * @return boolean If we are on this Mode
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