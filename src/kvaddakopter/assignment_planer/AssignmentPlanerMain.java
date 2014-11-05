package kvaddakopter.assignment_planer;

import java.io.IOException;
import java.util.ArrayList;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;

public class AssignmentPlanerMain {

	public static void main(String[] args) throws IOException, MatlabConnectionException, MatlabInvocationException {
		System.out.println("Program initialized \n");
		
		// Create an object to analyse
		MissionObject testobject = new MissionObject();
		testobject.mission("coordinatesearch");
		testobject.setTargetCoordinate(new double[][] {{58.3949, 15.5742}});
		testobject.setHeight(new double[] {1});
		testobject.setRadius(new double[] {1});
		
		// Create search areas
		ArrayList<Area> searchareas = new ArrayList<Area>();
		Area tmparea = new Area();
		tmparea.area = new double[][] {{58.3949, 15.5742},{58.39493, 15.57423},
				{58.39492, 15.57423},{58.39491, 15.57423},{58.39491, 15.57424}};
		searchareas.add( tmparea );
		tmparea.area = new double[][] {{58.3948, 15.5732},{58.39483, 15.57433},{58.39491, 15.57423}};
		searchareas.add( tmparea );
		tmparea.area = new double[][] {{58.3949, 15.57323},{58.394836, 15.574332},{58.394912, 15.574231}};
		searchareas.add( tmparea );
		tmparea.area = new double[][] {{58.3949, 15.57322},{58.394836, 15.574335},{58.394911, 15.574231}};
		searchareas.add( tmparea );
		testobject.setSearchAreas(searchareas);
		
		// Create forbidden areas
		ArrayList<Area> forbiddenareas = new ArrayList<Area>();
		Area tmpforbiddenarea = new Area();
		tmpforbiddenarea.area = new double[][] {{58.3949, 15.5742},{58.39493, 15.57423},
				{58.39492, 15.57423},{58.39491, 15.57423},{58.39491, 15.57424}};
		forbiddenareas.add( tmparea );
		tmpforbiddenarea.area = new double[][] {{58.3948, 15.5732},{58.39483, 15.57433},{58.39491, 15.57423}};
		forbiddenareas.add( tmparea );
		testobject.setForbiddenAreas(forbiddenareas);
		
		// Calculate the trajectory
		CalculateTrajectory calculatetrajectory = new CalculateTrajectory();
		double[][] trajectory = calculatetrajectory.getTrajectory(testobject);
		
		// Print the calculated trajectory
		calculatetrajectory.printTrajectory(trajectory);
		
		System.out.println("\nProgram terminated");
	}

}
