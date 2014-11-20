package kvaddakopter.interfaces;

import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.control_module.signals.ControlSignal;

public interface ControlMainBusInterface {	
	public double[] getSensorVector();
	public MissionObject getMissionObject();
	
	
	public void setControlSignalobject(ControlSignal csignal);
	public MissionObject setMissionObject();
	public ControlSignal getControlSignalobject();
}
