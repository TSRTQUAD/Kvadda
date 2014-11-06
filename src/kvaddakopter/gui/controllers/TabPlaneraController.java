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
import kvaddakopter.assignment_planer.MissionType;
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
    private ComboBox<String> listTarget;

    @FXML
    private ComboBox<String> listMissionHeight;

    @FXML
    private Button btnStartMissionCoordinates;

    @FXML
    private Button btnStartForbiddenArea;

    @FXML
    private Button btnSaveMission;
    
    /**
     * Properties
     */
    @SuppressWarnings("unused")
	private PlanningMap planningMap;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.planningMap = new PlanningMap(this.mapView);
        
        this.setEventHandlers();
        
        this.populateListsAndDefaults();
        
        
    }

    
    /**
     * Loads and sets the default values for all GUI options.
     */
    private void populateListsAndDefaults() {
    	
    	this.listMissionType.setItems( FXCollections.observableArrayList(
    			MissionType.ALONG_TRAJECTORY,
    			MissionType.AREA_COVERAGE,
    			MissionType.AROUND_COORDINATE
    			));
    	this.listMissionType.getSelectionModel().select(0);
		
	}


	/**
     * Used to add all event listeners in the planning tab.
     * EXCLUDES MAP EVENTS. THEY ARE HANDLED BY THE PLANNING MAP ABSTRACTION CLASS.
     */
    private void setEventHandlers() {

        // Event triggered when clicking "Save mission" button.
        this.btnSaveMission.setOnAction(e -> {
        	System.out.println(this.listMissionType.getValue() == MissionType.ALONG_TRAJECTORY);
        });

    }
    
    
    
    

}