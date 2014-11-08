package kvaddakopter.gui.controllers;


import com.lynden.gmapsfx.GoogleMapView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
    private Button btnStartMissionCoordinates;

    @FXML
    private Button btnStartMarkForbiddenAreas;

    @FXML
    private Button btnSaveMission;
    
    
    
    /**
     * Properties
     */
    @SuppressWarnings("unused")
	private PlanningMap planningMap;
    
    protected boolean canEnterMissionCoordinates = false;
	protected boolean canEnterForbiddenAreaCoordinates = false;
	
	
	
	
	/**
	 * Public Methods
	 */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
        this.planningMap = new PlanningMap(this.mapView, this);
        
        this.setEventHandlers();
        this.populateListsAndDefaults();
    }
    
    
    public boolean possibleToAddMissionCoordinates(){
    	return this.canEnterMissionCoordinates;
    }
    
    public boolean possibleToAddForbinnenAreaCoordinates(){
    	return this.canEnterForbiddenAreaCoordinates;
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
    			MissionType.ALONG_TRAJECTORY,
    			MissionType.AREA_COVERAGE,
    			MissionType.AROUND_COORDINATE
    			));
    	this.listMissionType.getSelectionModel().select(0);
    	
    	
    	this.listMissionHeight.setItems( FXCollections.observableArrayList(
    			MissionHeight.ONE_METER,
    			MissionHeight.FIVE_METERS,
    			MissionHeight.TEN_METERS
    			));
    	this.listMissionHeight.getSelectionModel().select(0);
    	
    	// Populate template targets
    	this.listTargetTemplate.setItems(FXCollections.observableArrayList(
    			"Av", "Cirkel", "Kvadrat", "Triangel"
    			));
    	this.listTargetTemplate.getSelectionModel().select(0);

    	// Populate color targets
    	this.listTargetColor.setItems(FXCollections.observableArrayList(
    			"Av","Röd", "Grön", "Blå"
    			));
    	this.listTargetColor.getSelectionModel().select(0);
		
	}


	/**
     * Used to add all event listeners in the planning tab.
     * EXCLUDES MAP EVENTS. THEY ARE HANDLED BY THE PLANNING MAP ABSTRACTION CLASS.
     */
    private void setEventHandlers() {

        // Event triggered when clicking "Save mission" button.
        this.btnSaveMission.setOnAction(e -> {
        	System.out.println(this.planningMap.allNavigationCoordinates() );
        	System.out.println(this.planningMap.allForbiddenAreaCoordinates() );
        });
        
        this.btnStartMissionCoordinates.setOnAction(e -> {
        	this.canEnterMissionCoordinates = true;
        	this.canEnterForbiddenAreaCoordinates = false;
        });

        this.btnStartMarkForbiddenAreas.setOnAction(e -> {
        	this.canEnterForbiddenAreaCoordinates = true;
        	this.canEnterMissionCoordinates = false;
        });
    }
    
    
    
    

}