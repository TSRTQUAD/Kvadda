package kvaddakopter.interfaces;

import kvaddakopter.communication.QuadData;

public interface MainBusCommInterface {
	
	public void setQuadData(QuadData data);
	public float[] getControlSignal();
	public boolean EmergencyStop();
	public void setSelfCheck(boolean b);
	//public boolean getStartPermission();
	public boolean shouldStart();
	public void setIsStarted(boolean isStarted);
	String getMode();
	public void setGpsFixOk(boolean b);
	public void setWifiFixOk(boolean b);
	public boolean isStarted();
	public void setIsArmed(boolean b);
	/**
	 * Sets to true when a mission should start
	 * @param b
	 */
	public void setShouldStart(boolean b);
	public boolean getIsArmed();
	public void setControlSignal(float[] controlsignal);
	public void setRunController(boolean runctrl);
}
