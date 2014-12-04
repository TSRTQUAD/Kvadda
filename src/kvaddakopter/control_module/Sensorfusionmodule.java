package kvaddakopter.control_module;
import java.io.IOException;

import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.assignment_planer.ReferenceExtractor;
import kvaddakopter.communication.QuadData;
import kvaddakopter.control_module.modules.*;
import kvaddakopter.control_module.signals.*;
import kvaddakopter.interfaces.ControlMainBusInterface;


/*Initialize
* 1: Read sensor data and set origo @ intilong initlat
* 2: Initialize kalmanfilter
* 3:
*/
	
/*Control loop
* 1: Update Reference ..
* 2: Transformation into correct coordinate system
* 3: Check mission type:
* 		Type 1: Get control signal Mission
* 				controller.GetControlSignalMission(rsdata, rrdata);
* 		Type 0: Get control signal single
* 				controller.GetControlSignalSingle(rsdata, rrdata);
* 4: Saturate control signal?
* 5: Update to mainbus?
*/

/*Main control loop
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
	protected double 				sampletime			= 0.05; //seconds
	protected double 				seconds				= 50;
	protected QuadData				quadData;
	protected SensorData 			sdata				= new SensorData();
	protected ControlSignal 		controlsignal		= new ControlSignal();
	protected MissionObject 		missionobject		= new MissionObject(); 
	protected KalmanFilter			skalmanx 			= new KalmanFilter(sampletime,1,0.01,1,0,0); //REMOVE??
	protected KalmanFilter			skalmany	 		= new KalmanFilter(sampletime,1,0.01,1,0,0); //REMOVE??
	protected Kalmanfilter_endast_gps			skalmanxx 			= new Kalmanfilter_endast_gps(sampletime,1,0.01,0,0);
	protected Kalmanfilter_endast_gps			skalmanyy	 		= new Kalmanfilter_endast_gps(sampletime,1,0.01,0,0);	
	protected RefinedSensorData 	rsdata  			= new RefinedSensorData();
	protected Controller			controller			= new Controller(sampletime);
	protected ReferenceData 		rrdata				= new ReferenceData();   
	protected ReferenceExtractor	referenceextractor	= new ReferenceExtractor(0);
	protected int					counter				= 0;
	protected int					controllingmode		= 0; 	//REMOVE??
	protected boolean				debugMode			= true;					// Toggle System out prints 		
	protected int					whichkalman			= 0; // 1 for 2xY 0 for 1xY //REMOVE??
	protected double[][]			states				= new double[(int) (1/sampletime*seconds)][2];
	protected MatFileHandler		saver				= new MatFileHandler();
	protected boolean				initialbool 		= true;
	protected boolean				threadrunning		= true;
	protected SampleTimer			sampletimer			= new SampleTimer(sampletime/1000);
	protected DataSaver				datasaver			= new DataSaver(sampletime,50,2);
	protected double 				time;
	
	
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
		checkIsArmed();		
		//Initialize -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-		
		if(debugMode){
			System.out.println("Initializing modules ..");
		}
		if (0 == controllingmode){

			
			//Average initials -_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_-
			double Initiallatitud = 0;
			double Initiallongitud = 0;
			int localcounter = 0;
			while(initialbool) {					
				this.quadData = mainbus.getQuadData();								//Reads sensor data from mainbus
				sdata.setnewsensordata(quadData);
				if (sdata.isGPSnew()){

					if (debugMode){
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
		//-_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_-

		
		rrdata.initialize(sdata.getLatitud(),sdata.getLongitud());			// Fix local coordinate system XY
		this.missionobject = mainbus.getMissionObject();					//Reads mission object from mainbus			
		rrdata.updateref(referenceextractor.update(missionobject));			// update ref @ Autonomous flight mode		
		checkIsRunning();
		
						//Waiting for Quad-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-								
						try {
							if(debugMode){
								System.out.println("Waiting for quadcopter...");
							}
							Thread.sleep((long) 1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//-_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_--_-_-_-_-_-
						
						
		this.quadData = mainbus.getQuadData();								//Reads sensor data from mainbus
		sdata.setnewsensordata(quadData);							
		sdata.xydot2XYdot();
		}			
		
		
		else if (1 == controllingmode){
		sdata.setinitial();													// Fix local coordinate system XY
		sdata.GPS2XY();														// Transformation GPS to XY coordinates
		sdata.xydot2XYdot();
		rrdata.initialize(sdata.getLatitud(),sdata.getLongitud());			// Fix local coordinate system XY
		rrdata.settestpoint();												// Primitive fixed reference @ initial-lat/lon		
		}
		
		else if (2 == controllingmode){
			sdata.setGPSposition(new double[]{0,0});						// Set initial gps to {0,0}
			sdata.setinitial();												// Fix local coordinate system XY
			sdata.GPS2XY();													// Transformation GPS to XY coordinates
			sdata.xydot2XYdot();											// Transformation velocities to XY(dot)
			rrdata.initialize(sdata.getLatitud(),sdata.getLongitud());		// Fix local coordinate system XY
			rrdata.updateindoor(referenceextractor.updatetest());			// Indoor flight mode GPS has to be [0,0]
		}	
		//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		
		
		if (1 ==  whichkalman){
		//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		rsdata.setXstates(skalmanx.timeupdate()); 							//Kalman filter in X direction
		rsdata.setYstates(skalmany.timeupdate()); 							//Kalman filter in Y direction
		rsdata.setXstates(skalmanx.velmeasurementupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
		rsdata.setYstates(skalmany.velmeasurementupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction
		rsdata.setYaw(sdata.getYaw());										// Set Yaw
		rsdata.setHeight(sdata.getHeight());								// Set Height
		rsdata.XYdot2Vel();													//Transform Xdot,Ydot to velocities
		//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		}
		
		
		if (0 ==  whichkalman){
		//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		rsdata.setXstates(skalmanxx.timeupdate(sdata.getXdot())); 							//Kalman filter in X direction
		rsdata.setYstates(skalmanyy.timeupdate(sdata.getYdot())); 							//Kalman filter in Y direction
		rsdata.setYaw(sdata.getYaw());										// Set Yaw
		rsdata.setHeight(sdata.getHeight());								// Set Height
		rsdata.XYdot2Vel();													//Transform Xdot,Ydot to velocities
		//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		}
		

		


		//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-

		try {
			if(debugMode) System.out.println("Sleep");
			Thread.sleep((long) 500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				

//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_--_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_--_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if(debugMode) System.out.println("Controllerloop initialized");
				while(threadrunning)
				{
					
				//For every sample  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				counter ++;
				sampletimer.initiate();
																				//time = System.currentTimeMillis();						//REMOVE?
				ControlSignal csignal = new ControlSignal();			
				this.quadData = mainbus.getQuadData();						//Reads sensor data from mainbus
				sdata.setnewsensordata(quadData);							//Update local sensor object
				sdata.GPS2XY();												//Transformation
				sdata.xydot2XYdot();										//Transformation

				
				
				//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if (1 ==  whichkalman){
					rsdata.setXstates(skalmanx.timeupdate()); 							//Kalman filter in X direction
					rsdata.setYstates(skalmany.timeupdate()); 							//Kalman filter in Y direction
					rsdata.setXstates(skalmanx.velmeasurementupdate(sdata.getXdot()));  //Kalman filter in X direction
					rsdata.setYstates(skalmany.velmeasurementupdate(sdata.getYdot()));  //Kalman filter in Y direction
					rsdata.XYdot2Vel();													//Transform Xdot,Ydot to velocities
					rsdata.s2rs(sdata);
							//For every new GPS measurement-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
							if(sdata.isGPSnew() )
							{	
								rsdata.setXstates(skalmanx.gpsmeasurementupdate(sdata.getX()));	// Measurement update in kalmanfilter
								rsdata.setYstates(skalmany.gpsmeasurementupdate(sdata.getY()));	// Measurement update in kalmanfilter
								rsdata.XYdot2Vel();												// Transform Xdot,Ydot to velocities								
							}
				}

				
				//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if (0 ==  whichkalman){					
					rsdata.setXstates(skalmanxx.timeupdate(sdata.getXdot())); 							//Kalman filter in X direction
					rsdata.setYstates(skalmanyy.timeupdate(sdata.getYdot())); 							//Kalman filter in Y direction
					rsdata.setYaw(sdata.getYaw());														// Set Yaw
					rsdata.setHeight(sdata.getHeight());												// Set Height
					rsdata.XYdot2Vel();																	//Transform Xdot,Ydot to velocities
					rsdata.s2rs(sdata);
							//For every new GPS measurement-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
							if(sdata.isGPSnew() )
							{	
								rsdata.setXstates(skalmanxx.gpsmeasurementupdate(sdata.getX()));	// Measurement update in kalmanfilter
								rsdata.setYstates(skalmanyy.gpsmeasurementupdate(sdata.getY()));	// Measurement update in kalmanfilter
								rsdata.XYdot2Vel();													//Transform Xdot,Ydot to velocities								
							}		
				}						

								
				
				//Reference update  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-				
				rrdata.update(rsdata, missionobject);						//Update reference data
				
								
				//Control-signal -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
					csignal = controller.GetControlSignalMission0(rsdata, rrdata);			// Calculates controlsignal
					csignal = controller.saturation(csignal,0.15,0.15,0.5,1.5,0.02);		// Saturate Controlsignal
					csignal = controller.shouldland(rrdata, csignal);						// Initiate landing?														
					mainbus.setControlSignalobject(csignal);								// Update main-bus control-signal					
					if (controller.landinginitiated()) threadrunning = false;				// Shuts down thread after landing is initiated.

				//% of mission completed -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-	
				//mainbus.addVisitedPoint(rrdata.getCounter());
				
				
					
				/*
				//Save data-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if (counter <= 20*seconds){
				states[counter-1][0] = rsdata.getXpos();
				states[counter-1][1] = rsdata.getYpos();
				
				if (counter == 20*seconds){
					try {
						
						ControlSignal csignal1 = new ControlSignal();
						csignal.setStart(1);
						mainbus.setControlSignalobject(csignal1);
						saver.createMatFileFromFlightData("States", states);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}
				*/ /// REMOVE??
				
				
					
				//Save data-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				datasaver.saver(new double[]{rsdata.getXpos(),rsdata.getYpos()});
				
				
				//Printer-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if(debugMode){
					System.out.format("Sensordata at sample %d%n",counter);
					sdata.print();
					System.out.format("States at sample %d%n",counter);
					rsdata.print();
					System.out.print("Reference signal:");
					rrdata.print();
					System.out.println("Controlsignal, Mission = " + rrdata.getMission());
					csignal.print();
				}

				
				
	/*
				//Sample-time -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-	
				time = sampletime*1000 - (System.currentTimeMillis() - time);				
				System.out.format("Samplingsintervall: %.2f%n",time); 
				if(debugMode){
					System.out.println("-------------------------------");
					System.out.println("");
					System.out.println("");
				}
				if(time>0){
				
				try {
					Thread.sleep((long) time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_- */  //REMOVE???
				
				
							
				//Sample-time-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				sampletimer.waiter();	
		}				
	}	
}
