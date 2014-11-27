package kvaddakopter.interfaces;

public interface ManualControlInterface {
	public void setSpeed(float spd);
	public void setEmergencyStop(boolean newBool);
	boolean getManualControl();
	public void setManualControl(boolean mcb);
	public void setControlSignal(float[] controlsignal);
	public boolean getRunController();	
	public void setRunController(boolean runctrl);
	
}