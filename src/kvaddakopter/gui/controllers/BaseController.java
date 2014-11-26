package kvaddakopter.gui.controllers;

public class BaseController {
	
	
	/**
	 * The master parent of all other Controllers
	 */
	protected MainController parentController;
	
	
	
	/**
	 * Function to get a reference to the parent controller of this contoller.
	 * @return parent controller.
	 */
	public MainController getParent(){
		return this.parentController;
	}
	
	/**
	 * Used by the master controller when initializing to set it self as the parent controller to all it's children.
	 * @param controller A reference to the master controller.
	 */
	public void setParent(MainController controller){
		this.parentController = controller;
	}
}
