package kvaddakopter.Mainbus;

import java.io.IOException;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import kvaddakopter.assignment_planer.CalculateTrajectory;

/**
 * Example of how to make threads
 * 1) Make the classes implement runnable
 * 2) Class should have volatile member Mainbus mainbus
 * 3) Constructor of class should set mainbus reference
 * 4) Implement run() method
 * 
 * When calling mb use synchronized! This handles multiple accesses.
 * OBS! This is not needed if get/set functions are declared as synchronized in mainbus
 * OBS! Do not put entire methods inside synchronized. Just the get/set from mainbus
 * 
 * GUI can initiate other threads with the use of condition variables 
 * The other threads can wait until GUI sets their respective conditionvariables
 * See example code
 */
public class AssignmentPlanerRunnable implements Runnable {

	private Mainbus mainbus;
	private int mThreadId;
	private CalculateTrajectory calculatetrajectory;

	public AssignmentPlanerRunnable(int threadid, Mainbus MB) {
		mainbus = MB;
		mThreadId = threadid;
	}

	public void run() {
		waitOnCondVar();
		calculatetrajectory = new CalculateTrajectory(mainbus.getMatlabProxy());
		try {
			calculatetrajectory.createMatFile(mainbus);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			calculatetrajectory.makeMatlabCall();
		} catch (MatlabConnectionException | MatlabInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			calculatetrajectory.readMatFile(mainbus);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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