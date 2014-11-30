package kvaddakopter.assignment_planer;

import kvaddakopter.interfaces.AssignmentPlanerInterface;
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
	 * Calls the Matlabscript, the output is saved in a Mat-file called results.mat.
	 * @throws MatlabConnectionException
	 * @throws MatlabInvocationException
	 */
	public void makeMatlabCall(MatlabProxyConnection matlabproxy) throws MatlabConnectionException, MatlabInvocationException{
		MatlabProxy proxy = matlabproxy.getMatlabProxy();

		System.out.println("Calculating Trajectory in Matlab ...");

		//Make script call
		proxy.eval("assignmentplaner");

	}
	
	public double coveragePercentage(AssignmentPlanerInterface mainbus) {
		double realnrofpoints = mainbus.getMissionObject().getTrajectory().length;
		double nrofvisitedpoints = mainbus.getVisitedPoints();
		double percent = Math.min((realnrofpoints - nrofvisitedpoints)/realnrofpoints, 100);
		
		return percent;
		
	}
	
	public double coverageArea(AssignmentPlanerInterface mainbus) {
		double coveragearea = coveragePercentage(mainbus)*mainbus.getMissionObject().getCoverageArea()[0][0];
		
		return coveragearea;
		
	}

}
