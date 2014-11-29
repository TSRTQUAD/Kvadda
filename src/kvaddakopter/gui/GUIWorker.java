package kvaddakopter.gui;

import javafx.application.Platform;
import kvaddakopter.gui.controllers.MainController;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.utils.Clock;

public class GUIWorker implements Runnable{


	protected MainBusGUIInterface mainBuss;


	protected MainController mainController;


	protected long sampleTime = 1000;

	public GUIWorker(MainController main){
		this.mainController = main;
	}

	@Override
	public void run() {

		boolean killThread = false;
		Clock clock = new Clock();
		this.mainBuss = this.mainController.getMainBus();


		while(!killThread){
			// VERIFICATION CLOCK
			clock.tic();
			try {
				//GET ALL NEEDED REFERENCES
				if (this.mainBuss == null && mainController != null ){
					this.mainBuss = mainController.getMainBus();
					Thread.sleep(100);
					continue;
				}

				//RUN A MISSION
				if(mainBuss.isStarted()){
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mainController.tabUtforController.drawQuadMarker();
							mainController.tabUtforController.drawTargetsOnMap();
							mainController.tabUtforController.updateTimeLeft(sampleTime);
							mainController.tabUtforController.updateSpeed();
							mainController.tabUtforController.updateMovie();
						}
					});

				}
				
				//Update gui image
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						mainController.tabDatorseendeController.updateImage();
					}
				});
				
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
				
				Thread.sleep(clock.stopAndGetSleepTime(1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}


	}

}
