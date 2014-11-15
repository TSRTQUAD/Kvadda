package kvaddakopter.gui.controllers;


import com.lynden.gmapsfx.GoogleMapView;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import kvaddakopter.maps.MissionMap;



public class TabUtforController implements Initializable {

	
	/**
	 * UI ELEMENTS
	 */
    @FXML
    private GoogleMapView mapViewUtfor; 
    
    /**
     * Properties
     */
	
	private MissionMap missionMap;
	

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	
    
        this.missionMap = new MissionMap(this.mapViewUtfor, this);
        
    }
    
    

    
    

    


}