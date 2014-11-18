package kvaddakopter.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.lynden.gmapsfx.GoogleMapView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import kvaddakopter.gui.GUIWorker;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.maps.MissionMap;
import kvaddakopter.maps.PlanningMap;

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
	
	
	@FXML
	public TabPane mainTabPane;
	
	/**
	 * GUI events
	 */
	@FXML
	private void runMissionTabSelected(){
		
	}

	private void setAnchorZero(GoogleMapView node) {
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
	}
	
	
	

	/**
	 * MainBus
	 */
	protected MainBusGUIInterface mainBus;

	/**
	 * Runs When GUI is initialized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		if (this.tabUtforController != null){
			this.tabUtforController.setParent(this);
		}
		if (this.tabPlaneraController != null){
			this.tabPlaneraController.setParent(this);
		}
		
		this.mainTabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				createGoogleMapViewOnTab(newValue.intValue());
			}
		}); 
		
		
		
		Thread t1 = new Thread(new GUIWorker(this));
		t1.setDaemon(true);
		t1.start();
		
	}
	
	
	
	/**
	 * Public Methods
	 */
	
	public void setMainBus(MainBusGUIInterface mainBus){
		this.mainBus = mainBus;
		
	}
	
	public MainBusGUIInterface getMainBus(){
		return this.mainBus;
	}
	
	
	private void createGoogleMapViewOnTab(int tabNr){
		
		if(0 == tabNr && this.tabPlaneraController.mapView  == null){
				this.tabPlaneraController.mapView = new GoogleMapView();
                setAnchorZero(this.tabPlaneraController.mapView);
                this.tabPlaneraController.mapContainer.getChildren().add(this.tabPlaneraController.mapView);
                this.tabPlaneraController.planningMap = new PlanningMap(this.tabPlaneraController.mapView, this.tabPlaneraController);

		}
		else if(1 == tabNr && this.tabUtforController.mapViewUtfor  == null){
			
				// ADD GoogleMap To new View
                this.tabUtforController.mapViewUtfor = new GoogleMapView();
                setAnchorZero(this.tabUtforController.mapViewUtfor);

                this.tabUtforController.mapContainer.getChildren().add(this.tabUtforController.mapViewUtfor);
                this.tabUtforController.missionMap = new MissionMap(this.tabUtforController.mapViewUtfor, this.tabUtforController);
		}
	}
	
	

}
