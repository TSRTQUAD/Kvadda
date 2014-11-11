package kvaddakopter.control_module.signals;

public class ControlSignal{
	public static double YawRate;
	public static double HeightVelocity;
	public static double ForwardVelocity;
	public static double LateralVelocity;		
	
	
	
	
	
	public ControlSignal(){
		ForwardVelocity = 0;
		HeightVelocity = 0;
		LateralVelocity = 0;
		YawRate = 0;
	}
	
	public void print(){
		System.out.format("Yawrate: %.2f%n", YawRate);
		System.out.format("Heightvelocity: %.1f%n", HeightVelocity);
		System.out.format("Forwardvelocity: %.2f%n", ForwardVelocity);
		System.out.format("Lateralvelocity: %.2f%n", LateralVelocity);
		
	}
}

