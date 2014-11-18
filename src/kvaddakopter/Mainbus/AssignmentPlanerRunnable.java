package kvaddakopter.Mainbus;

import java.io.IOException;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import kvaddakopter.assignment_planer.CalculateTrajectory;
import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MissionObject;

public class AssignmentPlanerRunnable implements Runnable {

	private Mainbus mainbus;
	private int mThreadId;
	private CalculateTrajectory calculatetrajectory;
	private MissionObject missionobject;

	public AssignmentPlanerRunnable(int threadid, Mainbus MB) {
		mainbus = MB;
		mThreadId = threadid;
	}

	public void run() {
		waitOnCondVar();
		calculatetrajectory = new CalculateTrajectory(mainbus.getMatlabProxy());
		try {
			new MatFileHandler().createMatFile("object", mainbus.getMissionObject());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			calculatetrajectory.makeMatlabCall();
		} catch (MatlabConnectionException | MatlabInvocationException e) {
			e.printStackTrace();
		}
		try {
			missionobject = new MatFileHandler().readMatFile("results", mainbus.getMissionObject());
			mainbus.setMissionObject(missionobject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		resetCondVar();

	}

	public void waitOnCondVar(){
		while (!mainbus.isAssignmentPlanerOn())
			try{
				synchronized(mainbus){
					mainbus.wait();
				}

			}
		catch (InterruptedException e) {}
	}

	public synchronized void resetCondVar(){
		mainbus.setAssignmentPlanerOn(false);
		synchronized(mainbus){
			mainbus.notify ();
		}

	}

}