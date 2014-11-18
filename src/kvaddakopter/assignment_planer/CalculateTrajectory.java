package kvaddakopter.assignment_planer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

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
	protected double[][] trajectory;
	protected double trajectorylength;
	protected MatlabProxyConnection Matlab;

	public CalculateTrajectory(MatlabProxyConnection matlab) {
		this.Matlab = matlab;
	}

	/**
	 * Calculates the trajectory and saves all obtained information such as; coverage area, <br>
	 * estimated time, the trajectory itself and an velocity reference vector for the regulator.
	 * @param object
	 */
	public void calculateTrajectory(MissionObject object) throws IOException, MatlabConnectionException, MatlabInvocationException {
		createMatFile(object);
		calculateTrajectory();
		readMatFile(object);
	}

	/**
	 * Creates a Mat-file to be loaded in Matlab by the Matlabscript.
	 * @param object
	 * @throws IOException
	 */
	public void createMatFile(MissionObject object) throws IOException {
		// Declare local variables
		MLCell area = new MLCell("area", new int[] {1,1});
		MLCell forbiddenarea = new MLCell("forbiddenarea", new int[] {1,1});

		//Read data and put them in ML-classes
		MLDouble mission = new MLDouble( "mission", new double[][] {{object.getMissionType().getValue()}});
		MLDouble startcoordinate = new MLDouble( "startcoordinate", object.getStartCoordinate() );
		MLDouble height = new MLDouble( "height", object.getHeight(), 1 );
		MLDouble radius = new MLDouble( "radius", object.getRadius(), 1 );

		//Read search areas
		ArrayList<Area> searchareas = object.getSearchAreaCoordinates();
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
		ArrayList<Area> forbiddenareas = object.getForbiddenAreaCoordinates();
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
		
		
		/*
		//Create a structure of the mission
		MLStructure missionobject = new MLStructure("object", new int[] {6,1});
		missionobject.setField("mission", mission);
		missionobject.setField("area", area);
		missionobject.setField("forbidden", forbiddenarea);
		missionobject.setField("startcoordinate", startcoordinate);
		missionobject.setField("height", height);
		missionobject.setField("radius", radius);
		*/
		
	}

	/**
	 * Calls the Matlabscript which saves the results in a Mat-file.
	 * @throws MatlabConnectionException
	 * @throws MatlabInvocationException
	 */
	public void calculateTrajectory() throws MatlabConnectionException, MatlabInvocationException{
		MatlabProxy proxy = this.Matlab.getMatlabProxy();
		
		System.out.println("Making Matlab call");
		
		//Make script call
		proxy.eval("cd('src/kvaddakopter/assignment_planer/Matlab');assignmentplaner");

	}

	/**
	 * Reads the Mat-file and saves the obtained results in assigned MissionObject.
	 * @param object
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readMatFile(MissionObject object) throws FileNotFoundException, IOException {
		MatFileReader MLResults = new MatFileReader( "results.mat" );
		
		// Save trajectory and corresponding data to MissionObject
		double[][] trajectory = ((MLDouble) MLResults.getMLArray("trajectory")).getArray();
		object.setTrajectory(trajectory);
		object.setTrajectoryLength(((MLDouble) MLResults.getMLArray("trajectorylength")).getArray());
		object.setCoverageArea(((MLDouble) MLResults.getMLArray("area")).getArray());
		object.setMissionTime(((MLDouble) MLResults.getMLArray("time")).getArray());
		object.setReferenceVelocity(((MLDouble) MLResults.getMLArray("velocity")).getArray());
		
	}

}
