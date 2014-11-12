package kvaddakopter.control_module.modules;
import org.ejml.simple.SimpleMatrix;
import kvaddakopter.control_module.signals.*;



public class Controller{		
	protected double  errorheigt,errorlateralvel, errorforwardvel, errorheading, Ts;
	protected double KVelForward, KVelHeight, KVelLateral, KYaw, Tintegral;

	public Controller(double sampletime){	
		KVelHeight = 1; 
		KVelForward = 0.3;
		KVelLateral = 0.4;
		KYaw = 1.5;
		Tintegral = 0.01;
		Ts = sampletime;
		errorheigt = 0;
		errorlateralvel = 0;
		errorforwardvel = 0;
		errorheading = 0;		
	}
		
	
	
		public void GetControlSignalSingle(RefinedSensorData rsdata, ReferenceData rrdata){
		//Coordinate system calculation
		SimpleMatrix errorpos = new SimpleMatrix(2,1,true,rrdata.getXpos() - rsdata.getXpos(),
														  rrdata.getYpos() - rsdata.getYpos());	
		SimpleMatrix pos2vel = new SimpleMatrix(2,2,true,Math.cos(rsdata.getYaw()), Math.sin(rsdata.getYaw()),
														-Math.sin(rsdata.getYaw()), Math.cos(rsdata.getYaw()));					 
		SimpleMatrix RDataVel = pos2vel.mult(errorpos);
		
		//Controlsignals
		ControlSignal.ForwardVelocity	=	KVelForward*(RDataVel.get(0) - rsdata.getForVel() );
		ControlSignal.LateralVelocity	=	KVelLateral*(RDataVel.get(1) - rsdata.getLatVel() );	
		ControlSignal.HeightVelocity	= 	KVelHeight*(rrdata.getHeight() - rsdata.getHeight());	
		ControlSignal.YawRate			=	KYaw*(rrdata.getYaw() - rsdata.getYaw());
		 
		 this.errorheigt = rrdata.getHeight() - rsdata.getHeight();
		 this.errorforwardvel = rrdata.getForVel() - rsdata.getForVel();
		 this.errorheading = rrdata.getYaw() - rsdata.getYaw();
		
		}

		
		
		 public void GetControlSignalMission(RefinedSensorData rsdata, ReferenceData rrdata){ 
		 //Constant speed controlling

			 
			 ControlSignal.ForwardVelocity		+=	(KVelForward+ Ts*Tintegral/2)*
					 								(rrdata.getForVel() - rsdata.getForVel())+
					 								(Ts*Tintegral/2 - KVelForward)*
					 								errorforwardvel;
			 
			 ControlSignal.YawRate 			   	=	KYaw*(rrdata.getYaw() - rsdata.getYaw());
			 ControlSignal.HeightVelocity		=	KVelHeight*(rrdata.getHeight() - rsdata.getHeight());
			 
			 //Errors 
			 this.errorheigt = rrdata.getHeight() - rsdata.getHeight();
			 this.errorforwardvel = rrdata.getForVel() - rsdata.getForVel();
			 this.errorheading = rrdata.getYaw() - rsdata.getYaw();			 
		 }
		 

		 
		 

	// Getters
	public double geterrorheigt() {
		return errorheigt;
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
