package kvaddakopter.interfaces;

import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.communication.QuadData;
import kvaddakopter.control_module.signals.ControlSignal;

public interface ControlMainBusInterface {	
	public QuadData getQuadData();
	public MissionObject getMissionObject();
	public void setControlSignalobject(ControlSignal csignal);
	public boolean isStarted();
}
