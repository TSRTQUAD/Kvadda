package kvaddakopter.assignment_planer;

import java.io.IOException;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import kvaddakopter.interfaces.AssignmentPlanerInterface;

/**
 * The runnable file for the Assigmentplaner.
 * @author tobiashammarling
 *
 */
public class AssignmentPlanerRunnable implements Runnable {

	private AssignmentPlanerInterface mainbus;
	// private int mThreadId;
	private CalculateTrajectory calculatetrajectory;
	private MissionObject missionobject;

	/**
	 * Constructor for the AssignmentPlanerRunnable. The input parameter MB should fulfill <br>
	 * the AssignmentPlanerInterface conditions.
	 * @param threadid
	 * @param MB
	 */
	public AssignmentPlanerRunnable(int threadid, AssignmentPlanerInterface MB) {
		mainbus = MB;
		// mThreadId = threadid;
	}

	/**
	 * Runs continuously when the module is started.
	 */
	public void run() {
		while (true) {
			//Wait for GUI to start the AssignmentPlaner
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

	}

	/**
	 * Puts the module in hold until it's called from the GUI. Activation is handled with <br>
	 * the conditional variable isAssigmentPlanerOn.
	 */
	public void waitOnCondVar(){
		while (!mainbus.isAssignmentPlanerOn()) {
			System.out.println("AssignmentPlaner waiting on conditional variable from from GUI");
			try{
				synchronized(mainbus){
					mainbus.wait();
				}

			}
			catch (InterruptedException e) {}
		}
	}

	/**
	 * Resets the conditional variable used by the GUI and AssignmentPlanerRunnable to notify <br>
	 * each other if the assignmentplaner is activated or not.
	 */
	public void resetCondVar(){
		mainbus.setAssignmentPlanerOn(false);
		synchronized(mainbus){
			mainbus.notifyAll();
		}
	}

}