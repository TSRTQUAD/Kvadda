package kvaddakopter.interfaces;

import kvaddakopter.communication.QuadData;

public interface MainBusCommInterface {
	/**
	 * Update the sensor information from the quad so that other modules can access it
	 * @param data
	 */
	public void setQuadData(QuadData data);
	/**
	 * Get current control signals
	 * @return
	 */
	public float[] getControlSignal();
	
	/**
	 * Get the flag that indicates emergency stop
	 * @return
	 */
	public boolean EmergencyStop();
	
	/**
	 * Get the flag that indicates whether initiation should start or not.
	 * @return
	 */
	public boolean shouldStart();
	
	/**
	 * Set if the unit run or not.
	 * @param isStarted
	 */
	public void setIsStarted(boolean isStarted);
	
	/**
	 * Set the flag that indiacates if the GPS fix is ok or not.
	 * @param b
	 */
	public void setGpsFixOk(boolean b);
	
	/**
	 * Set the flag which indicates if the wifi fix is ok or not.
	 * @param b
	 */
	public void setWifiFixOk(boolean b);
	
	/**
	 * Get the flag that indicates if the unit should run or not.
	 * @return
	 */
	public boolean isStarted();
	
	/**
	 * Sets the flag that indicates if the quad is armed or not.
	 * @param b
	 */
	public void setIsArmed(boolean b);
	/**
	 * Set to true when to start iniating
	 * @param b
	 */
	public void setShouldStart(boolean b);
	
	/**
	 * Get the flag that indicates if the quad is armed or not.
	 * @return
	 */
	public boolean getIsArmed();
	
	/**
	 * Sets the current control signals
	 * @param controlsignal
	 */
	public void setControlSignal(float[] controlsignal);
	
	/**
	 * Sets if the controller shopuld run or not.
	 * @param runctrl
	 */
	public void setRunController(boolean runctrl);
}
