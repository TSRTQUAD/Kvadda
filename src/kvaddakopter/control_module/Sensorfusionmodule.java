package kvaddakopter.control_module;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.assignment_planer.ReferenceExtractor;
import kvaddakopter.control_module.modules.*;
import kvaddakopter.control_module.signals.*;
import kvaddakopter.interfaces.ControlMainBusInterface;


/*Initialize
* 1: Read sensor data and set origo @ intilong init lat
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
	protected double 				sampletime			= 0.2; //seconds
	protected double 				time;
	protected double[] 				sensorvector;
	
	
	protected SensorData 			sdata				= new SensorData();
	protected ControlSignal 		controlsignal		= new ControlSignal();
	protected MissionObject 		missionobject		= new MissionObject(); // TODO move to mainbus
	
	
	protected KalmanFilter			skalmanx 			= new KalmanFilter(sampletime,1,0.1,1,0,0);
	protected KalmanFilter			skalmany	 		= new KalmanFilter(sampletime,1,0.1,1,0,0);
	protected RefinedSensorData 	rsdata  			= new RefinedSensorData();
	protected Controller			controller			= new Controller(sampletime);
	protected ReferenceData 		rrdata				= new ReferenceData();   
	protected ReferenceExtractor	referenceextractor	= new ReferenceExtractor(0);
	protected int					counter				= 0;
	
	
	//public Sensorfusionmodule(ControlMainBusInterface mainbus){
	//	this.mainbus = mainbus;
	//}
	
	
	public Sensorfusionmodule(ControlMainBusInterface mainbus) {
		this.mainbus = mainbus;
	}


	
	
	public void run(){
		
		System.out.println("Initializing modules ..");
		//First Read
		this.sensorvector = mainbus.getSensorVector();				//Reads sensor data from mainbus
		sdata.setnewsensordata(sensorvector);						//Update local sensor object		
//		this.controlsignal = mainbus.getControlSignal();			//Reads Control signal from mainbus
//		this.missionobject = mainbus.getMissionObject();			//Reads mission object from mainbus

		//Set initials
		sdata.setinitial();											// Fix local coordinate system XY
		sdata.GPS2XY();												// Transformation GPS to XY coordinates
		sdata.xydot2XYdot();										// Transformation velocities to XY(dot)
		//sdata.print();
		
/*		rrdata.initialize(sdata.getLatitud(),sdata.getLongitud());	// Fix local coordinate system XY
		rrdata.updateref(referenceextractor.update(missionobject));													// Get first reference 
*/		rrdata.print();
		
		
		rsdata.setXstates(skalmanx.timeupdate()); //Kalman filter in X direction
		rsdata.setYstates(skalmany.timeupdate()); //Kalman filter in Y direction
		rsdata.setXstates(skalmanx.velmeasurementupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
		rsdata.setYstates(skalmany.velmeasurementupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction
		rsdata.setYaw(sdata.getYaw());								// Set Yaw
		rsdata.setHeight(sdata.getHeight());						// Set Height
		rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities
		
		//rsdata.print();
		
		System.out.println("Initializing completed");
		System.out.println("");
		
/*		//Start quad
		if(rrdata.getStart() == 1){			
			controlsignal.setStart(1);			
			try {
				System.out.println("Waiting for quadcopter...");
				System.out.println("Quad is starting .. startsignal =  ");
				System.out.println(controlsignal.getStart());
				System.out.println("");
				Thread.sleep((long) 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
*/		
				System.out.println("Controllerloop initialized");
				
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				//-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-

				while(true)
				{
				counter ++;
				time = System.currentTimeMillis();	
				//For every sample  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
//---			ControlSignal csignal = new ControlSignal();
				this.sensorvector = mainbus.getSensorVector();				//Reads sensor data from mainbus
				sdata.setnewsensordata(sensorvector);						//Update local sensor object
				sdata.GPS2XY();												//Transformation
				sdata.xydot2XYdot();										//Transformation
				System.out.format("Sensordata at sample %d%n",counter);
				//sdata.print();
													

				
				//SENSORFUSION  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
			    //Statements
										//transformation

				
				//Update states from kalman filter
				rsdata.setXstates(skalmanx.timeupdate()); //Kalman filter in X direction
				rsdata.setYstates(skalmany.timeupdate()); //Kalman filter in Y direction
				rsdata.setXstates(skalmanx.velmeasurementupdate(sdata.getXdot())); //Kalman filter in X direction
				rsdata.setYstates(skalmany.velmeasurementupdate(sdata.getYdot())); //Kalman filter in Y direction
				rsdata.XYdot2Vel();										 //Transform Xdot,Ydot to velocities
				rsdata.s2rs(sdata);
				System.out.format("States at sample %d%n",counter);
				rsdata.print();
				

				
				// -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_
				//For every new GPS measurement-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-
				if(sdata.isGPSnew() )
				{	
				System.out.println("New GPS measurement");
				//sdata.GPS2XY();												// Transformation GPS to XY coordinates

				rsdata.setXstates(skalmanx.gpsmeasurementupdate(sdata.getX()));// Measurement update in kalmanfilter
				rsdata.setYstates(skalmany.gpsmeasurementupdate(sdata.getY()));// Measurement update in kalmanfilter
				rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities
				
				System.out.println("New states:");
				rsdata.print();	
				// -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_
				}
				
				
				//CONTROLLER  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-				
/*
				rrdata.update(rsdata, missionobject);						//Update reference data					
				System.out.print("Reference signal:");
				rrdata.print();
				
				
				//Controlsignal calculation
				if(rrdata.getMission()==1){
					csignal = controller.GetControlSignalMission1(rsdata, rrdata, controlsignal);
					System.out.println("Controller Mission = 1, controlsignal:");
					csignal.print();
				}
				else if(rrdata.getMission()==0 || rrdata.getMission()==2){
					csignal = controller.GetControlSignalMission0(rsdata, rrdata, controlsignal);
					System.out.println("Controller Mission = 0, controlsignal:");
					csignal.print();
				}
				
				
				
				if( rrdata.getLand() == 1){									//Initiate landing?
					csignal.setLand(1);										//
					try {													//
						Thread.sleep((long) 10);							//
					} catch (InterruptedException e) {						//
						e.printStackTrace();								//
					}
					
					
					mainbus.setControlSignal(csignal);						// Update mainbus controlsignal
				}				
*/
				
				time = sampletime*1000 - (System.currentTimeMillis()-time);
				
				System.out.format("Samplingsintervall: %.2f%n",time);
				System.out.println("-------------------------------");
				System.out.println("");
				System.out.println("");
				
				if(counter == 81){
					System.out.println("apa");
				}
				
				if(time>0){
				
				try {
					Thread.sleep((long) time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
				}
				
				
				
				
				
 // Test
/*				//First Read
				sdata.setGPSposition(new double[]{58.401122,15.595410});	// New GPS measurement
				sdata.setydot(1);											// New velocity read (ydot)
				sdata.setxdot(0);											// New velocity read (xdot)
				sdata.setYaw(0);											// New Yaw read
				sdata.setHeight(4);											// New Height measurement
				
				sdata.setinitial();											// Fix local coordinate system XY
				sdata.GPS2XY();												// Transformation GPS to XY coordinates
				sdata.xydot2XYdot();										// Transformation velocities to XY(dot)
				sdata.print();
				
				//Refined
				rsdata.setXstates(skalmanx.timeupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
				rsdata.setYstates(skalmany.timeupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction				
				rsdata.setYaw(sdata.getYaw());								// Set Yaw
				rsdata.setHeight(sdata.getHeight());						// Set Height
				rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities
				rsdata.print();
				
				
				//Refined
				rsdata.setXstates(skalmanx.timeupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
				rsdata.setYstates(skalmany.timeupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction				
				rsdata.setYaw(sdata.getYaw());								// Set Yaw
				rsdata.setHeight(sdata.getHeight());						// Set Height
				rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities
				rsdata.print();
		
				rsdata.setXstates(skalmanx.measurementupdate(0));			// Measurement update in kalmanfilter
				rsdata.setYstates(skalmany.measurementupdate(0.5));			// Measurement update in kalmanfilter
				rsdata.XYdot2Vel();
				rsdata.print();
				
				rsdata.setXstates(skalmanx.timeupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
				rsdata.setYstates(skalmany.timeupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction				
				rsdata.setYaw(sdata.getYaw());								// Set Yaw
				rsdata.setHeight(sdata.getHeight());						// Set Height
				rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities
				rsdata.print();*/
				
	}
	
}
