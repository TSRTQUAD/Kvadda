package kvaddakopter.control_module;
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
	protected double 				time;
	protected QuadData				quadData;
	protected SensorData 			sdata				= new SensorData();
	protected ControlSignal 		controlsignal		= new ControlSignal();
	protected MissionObject 		missionobject		= new MissionObject(); 
	protected KalmanFilter			skalmanx 			= new KalmanFilter(sampletime,1,0.01,1,0,0);
	protected KalmanFilter			skalmany	 		= new KalmanFilter(sampletime,1,0.01,1,0,0);
	protected RefinedSensorData 	rsdata  			= new RefinedSensorData();
	protected Controller			controller			= new Controller(sampletime);
	protected ReferenceData 		rrdata				= new ReferenceData();   
	protected ReferenceExtractor	referenceextractor	= new ReferenceExtractor(0);
	protected int					counter				= 0;
	protected int					controllingmode		= 0; 					// 0 for autonomous 
	protected boolean				debugMode			= true;					// Toggle System out prints 		
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
	

	
	public void run(){	
		checkIsRunning();
		try {
			if(debugMode){
				System.out.println("Waiting for quadcopter...");
				System.out.println("Quad is starting .. startsignal =  ");
				System.out.println(controlsignal.getStart());
				System.out.println("");
			}
			Thread.sleep((long) 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Initialize -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-		
		//MissionObject -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-		
		missionobject.setTrajectory(new double[][]
				{{58.395132,15.574524},
				{58.395184,15.574648},
				{58.395224,15.574768},
				{58.395222,15.5749},
				{58.395197,15.575007},
				{58.395167,15.575098},
				{58.395128,15.575096},
				{58.395084,15.575039},
				{58.395041,15.574983},
				{58.394997,15.574927},
				{58.394954,15.57487},
				{58.394917,15.574768},
				{58.394916,15.574629},
				{58.39495,15.5745},
				{58.395028,15.57453},
				{58.395061,15.574634},
				{58.39509,15.574758},
				{58.395127,15.574881},
				{58.395205,15.574908},
				{58.395291,15.574854},
				{58.395305,15.574725},
				{58.395273,15.574701},
				{58.395233,15.574699},
				{58.395181,15.574704},
				{58.395134,15.574677},
				{58.39511,15.574573}
				});
		
		missionobject.setHeight(new double[]{4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4});
		missionobject.setYaw(0.0);
		missionobject.setWaitingtime(0.0);
		missionobject.setReferenceVelocity(new double[][]
				{{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0},
				{0.7,0}
				});
		
		//Initialize -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		
		if(debugMode){
		System.out.println("Initializing modules ..");
		}
		this.quadData = mainbus.getQuadData();								//Reads sensor data from mainbus
		sdata.setnewsensordata(quadData);									//Update local sensor object
		//sdata.print();
		
		if (0 == controllingmode){
		//this.missionobject = mainbus.getMissionObject();					//Reads mission object from mainbus	
		
						//Start Quad-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-		
						ControlSignal csignal = new ControlSignal();	
						csignal.setStart(1);
						mainbus.setControlSignalobject(csignal);
						try {
							if(debugMode){
								System.out.println("Waiting for quadcopter...");
								System.out.println("Quad is starting .. startsignal =  ");
								System.out.println(csignal.getStart());
								System.out.println("");
							}
							Thread.sleep((long) 1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
								
		sdata.setinitial();													// Fix local coordinate system XY
		sdata.GPS2XY();														// Transformation GPS to XY coordinates
		sdata.xydot2XYdot();
		rrdata.initialize(sdata.getLatitud(),sdata.getLongitud());			// Fix local coordinate system XY
		rrdata.updateref(referenceextractor.update(missionobject));			// update ref @ Autonomous flight mode		
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
		if(debugMode) rrdata.print();
		//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		
		//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		rsdata.setXstates(skalmanx.timeupdate()); 							//Kalman filter in X direction
		rsdata.setYstates(skalmany.timeupdate()); 							//Kalman filter in Y direction
		rsdata.setXstates(skalmanx.velmeasurementupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
		rsdata.setYstates(skalmany.velmeasurementupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction
		rsdata.setYaw(sdata.getYaw());										// Set Yaw
		rsdata.setHeight(sdata.getHeight());								// Set Height
		rsdata.XYdot2Vel();													//Transform Xdot,Ydot to velocities
		//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		
		
		//rsdata.print();
		if(debugMode){
			System.out.println("Initializing completed");
			System.out.println("");
		}
		

		//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-

//		try {
//			if(debugMode) System.out.println("Sleep");
//			Thread.sleep((long) 10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		

				
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if(debugMode) System.out.println("Controllerloop initialized");
				while(true)
				{					
				//For every sample  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				counter ++;
				time = System.currentTimeMillis();		
				ControlSignal csignal = new ControlSignal();
				this.quadData = mainbus.getQuadData();						//Reads sensor data from mainbus
				sdata.setnewsensordata(quadData);							//Update local sensor object
				

				if (0 == controllingmode || 1 == controllingmode){
				sdata.GPS2XY();												//Transformation
				sdata.xydot2XYdot();										//Transformation
				}
				else if (2 == controllingmode){
				sdata.setGPSposition(new double[]{0,0});					//Set gps to {0,0}
				sdata.GPS2XY();												//Transformation
				sdata.xydot2XYdot();										//Transformation
				}
				
				if(debugMode){
					System.out.format("Sensordata at sample %d%n",counter);
					sdata.print();
				}
													

				
				//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				//Update states from Kalman filter
				rsdata.setXstates(skalmanx.timeupdate()); 					//Kalman filter in X direction
				rsdata.setYstates(skalmany.timeupdate()); 					//Kalman filter in Y direction
				rsdata.setXstates(skalmanx.velmeasurementupdate(sdata.getXdot())); //Kalman filter in X direction
				rsdata.setYstates(skalmany.velmeasurementupdate(sdata.getYdot())); //Kalman filter in Y direction
				rsdata.XYdot2Vel();											 //Transform Xdot,Ydot to velocities
				rsdata.s2rs(sdata);	
						//For every new GPS measurement-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
						if(sdata.isGPSnew() )
						{	
						System.out.println("New GPS measurement");
						rsdata.setXstates(skalmanx.gpsmeasurementupdate(sdata.getX()));	// Measurement update in kalmanfilter
						rsdata.setYstates(skalmany.gpsmeasurementupdate(sdata.getY()));	// Measurement update in kalmanfilter
						rsdata.XYdot2Vel();												//Transform Xdot,Ydot to velocities								
						}
						// -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_				
				if(debugMode){
					System.out.format("States at sample %d%n",counter);
					rsdata.print();
				}
				// -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				
				
				
				//Reference update  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-				
				if (0 == controllingmode){
				rrdata.update(rsdata, missionobject);						//Update reference data
				}
				if (1 == controllingmode){
				rrdata.updatesquare(rsdata);								//Reference is a 3m square)
				rrdata.GPS2XY();
				}
				else if (2 == controllingmode){
				rrdata.updatetest(rsdata);									//Reference is init+-(2m)
				}	
				if(debugMode){
					System.out.print("Reference signal:");
					rrdata.print();
				}
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-	
				
				
								
				//Control-signal -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if(rrdata.getMission()==1){
					csignal = controller.GetControlSignalMission1(rsdata, rrdata);
					if(debugMode) System.out.println("Controller Mission = 1, controlsignal:");
					//csignal.print();
					csignal = controller.saturation(csignal,0.25,0.25,0.4,5,0.02);
				}
				else if(rrdata.getMission()==0 || rrdata.getMission()==2){
					csignal = controller.GetControlSignalMission0(rsdata, rrdata);
					if(debugMode) System.out.println("Controller Mission = 0, controlsignal:");
					//csignal.print();
					csignal = controller.saturation(csignal,0.2,0.2,0.1,1.5,0.02);
				}
				
				if( rrdata.getLand() == 1){									//Initiate landing?
					csignal.setStart(0);									//
					try {													//
						Thread.sleep((long) 10);							//
					} catch (InterruptedException e) {						//
						e.printStackTrace();								//
					}					
				}
				
				//csignal = controller.saturation(csignal,0.7,0.6,0.1,1.5,0.02);// Saturate control-signal
				mainbus.setControlSignalobject(csignal);						// Update main-bus control-signal
				if(debugMode) csignal.print();
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-	
				
				
				
				
				//Sample-time -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-	
				time = sampletime*1000 - (System.currentTimeMillis()-time);				
//				System.out.format("Samplingsintervall: %.2f%n",time); 
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
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
		}				
	}	
}
