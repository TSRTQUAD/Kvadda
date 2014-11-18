package kvaddakopter.gui.controllers;

public class BaseController {
	
	
	protected MainController parentController;
	
	
	
	
	public MainController getParent(){
		return this.parentController;
	}
	
	public void setParent(MainController controller){
		this.parentController = controller;
	}
}
