package kvaddakopter.interfaces;

import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.control_module.signals.ControlSignal;

public interface ControlMainBusInterface {	
	public double[] getSensorVector();
	public ControlSignal getControlSignal();
	public MissionObject getMissionObject();
	
	
	public void setControlSignal(ControlSignal csignal);
	public MissionObject setMissionObject();
}
