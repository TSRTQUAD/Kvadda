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
	protected double KVelForward, KVelForward2, KVelHeight, KVelLateral,KVelLateral2, KYaw, KYaw2, Tintegral, integral;
	protected boolean Yawdirection;

	public Controller(double sampletime){	
		KVelHeight = 1.5; 
		KVelForward = 0.04;
		KVelForward2 = 0.1;
		KVelLateral = 0.04;
		KVelLateral2 = 0.01;
		KYaw = 1.5;
		KYaw2 = 2.5;
		Yawdirection = true;
		Tintegral = 0.05;
		Ts = sampletime;
		errorheight = 0;
		errorlateralvel = 0;
		errorforwardvel = 0;
		errorheading = 0;
		integral = 0;
	}
		
	
		/**
		 * Function that calculates controlsignal given mission = 0 @ Reference data
		 * @param Refinedsensordata object
		 * @param Referencedata object
		 * @return Controlsignal
		 */
	
		public double SelectYawDirection(double Yawref,double Yaw, double KYaw){
			boolean direction = KYaw >= 0;
			if (Math.abs(Yawref - Yaw) > (2*Math.PI - Math.abs(Yawref) - Math.abs(Yaw))){
				if (direction){
				KYaw = -KYaw;
				}
			}
			else{
			KYaw = Math.abs(KYaw);
			}
			return KYaw;
			}
	
	
	
		public ControlSignal GetControlSignalMission0(RefinedSensorData rsdata, ReferenceData rrdata){
		
		ControlSignal csignal = new ControlSignal();
			
		//Coordinate system calculation
		SimpleMatrix errorpos = new SimpleMatrix(2,1,true,rrdata.getYpos() - rsdata.getYpos(),
														  rrdata.getXpos() - rsdata.getXpos());	
		SimpleMatrix pos2vel = new SimpleMatrix(2,2,true,Math.cos(rsdata.getYaw()), -Math.sin(rsdata.getYaw()),
														 Math.sin(rsdata.getYaw()), Math.cos(rsdata.getYaw()));					 
		SimpleMatrix RDataVel = pos2vel.mult(errorpos);
		
		//Yaw controlling direction correction
		this.KYaw = this.SelectYawDirection(rrdata.getYaw(), rsdata.getYaw(), this.KYaw);
		
		//Control signals
		csignal.setForwardvelocity	(	-KVelForward*(RDataVel.get(0) - rsdata.getForVel() )	);
		csignal.setLateralvelocity	(	KVelLateral*(RDataVel.get(1) - rsdata.getLatVel() )		);	
		csignal.setHeightvelocity	( 	KVelHeight*(rrdata.getHeight() - rsdata.getHeight())	);	
		csignal.setYawrate			(	-KYaw*(rrdata.getYaw() - rsdata.getYaw())				);

		 
		
		this.errorheight = rrdata.getHeight() - rsdata.getHeight();
		this.errorforwardvel = rrdata.getForVel() - rsdata.getForVel();
		this.errorheading = rrdata.getYaw() - rsdata.getYaw();

		return csignal;		
		}

		
		
		
		/**
		 * Function that calculates controlsignal given mission = 1 @ Reference data
		 * This controller uses the velocity referenece in Referencedata object.
		 * @param rsdata  RefinedSensorData object containing estimated states and sensordata.
		 * @param rrdata ReferenceData object containing reference signal
		 * @return Controlsignal
		 */
		 public ControlSignal GetControlSignalMission1(RefinedSensorData rsdata, ReferenceData rrdata){ 
		 //Constant speed controlling
			ControlSignal csignal = new ControlSignal();
			 
			
			this.KYaw2 = this.SelectYawDirection(rrdata.getYaw(), rsdata.getYaw(), this.KYaw2);
			
			 csignal.setForwardvelocity		(	(this.integral + (KVelForward2+ Ts*Tintegral/2)*
					 							(rrdata.getForVel() - rsdata.getForVel())	+
					 							(Ts*Tintegral/2 - KVelForward2)*this.errorforwardvel)				);
			 csignal.setLateralvelocity		(	KVelLateral2*(0 - rsdata.getLatVel())								);
			 csignal.setYawrate 			(	-KYaw2*(rrdata.getYaw() - rsdata.getYaw())							);
			 csignal.setHeightvelocity		(	KVelHeight*(rrdata.getHeight() - rsdata.getHeight())				);
			 
			 //Errors 
			 this.errorheight = rrdata.getHeight() - rsdata.getHeight();
			 this.errorforwardvel = rrdata.getForVel() - rsdata.getForVel();
			 this.errorheading = rrdata.getYaw() - rsdata.getYaw();
			 this.integral = csignal.getForwardvelocity();
			 
			 return csignal;
		 }
		 

		 /**
		  * Saturates controlsignal given saturation parameters (forvel, latvel, heightvel and yawdot).
		  * Also introduces a dead-zone for yawdot around yawdeadzone
		  * @param controlsignal
		  * @param forvel
		  * @param latvel
		  * @param heightvel
		  * @param yawdot
		  * @param yawdeadzone
		  * @return
		  */
	public ControlSignal saturation(ControlSignal csignal,double forvel,double latvel,double heightvel,double yawdot,double yawdeadzone){
		double refforvel,reflatvel,refheightvel,refyawdot;
		
		
		if (Math.abs(csignal.getForwardvelocity()) > forvel){
			refforvel = (csignal.getForwardvelocity() > forvel) ? forvel : -forvel;
			csignal.setForwardvelocity(refforvel);
		}
		if (Math.abs(csignal.getLateralvelocity()  ) > latvel){
			reflatvel = (csignal.getLateralvelocity() > latvel) ? latvel : -latvel;
			csignal.setLateralvelocity(reflatvel);
		}
		if (Math.abs(csignal.getHeightvelocity()  ) > heightvel){
			refheightvel = (csignal.getHeightvelocity() > heightvel) ? heightvel : - heightvel;
			csignal.setHeightvelocity(refheightvel);
		}
		if (Math.abs(csignal.getYawrate()  ) < yawdeadzone){
			csignal.setYawrate(0);
		}
		if (Math.abs(csignal.getYawrate()  ) > yawdot){
			refyawdot = (csignal.getYawrate() > yawdot) ? yawdot : - yawdot;
			csignal.setYawrate(refyawdot);
		}
		//TODO  yawdot

		return csignal;		
	}
		 

	/**
	 * Get error height
	 * @return
	 */
	public double geterrorheight() {
		return errorheight;
	}

	/**
	 * Geterror for lateral velocity
	 * @return
	 */
	public double geterrorlateralvel() {
		return errorlateralvel;
	}

	/**
	 * Get error for forward velocity
	 * @return
	 */
	public double geterrorforwardvel() {
		return errorforwardvel;
	}
	/**
	 * Get error for heading (yaw)
	 * @return
	 */
	public double geterrorheading() {
		return errorheading;
	}
	
}
