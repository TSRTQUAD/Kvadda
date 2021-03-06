package kvaddakopter.control_module;
import java.io.IOException;

import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.assignment_planer.ReferenceExtractor;
import kvaddakopter.communication.QuadData;
import kvaddakopter.control_module.modules.*;
import kvaddakopter.control_module.signals.*;
import kvaddakopter.interfaces.ControlMainBusInterface;

/**
 * 	Controller: Implements a kalmanfilter for positions and velocity estimates, reads referencedata and calculates controlsignals.
 * 
 */ 
 /*	Initialize
 * 1: Read sensor data and set origo @ intilong initlat after averageing 5 gps measurements.
 * 2: Initialize reference data
 * 3: Waiting for execute command
 *
 *
 *	Control loop
 * 1: Update Reference ..
 * 2: Transformation into correct coordinate system
 * 3: Check mission type:
 * 		Type 1: Get control signal Mission
 * 				controller.GetControlSignalMission(rsdata, rrdata);
 * 		Type 0: Get control signal single
 * 				controller.GetControlSignalSingle(rsdata, rrdata);
 * 4: Saturate control signal?
 * 5: Update to mainbus?
 *
 *
 * 	Main control loop
 *	 1: For every sample:
 * 		SENSORFUSION
 *	 	i: Read latest sensor data
 *	 	ii: Transform data into fixed coordinate system
 *	 	iii: Estimate positions with kalmanfilter (time update) in X and Y direction.
 *	 	iv: Transform states into quad velocities.
 *
 *		CONTROLLER
 *		i: Update Reference ..
 *		ii: Transformation into correct coordinate system
 *		iii: Check mission type:
 * 			Type 1: Get control signal Mission
 * 					controller.GetControlSignalMission(rsdata, rrdata);
 * 			Type 0 or 2: Get control signal single
 * 						 controller.GetControlSignalSingle(rsdata, rrdata);
 *		? vi: Saturate control signal?
 *		? v: Update to mainbus?

 *	 2: For every new GPS measurement
 * 		i: Read new gps measurement.
 * 		ii: Transform into fixed coordinate system
 * 		iii: Estimate positions with kalmanfilter (measurement update) in X and Y direction.
 * 		iv: Transform states into quad velocities.
 *  
 */

public class Sensorfusionmodule implements Runnable{	
	protected ControlMainBusInterface mainbus;

	// Parameters
	protected double 				sampletime			= 0.05; 	// in seconds
	protected boolean				debugmode			= true;		// Toggle System out prints 

	//Signal objects
	protected SensorData 			sdata				= new SensorData();
	protected ControlSignal 		controlsignal		= new ControlSignal();
	protected MissionObject 		missionobject		= new MissionObject(); 
	protected ReferenceData 		rrdata				= new ReferenceData();
	protected QuadData				quadData;
	protected RefinedSensorData 	rsdata  			= new RefinedSensorData();

	//Functions
	protected ReferenceExtractor	referenceextractor	= new ReferenceExtractor(0);
	protected SampleTimer			sampletimer			= new SampleTimer(sampletime*1000);
	protected DataSaver				datasaver			= new DataSaver(2,"states");
	
	//Modules
	protected Kalmanfilter			kalmanx 			= new Kalmanfilter(sampletime,1,0.01,0,0);
	protected Kalmanfilter			kalmany	 			= new Kalmanfilter(sampletime,1,0.01,0,0);	
	protected Controller			controller			= new Controller(sampletime);

	//Other variables		
	protected boolean				initialbool 		= true;
	protected boolean				threadrunning		= true;



	public Sensorfusionmodule(ControlMainBusInterface mainbus) {
		this.mainbus = mainbus;
	}


	private void checkIsRunning(){
		while(!mainbus.isStarted()){
			synchronized(mainbus){
				try {
					mainbus.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}


	private void checkIsArmed(){
		while(!mainbus.getIsArmed()){
			synchronized(mainbus){
				try {
					mainbus.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}



	public void run(){	
		while(true){
			checkIsArmed();			
			if(debugmode){
				System.out.println("Initializing modules ..");
			}

			
			//Average initials -_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_-
			double Initiallatitud = 0;
			double Initiallongitud = 0;
			int localcounter = 0;
			while(initialbool) {					
				this.quadData = mainbus.getQuadData();								//Reads sensor data from mainbus
				sdata.setnewsensordata(quadData);
				if (sdata.isGPSnew()){

					if (debugmode){
						System.out.println(this.quadData.getGPSLat());
						System.out.println(this.quadData.getGPSLong());
						System.out.println(localcounter);
					}

					Initiallatitud = Initiallatitud + this.quadData.getGPSLat();
					Initiallongitud = Initiallongitud + this.quadData.getGPSLong();
					localcounter = localcounter + 1;

					if (5 == localcounter) initialbool = false;
				}
				try {
					Thread.sleep((long) 200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
			Initiallatitud = Initiallatitud/(localcounter);
			Initiallongitud = Initiallongitud/(localcounter);
			sdata.setGPSposition(new double[]{Initiallatitud,Initiallongitud});
			sdata.setinitial();													// Fix local coordinate system XY
			sdata.GPS2XY();														// Transformation GPS to XY coordinates



			//Initialize reference data-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-	
			rrdata.initialize(sdata.getLatitud(),sdata.getLongitud());			// Fix local coordinate system XY
			this.missionobject = mainbus.getMissionObject();					//Reads mission object from mainbus			
			rrdata.updateref(referenceextractor.update(missionobject));			// update ref @ Autonomous flight mode		



			//Waiting for Quad to start-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-								
			checkIsRunning();
			try {
				if(debugmode){
					System.out.println("Waiting for quadcopter...");
				}
				Thread.sleep((long) 1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			

			//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_--_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
			//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_--_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
			if(debugmode) System.out.println("Controllerloop initialized");
			while(threadrunning && mainbus.isStarted())
			{

				//For every sample  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				sampletimer.initiate();
				ControlSignal csignal = new ControlSignal();			
				this.quadData = mainbus.getQuadData();						//Reads sensor data from mainbus
				sdata.setnewsensordata(quadData);							//Update local sensor object
				sdata.GPS2XY();												//Transformation
				sdata.xydot2XYdot();										//Transformation

			
				//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-				
					rsdata.setXstates(kalmanx.timeupdate(sdata.getXdot())); 							//Kalman filter in X direction
					rsdata.setYstates(kalmany.timeupdate(sdata.getYdot())); 							//Kalman filter in Y direction
					rsdata.setYaw(sdata.getYaw());														// Set Yaw
					rsdata.setHeight(sdata.getHeight());												// Set Height
					rsdata.XYdot2Vel();																	//Transform Xdot,Ydot to velocities
					rsdata.s2rs(sdata);
					//For every new GPS measurement-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
					if(sdata.isGPSnew() )
					{	
						rsdata.setXstates(kalmanx.gpsmeasurementupdate(sdata.getX()));	// Measurement update in kalmanfilter
						rsdata.setYstates(kalmany.gpsmeasurementupdate(sdata.getY()));	// Measurement update in kalmanfilter
						rsdata.XYdot2Vel();													//Transform Xdot,Ydot to velocities								
					}								

				//Save data-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				datasaver.adddata(new double[]{rsdata.getXpos(),rsdata.getYpos()});				


				//Reference update  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-				
				rrdata.update(rsdata, missionobject);						//Update reference data


				//Control-signal -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				csignal = controller.GetControlSignalMission0(rsdata, rrdata);			// Calculates controlsignal
				csignal = controller.saturation(csignal,0.15,0.15,0.5,1.5,0.02);		// Saturate Controlsignal
				csignal = controller.shouldland(rrdata, csignal);						// Initiate landing?														
				mainbus.setControlSignalobject(csignal);								// Update main-bus control-signal					
				if (controller.landinginitiated()) threadrunning = false;				// Shuts down thread after landing is initiated.



				//Number of reference points visited -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				mainbus.setVisitedPoints(rrdata.getreferenscounter());


				//Printer-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if(debugmode){
					System.out.println("");
					System.out.print("Sensordata:");
					sdata.print();
					System.out.print("States:");
					rsdata.print();
					System.out.print("Reference signal:");
					rrdata.print();
					System.out.println("Controlsignal, Mission = " + rrdata.getMission());
					csignal.print();
				}

				//Sample-time-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				sampletimer.waiter();
			}
			
				//Save data to matfile when landed -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
			datasaver.savedata();
			
		}
	}
}

