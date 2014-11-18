package kvaddakopter.assignment_planer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import kvaddakopter.Mainbus.Mainbus;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

/**
 * This class is used for all tasks regarding the calculation of an trajectory.
 * @author tobiashammarling
 *
 */
public class CalculateTrajectory {
	protected MatlabProxyConnection Matlab;

	public CalculateTrajectory(MatlabProxyConnection matlab) {
		this.Matlab = matlab;
	}

	/**
	 * Calculates the trajectory and saves all obtained information such as; coverage area, <br>
	 * estimated time, the trajectory itself and an velocity reference vector for the regulator.
	 * @param missionobject
	 */
	public void calculateTrajectory(MissionObject missionobject) throws IOException, MatlabConnectionException, MatlabInvocationException {
		Mainbus mainbus = new Mainbus();
		mainbus.setMissionObject(missionobject);
		
		createMatFile(mainbus);
		makeMatlabCall();
		readMatFile(mainbus);
		
	}

	/**
	 * Creates a Mat-file to be loaded in Matlab by the Matlabscript. All values are obtained <br>
	 * from the input classvariable missionobject.
	 * @param missionobject
	 * @throws IOException
	 */
	public void createMatFile(Mainbus mainbus) throws IOException {
		//Get mission object
		MissionObject missionobject = mainbus.getMissionObject();
		
		// Declare local variables
		MLCell area = new MLCell("area", new int[] {1,1});
		MLCell forbiddenarea = new MLCell("forbiddenarea", new int[] {1,1});

		//Read data and put them in ML-classes
		MLDouble mission = new MLDouble( "mission", new double[][] {{missionobject.getMissionType().getValue()}});
		MLDouble startcoordinate = new MLDouble( "startcoordinate", missionobject.getStartCoordinate() );
		MLDouble height = new MLDouble( "height", missionobject.getHeight(), 1 );
		MLDouble radius = new MLDouble( "radius", missionobject.getRadius(), 1 );

		//Read search areas
		ArrayList<Area> searchareas = missionobject.getSearchAreaCoordinates();
		if (searchareas != null) {
			area = new MLCell("area", new int[] {1,searchareas.size()});
			for (int i = 0 ; i < searchareas.size() ; i++) {
				area.set(new MLDouble( "area" + i+1, searchareas.get(i).area ), i);
			}
		}
		else {
			area.set(new MLDouble( "area" + 1, new double[][] {{0,0}} ), 0);
		}

		//Read forbidden areas
		ArrayList<Area> forbiddenareas = missionobject.getForbiddenAreaCoordinates();
		if (forbiddenareas != null) {
			forbiddenarea = new MLCell("forbiddenarea", new int[] {1,forbiddenareas.size()});
			for (int i = 0 ; i < forbiddenareas.size() ; i++) {
				forbiddenarea.set(new MLDouble( "forbiddenarea" + i+1, forbiddenareas.get(i).area ), i);
			}
		}
		else {
			forbiddenarea.set(new MLDouble( "forbiddenarea" + 1, new double[][] {{0,0}} ), 0);
		}

		
		//Write arrays to file
		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add( mission );
		list.add( area );
		list.add( forbiddenarea );
		list.add( startcoordinate );
		list.add( height );
		list.add( radius );
		new MatFileWriter( "object.mat", list );
		
	}

	/**
	 * Calls the Matlabscript, the output is saved in a Mat-file called results.mat.
	 * @throws MatlabConnectionException
	 * @throws MatlabInvocationException
	 */
	public void makeMatlabCall() throws MatlabConnectionException, MatlabInvocationException{
		MatlabProxy proxy = this.Matlab.getMatlabProxy();
		
		System.out.println("Making Matlab call");
		
		//Make script call
		proxy.eval("cd('src/kvaddakopter/assignment_planer/Matlab')");
		proxy.eval("assignmentplaner");

	}

	/**
	 * Reads the Mat-file and saves the obtained results in the assigned MissionObject.
	 * @param missionobject
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readMatFile(Mainbus mainbus) throws FileNotFoundException, IOException {
		MatFileReader MLResults = new MatFileReader( "results.mat" );
		
		//Get mission object
		MissionObject missionobject = mainbus.getMissionObject();
		
		//Save trajectory and corresponding data to missionobject
		double[][] trajectory = ((MLDouble) MLResults.getMLArray("trajectory")).getArray();
		missionobject.setTrajectory(trajectory);
		missionobject.setTrajectoryLength(((MLDouble) MLResults.getMLArray("trajectorylength")).getArray());
		missionobject.setCoverageArea(((MLDouble) MLResults.getMLArray("area")).getArray());
		missionobject.setMissionTime(((MLDouble) MLResults.getMLArray("time")).getArray());
		missionobject.setReferenceVelocity(((MLDouble) MLResults.getMLArray("velocity")).getArray());
		
		//Save mission object to mainbus
		mainbus.setMissionObject(missionobject);
		
	}

}
