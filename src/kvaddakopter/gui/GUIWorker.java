package kvaddakopter.gui;

import javafx.application.Platform;
import kvaddakopter.gui.controllers.MainController;
import kvaddakopter.gui.interfaces.MainBusGUIInterface;

public class GUIWorker implements Runnable{

	
	protected MainBusGUIInterface mainBuss;


	protected MainController mainController;


	public GUIWorker(MainController main){
		this.mainController = main;
	}

	@Override
	public void run() {
		
		boolean killThread = true;

		while(!killThread){
			try {
                Thread.sleep(1000);
				if(mainController != null && mainController.tabUtforController.shouldStart()){
					
				
					 Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mainController.tabUtforController.drawQuadMarker();
	
						    System.out.println("UPDATED");
						}
					 });
				
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}
