package kvaddakopter.Mainbus;

import java.io.IOException;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import kvaddakopter.assignment_planer.CalculateTrajectory;
import kvaddakopter.assignment_planer.MatFileHandler;
import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.interfaces.AssignmentPlanerInterface;

public class AssignmentPlanerRunnable implements Runnable {

	private AssignmentPlanerInterface mainbus;
	// private int mThreadId;
	private CalculateTrajectory calculatetrajectory;
	private MissionObject missionobject;

	public AssignmentPlanerRunnable(int threadid, AssignmentPlanerInterface MB) {
		mainbus = MB;
		// mThreadId = threadid;
	}

	public void run() {
		//Wait for GUI to start AssignmentPlaner
		waitOnCondVar();

		System.out.println("Assignmentplaner running ...");

		calculatetrajectory = new CalculateTrajectory();
		try {
			new MatFileHandler().createMatFile("object", mainbus.getMissionObject());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			calculatetrajectory.makeMatlabCall(mainbus.getMatlabProxyConnection());
		} catch (MatlabConnectionException | MatlabInvocationException e) {
			e.printStackTrace();
		}
		try {
			missionobject = new MatFileHandler().readMatFile("results", mainbus.getMissionObject());
			mainbus.setMissionObject(missionobject);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Notify mainbus that calculations is done.
		resetCondVar();

	}

	public void waitOnCondVar(){
		while (!mainbus.isAssignmentPlanerOn()) {
			System.out.println("Waiting on conditional variable from from GUI");
			try{
				synchronized(mainbus){
					mainbus.wait();
				}

			}
			catch (InterruptedException e) {}
		}
	}

	public void resetCondVar(){
		mainbus.setAssignmentPlanerOn(false);
		synchronized(mainbus){
			mainbus.notify ();
		}
	}

}