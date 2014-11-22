package kvaddakopter.gui.interfaces;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.MatlabProxyConnection;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.interfaces.AssignmentPlanerInterface;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.maps.GPSCoordinate;

public class MockMainBus implements MainBusGUIInterface, AssignmentPlanerInterface{
	
	protected ArrayList<GPSCoordinate> coords = new ArrayList<>();
	
	
	protected int counter = -1;
	protected int inc = 1;
	protected MissionObject missionobject;
	protected boolean assignmentplanerstatus;
	protected MatlabProxyConnection matlabproxy;
	
	
	
	public MockMainBus() {
		
		coords.add(new GPSCoordinate(58.406659, 15.620358));
		coords.add(new GPSCoordinate(58.406674, 15.620414));
		coords.add(new GPSCoordinate(58.406786, 15.620588));
		coords.add(new GPSCoordinate(58.406854, 15.620936));
		coords.add(new GPSCoordinate(58.406782, 15.621156));
		coords.add(new GPSCoordinate(58.406768, 15.621454));
		coords.add(new GPSCoordinate(58.406857, 15.621765));
		coords.add(new GPSCoordinate(58.406906, 15.622006));
	}

	@Override
	public double getCurrentSpeed() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public GPSCoordinate getCurrentQuadPosition() {
		this.counter += this.inc;
		if(this.counter== 0) this.inc = 1;
        if(this.counter == coords.size() -1){this.inc = -1;}
		
		return coords.get(this.counter);
	}

	@Override
	public boolean wifiFixOk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean gpsFixOk() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAssignmentPlanerOn(boolean state) {
		this.assignmentplanerstatus = state;
		
	}

	@Override
	public void setMissionObject(MissionObject MO) {
		this.missionobject = MO;
		
	}

	@Override
	public MissionObject getMissionObject() {
		return missionobject;
	}

	@Override
	public boolean isAssignmentPlanerOn() {
		return assignmentplanerstatus;
	}

	@Override
	public MatlabProxyConnection getMatlabProxy() {
		return matlabproxy;
	}
	
	public void setMatlabProxy(MatlabProxyConnection MP) {
		matlabproxy = MP;
	}
	
}
