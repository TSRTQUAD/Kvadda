package kvaddakopter.assignment_planer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;


public class CalculateTrajectory {
	protected double[][] trajectory;

	public double[][] getTrajectory(MissionObject object) throws IOException, MatlabConnectionException, MatlabInvocationException {
		createMatFile(object);
		//calculateTrajectory();
		//trajectory = readMatFile();

		return trajectory;
	}

	public void createMatFile(MissionObject object) throws IOException {
		//Read data and put them in ML-classes
		MLChar mission = new MLChar( "mission", object.getMissionType() );
		MLDouble targetcoordinate = new MLDouble( "targetcoordinate", object.getTargetCoordinate() );
		MLDouble startcoordinate = new MLDouble( "startcoordinate", object.getStartCoordinate() );
		MLDouble height = new MLDouble( "height", object.getHeight(), 1 );
		MLDouble radius = new MLDouble( "radius", object.getRadius(), 1 );

		//Read search areas
		ArrayList<Area> searchareas = object.getSearchAreaCoordinates();
		MLCell area = new MLCell("area", new int[] {1,searchareas.size()});
		for (int i = 0 ; i < searchareas.size() ; i++) {
			area.set(new MLDouble( "area" + i, searchareas.get(i).area ), i);
		}

		//Read forbidden areas
		ArrayList<Area> forbiddenareas = object.getForbiddenAreaCoordinates();
		MLCell forbiddenarea = new MLCell("forbiddenarea", new int[] {1,forbiddenareas.size()});
		for (int i = 0 ; i < forbiddenareas.size() ; i++) {
			forbiddenarea.set(new MLDouble( "forbiddenarea" + i, forbiddenareas.get(i).area ), i);
		}

		//Write arrays to file
		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add( mission );
		list.add( area );
		list.add( forbiddenarea );
		list.add( targetcoordinate );
		list.add( startcoordinate );
		list.add( height );
		list.add( radius );

		new MatFileWriter( "object.mat", list );
	}

	public void calculateTrajectory() throws MatlabConnectionException, MatlabInvocationException {
		//Create a proxy, which we will use to control MATLAB
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();

		//Make script call
		proxy.eval("cd('src/kvaddakopter/assignment_planer/Matlab');assignmentplaner");

		//Terminate Matlab
		//proxy.eval("exit;");

		//Disconnect the proxy from MATLAB
		proxy.disconnect();
	}

	public double[][] readMatFile() throws FileNotFoundException, IOException {
		MatFileReader MLTrajectory = new MatFileReader( "trajectory.mat" );
		double[][] tmptrajectory = ((MLDouble) MLTrajectory.getMLArray("trajectory")).getArray();
		return tmptrajectory;
	}

	public void printTrajectory(double[][] trajectory) {
		
		for (int i = 0; i < trajectory.length; i++)
		{
			for (int j = 0; j < trajectory[0].length; j++)
			{
				System.out.print(trajectory[i][j]);
				System.out.print(", ");
			}

			System.out.println("");
		}
	}

}
