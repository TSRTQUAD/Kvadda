package kvaddakopter.control_module.modules;
import org.ejml.simple.SimpleMatrix;
import kvaddakopter.control_module.signals.*;


/* Controller for quadcopter uses two different controlling modes.
 * 1. GetControlSignalSingle: Go to a single coordinate by controlling lateral and forward velocity, Yaw is fixed by referece.
 * 	Input: Refinedreference data and Sensordata, output is void, Updates the public object controlsignal.
 * 2. GetControlSignalMission: Controls quadcopter by fixed forward velocity and Yaw-angle. Lateral vel is set to zero.
 *  Input: Refinedreference data and Sensordata, output is void, Updates the public object controlsignal.
 * 
 * 
 * Created by Anton on 2014-10-10.
 */


public class Controller{		
	protected double  errorheight,errorlateralvel, errorforwardvel, errorheading, Ts;
	protected double KVelForward, KVelHeight, KVelLateral, KYaw, Tintegral, integral;


	public Controller(double sampletime){	
		KVelHeight = 1; 
		KVelForward = 0.3;
		KVelLateral = 0.4;
		KYaw = 0.3;
		Tintegral = 0.01;
		Ts = sampletime;
		errorheight = 0;
		errorlateralvel = 0;
		errorforwardvel = 0;
		errorheading = 0;
		integral = 0;
	}
		
	
	
		public ControlSignal GetControlSignalMission0(RefinedSensorData rsdata, ReferenceData rrdata,ControlSignal controlsignal){
		
		ControlSignal csignal = new ControlSignal();
			
		//Coordinate system calculation
		SimpleMatrix errorpos = new SimpleMatrix(2,1,true,rrdata.getYpos() - rsdata.getYpos(),
														  rrdata.getXpos() - rsdata.getXpos());	
		SimpleMatrix pos2vel = new SimpleMatrix(2,2,true,Math.cos(rsdata.getYaw()), Math.sin(rsdata.getYaw()),
														-Math.sin(rsdata.getYaw()), Math.cos(rsdata.getYaw()));					 
		SimpleMatrix RDataVel = pos2vel.mult(errorpos);
		
		
		
		//Control signals
		csignal.setForwardvelocity	(	KVelForward*(RDataVel.get(0) - rsdata.getForVel() )		);
		csignal.setLateralvelocity	(	KVelLateral*(RDataVel.get(1) - rsdata.getLatVel() )		);	
		csignal.setHeightvelocity	( 	KVelHeight*(rrdata.getHeight() - rsdata.getHeight())	);	
		csignal.setYawrate			(	KYaw*(rrdata.getYaw() - rsdata.getYaw())				);
		 
		
		this.errorheight = rrdata.getHeight() - rsdata.getHeight();
		this.errorforwardvel = rrdata.getForVel() - rsdata.getForVel();
		this.errorheading = rrdata.getYaw() - rsdata.getYaw();

		return csignal;		
		}

		
		
		 public ControlSignal GetControlSignalMission1(RefinedSensorData rsdata, ReferenceData rrdata, ControlSignal controlsignal){ 
		 //Constant speed controlling
			ControlSignal csignal = new ControlSignal();
			 
			 csignal.setForwardvelocity		(	this.integral + (KVelForward+ Ts*Tintegral/2)*
					 							(rrdata.getForVel() - rsdata.getForVel())	+
					 							(Ts*Tintegral/2 - KVelForward)*this.errorforwardvel						);
			 
			 csignal.setYawrate 			(	KYaw*(rrdata.getYaw() - rsdata.getYaw())							);
			 csignal.setHeightvelocity		(	KVelHeight*(rrdata.getHeight() - rsdata.getHeight())				);
			 
			 //Errors 
			 this.errorheight = rrdata.getHeight() - rsdata.getHeight();
			 this.errorforwardvel = rrdata.getForVel() - rsdata.getForVel();
			 this.errorheading = rrdata.getYaw() - rsdata.getYaw();
			 this.integral = csignal.getForwardvelocity();
			 return csignal;
		 }
		 

		 
		 

	// Getters
	public double geterrorheigt() {
		return errorheight;
	}


	public double geterrorlateralvel() {
		return errorlateralvel;
	}


	public double geterrorforwardvel() {
		return errorforwardvel;
	}

	public double geterrorheading() {
		return errorheading;
	}
	
}
