package kvaddakopter.interfaces;

public interface ManualControlInterface {
	public void setEmergencyStop(boolean newBool);
	boolean getManualControl();
	public void setManualControl(boolean mcb);
	public void setControlSignal(float[] controlsignal);
	public boolean getRunController();	
	public void setRunController(boolean runctrl);
	public float[] getControlSignal();
	public boolean isStarted();
	public void setIsStarted(boolean b);
	public void setShouldStart(boolean b);
	
}