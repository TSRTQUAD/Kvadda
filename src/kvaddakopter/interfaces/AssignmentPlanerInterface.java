package kvaddakopter.interfaces;

import kvaddakopter.assignment_planer.MatlabProxyConnection;
import kvaddakopter.assignment_planer.MissionObject;

public interface AssignmentPlanerInterface {
	/**
	 * Set the mission object of class MissionObject
	 * @param MO
	 */
	public void setMissionObject(MissionObject MO);
	
	/**
	 * Get the current mission object of class MissionObject
	 * @return
	 */
	public MissionObject getMissionObject();
	
	/**
	 * Starts AssignmentPlaner
	 */
	public void setAssignmentPlanerOn(boolean state);
	
	/**
	 * Get the current state of the AssignmentPlaner, if true the module is running.
	 * @return
	 */
	public boolean isAssignmentPlanerOn();
	
	/**
	 * Set the Matlab proxy connection variable of class MatlabProxyConnection
	 * @return
	 */
	public void setMatlabProxyConnection(MatlabProxyConnection MPC);
	
	/**
	 * Get the Matlab proxy connection variable of class MatlabProxyConnection
	 * @return
	 */
	public MatlabProxyConnection getMatlabProxyConnection();
	
	
}
