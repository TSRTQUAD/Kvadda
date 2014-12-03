package kvaddakopter.gui;

import javafx.application.Platform;
import kvaddakopter.gui.controllers.MainController;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.utils.Clock;

public class GUIWorker implements Runnable{


	protected MainBusGUIInterface mainBuss;


	protected MainController mainController;


	protected long sampleTime = 100;

	public GUIWorker(MainController main){
		this.mainController = main;
	}

	@Override
	public void run() {

		boolean killThread = false;
		Clock clock = new Clock();
		this.mainBuss = this.mainController.getMainBus();
		int counter = 0;


		while(!killThread){
			// VERIFICATION CLOCK
			try {
				clock.tic();
				counter ++;
				//GET ALL NEEDED REFERENCES
				if (this.mainBuss == null && mainController != null ){
					this.mainBuss = mainController.getMainBus();
					Thread.sleep(100);
					continue;
				}

				if(counter >= 10){
					counter = 0;
					//RUN A MISSION
					//if(mainBuss.isStarted()){
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								mainController.tabUtforController.drawQuadMarker();
								mainController.tabUtforController.drawTargetsOnMap();
								if(mainBuss.isStarted()) mainController.tabUtforController.updateTimeLeft(sampleTime);
								mainController.tabUtforController.updateSpeed();
								mainController.tabUtforController.updateCoverage();
								mainController.tabUtforController.updateMovie();
							}
						});
	
					//}
					
					
					
					
					//CHECK LINKSTATUS
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mainController.tabUtforController.updateBattery(mainBuss.getBattery());
							mainController.tabUtforController.updateGPSStatus(mainBuss.gpsFixOk());
							mainController.tabUtforController.updateWIFIStatus(mainBuss.wifiFixOk());
							mainController.tabUtforController.updateButtons();
						}
					});
				}
				
				for(int i = 0; i < 10; i++){
					//Update gui image
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mainController.tabDatorseendeController.updateImage();
							mainController.tabUtforController.updateMovie();
						}
					});
				}
				
				Thread.sleep(clock.stopAndGetSleepTime(100));
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


	}

}
