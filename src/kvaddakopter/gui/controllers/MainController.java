package kvaddakopter.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import kvaddakopter.gui.GUIWorker;
import kvaddakopter.interfaces.IPAndGUIInterface;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.maps.MissionMap;
import kvaddakopter.maps.PlanningMap;

import com.lynden.gmapsfx.GoogleMapView;

public class MainController implements Initializable {


	/**
	 * View responsible for Planning Tab.
	 */
	@FXML
	private Parent tabPlanera;


	/**
	 * Controller responsible for Running Tab.
	 */
	@FXML
	public TabPlaneraController tabPlaneraController;


	/**
	 * View responsible for  Running Tab.
	 */
	@FXML
	private Parent tabUtfor;
	

	/**
	 * Controller responsible for Running Tab.
	 */
	@FXML
	public TabUtforController tabUtforController;
	
	/**
	 * View that contains GUI for  computer vision
	 */
	@FXML
	public AnchorPane tabDatorseende;
	
	
	/**
	 * Controller representing the tab for computer vision.
	 */
	@FXML 
	public TabDatorseendeController tabDatorseendeController;
	
	
	/**
	 * A reference to the overall Tab object
	 */
	@FXML
	public TabPane mainTabPane;
	
	
	
	/**
	 * When the Execute mission tab is selected this runs once
	 */
	@FXML
	private void runMissionTabSelected(){ }

	private void setAnchorZero(GoogleMapView node) {
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
	}
	
	
	

	/**
	 * A reference to the program common mainBus. 
	 */
	protected IPAndGUIInterface mainBus;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
			this.tabUtforController.setParent(this);
			this.tabPlaneraController.setParent(this);
			this.tabDatorseendeController.setParent(this);
			this.createGoogleMapViewOnTab(0);
		
		this.mainTabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				createGoogleMapViewOnTab(newValue.intValue());
				updateAvailableMission();
			}
		}); 
		
		
		// Starts the GUI-worker which hanldes all connections with the other program.
		Thread t1 = new Thread(new GUIWorker(this));
		t1.setDaemon(true);
		t1.start();
		
		
	}
	
	
	/**
	 * Refreshes the list of available missions in the dropdown
	 */
	protected void updateAvailableMission() {
		this.tabUtforController.updateMissionList();
		
	}

	/**
	 * Public Methods
	 */
	
	public void setMainBus(IPAndGUIInterface mainBus){
		this.mainBus = mainBus;
		
	}
	
	public IPAndGUIInterface getMainBus(){
		return this.mainBus;
	}
	
	
	/**
	 * Draws a Google map instance on the current tab.
	 * @param tabNr
	 */
	public void createGoogleMapViewOnTab(int tabNr){
		
		if(0 == tabNr){
				this.tabPlaneraController.mapView = new GoogleMapView();
                setAnchorZero(this.tabPlaneraController.mapView);
                this.tabPlaneraController.mapContainer.getChildren().add(this.tabPlaneraController.mapView);
                this.tabPlaneraController.planningMap = new PlanningMap(this.tabPlaneraController.mapView, this.tabPlaneraController);

		}else if (1 == tabNr){
			
			System.out.println(this.mainBus);
			this.tabDatorseendeController.loadIPGUI(this.tabDatorseende);
		}
		else if(2 == tabNr){
			
				// ADD GoogleMap To new View
                this.tabUtforController.mapViewUtfor = new GoogleMapView();
                setAnchorZero(this.tabUtforController.mapViewUtfor);

                this.tabUtforController.mapContainer.getChildren().add(this.tabUtforController.mapViewUtfor);
                this.tabUtforController.missionMap = new MissionMap(this.tabUtforController.mapViewUtfor, this.tabUtforController);
		}
		
	}
	
	

}
