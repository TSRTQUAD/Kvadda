package kvaddakopter.assignment_planer;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

/**
 * This class is used for all tasks regarding the calculation of an trajectory.
 * @author tobiashammarling
 *
 */
public class CalculateTrajectory {

	/**
	 * Calls the Matlab script, the output is saved in a Mat-file called results.mat. The call <br>
	 * presumes that an object file i.e. object.mat have already been created and put in the Data <br>
	 * directory. This should be taken care of by the AssignmentPlanerRunnable.
	 * @throws MatlabConnectionException
	 * @throws MatlabInvocationException
	 */
	public void makeMatlabCall(MatlabProxyConnection matlabproxy) throws MatlabConnectionException, MatlabInvocationException{
		MatlabProxy proxy = matlabproxy.getMatlabProxy();

		System.out.println("Calculating Trajectory in Matlab ...");

		//Make script call
		proxy.eval("assignmentplaner");

	}


}
