package kvaddakopter.control_module;
import kvaddakopter.control_module.modules.*;
import kvaddakopter.control_module.signals.*;


public class ControllerModule{
	
	
	public ControlSignal 				 controlsignal = new ControlSignal();
	public RefinedSensorData 			 rsdata = new RefinedSensorData();
	public ReferenceData				 rdata = new ReferenceData();
	public ReferenceData 				 rrdata = new ReferenceData();
	public double						 sampletime	= 0.02;
	public Controller					 controller = new Controller(sampletime);
	

	
	
	public void start() {
		
		rsdata.setXpos(10);	
		//Test
	    	  
	   // XY coordinate initialization 	  
	    	// Read latest sensordata
	    	  
	    	  
	    	  
	    	  
	    	  
		rsdata.setYaw(0);
		rsdata.setXpos(10);
		rsdata.setYpos(10);
		rsdata.setHeight(5);
		rsdata.setForVel(1);
		rsdata.setLatVel(0);
		
		
		
		rrdata.setYaw(0);
		rrdata.setXpos(15);
		rrdata.setYpos(10);
		rrdata.setHeight(5);
		rrdata.setForVel(2);

		
		//Reference signal calculation

		
		//Control signal calculation
		controller.GetControlSignalMission(rsdata, rrdata);
		
		
		
		//Control signal update
		controlsignal.print();	
		rsdata.print();
	}
}






//controller.getcontrolsignal(apa,Referensdata
//mainBus.updateControlSignal(apa); */
//Reglersystem anton = new Reglersystem(10);
//anton.setTimer(1000);
//int pos = anton.readGPSPosition();
//int pos = anton.GPSPos;
//anton.GPSPOS = 100213231;
//RefinedSensorSignal = sensordata från kalmanfilter

