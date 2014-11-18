package kvaddakopter.gui.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import kvaddakopter.gui.GUIWorker;
import kvaddakopter.gui.interfaces.MainBusGUIInterface;

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
		
		Thread t1 = new Thread(new GUIWorker(this));
		t1.setDaemon(true);
		t1.start();
		
	}
	
	public void setMainBus(MainBusGUIInterface mainBus){
		this.mainBus = mainBus;
		
	}
	
	public MainBusGUIInterface getMainBus(){
		return this.mainBus;
	}
	

}
