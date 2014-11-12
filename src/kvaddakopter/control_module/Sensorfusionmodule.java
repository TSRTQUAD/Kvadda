package kvaddakopter.control_module;
import kvaddakopter.control_module.modules.*;
import kvaddakopter.control_module.signals.*;




public class Sensorfusionmodule {
	public double 				 sampletime = 1;
	public SensorData 			 sdata = new SensorData();
	public Kalmanfilter			 skalmanx = new Kalmanfilter(sampletime,1,1,0,0);
	public Kalmanfilter			 skalmany = new Kalmanfilter(sampletime,1,1,0,0);
	public RefinedSensorData 	 rsdata  = new RefinedSensorData();
	
	public void start(){

//		//First Read
//		sdata.setGPSposition(new double[]{58.400991,15.595099});	// New GPS measurement
//		sdata.setydot(0);											// New velocity read (ydot)
//		sdata.setxdot(0);											// New velocity read (xdot)
//		sdata.setYaw(0);											// New Yaw read
//		sdata.setHeight(4);											// New Height measurement
//		
//		sdata.setinitial();											// Fix local coordinate system XY
//		sdata.GPS2XY();												// Transformation GPS to XY coordinates
//		sdata.xydot2XYdot();										// Transformation velocities to XY(dot)
//
//		
//		//Set initials
//		rsdata.setXstates(skalmanx.timeupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
//		rsdata.setYstates(skalmany.timeupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction
//		rsdata.setYaw(sdata.getYaw());								// Set Yaw
//		rsdata.setHeight(sdata.getHeight());						// Set Height
//		rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities

		
	
		//Sensorfusion loop ------------------------------------------	
		
//				//For every sample  -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-		
//				sdata.setydot(0.2);									//read sdata
//				sdata.setxdot(0.1);									//read sdata
//				
//				sdata.xydot2XYdot();								//transformation
//				
//				//Update states from kalmanfilter	
//				rsdata.setXstates(skalmanx.timeupdate(sdata.getXdot())); //Kalmanfilter in X direction
//				rsdata.setYstates(skalmany.timeupdate(sdata.getYdot())); //Kalmanfilter in Y direction
//				rsdata.XYdot2Vel();										 //Transform Xdot,Ydot to velocities
//				// -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_
//				
//				
//				//For every new GPS measurement-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-	
//				sdata.setGPSposition(new double[]{58.401122,15.595410});	// New GPS measurement
//				sdata.GPS2XY();												// Transformation GPS to XY coordinates
//				
//				rsdata.setXstates(skalmanx.measurementupdate(sdata.getX()));// Measurement update in kalmanfilter
//				rsdata.setYstates(skalmany.measurementupdate(sdata.getY()));// Measurement update in kalmanfilter
//				rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities
//				// -_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_
		
 // Test
				//First Read
				sdata.setGPSposition(new double[]{58.400991,15.595099});	// New GPS measurement
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
		
				rsdata.setXstates(skalmanx.measurementupdate(0));// Measurement update in kalmanfilter
				rsdata.setYstates(skalmany.measurementupdate(0.5));// Measurement update in kalmanfilter
				rsdata.XYdot2Vel();
				rsdata.print();
				
				rsdata.setXstates(skalmanx.timeupdate(sdata.getXdot())); 	// Kalmanfilter in X direction
				rsdata.setYstates(skalmany.timeupdate(sdata.getYdot())); 	// Kalmanfilter in Y direction				
				rsdata.setYaw(sdata.getYaw());								// Set Yaw
				rsdata.setHeight(sdata.getHeight());						// Set Height
				rsdata.XYdot2Vel();											//Transform Xdot,Ydot to velocities
				rsdata.print();
				
	}
	
}
