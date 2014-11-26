package kvaddakopter.interfaces;

import kvaddakopter.communication.QuadData;

public interface MainBusCommInterface {
	public void setQuadData(QuadData data);
	public float[] getControlSignal();
	public boolean EmergencyStop();
	public void setSelfCheck(boolean b);
	public boolean getStartPermission();
	String getMode();
}
