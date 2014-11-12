package kvaddakopter.assignment_planer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;

public class AssignmentPlanerMain {

	public static void main(String[] args) throws IOException, MatlabConnectionException, MatlabInvocationException {
		System.out.println("Program initialized \n");

		// Set up an MatlabProxy, for quiet startup, set argument to true.
		MatlabProxyConnection Matlab = new MatlabProxyConnection();
		Matlab.startMatlab("quiet");
		
		// Create an object to analyse
		MissionObject testobject = new MissionObject();
		testobject.setStartCoordinate(new double[][] {{58.3948,15.574976}});
		testobject.setHeight(new double[] {5});
		testobject.setRadius(new double[] {15});
		
		/*
		testobject.mission( MissionType.AROUND_COORDINATE );
		ArrayList<Area> searchareas = new ArrayList<Area>();
		Area targetcoordinate = new Area();
		targetcoordinate.area = new double[][] {{58.395157,15.574821}};
		searchareas.add( targetcoordinate );
		testobject.setSearchAreas(searchareas);
		*/
		
		/*
		testobject.mission( MissionType.ALONG_TRAJECTORY );
		ArrayList<Area> searchareas = new ArrayList<Area>();
		Area linesearch = new Area();
		linesearch.area = new double[][] {{58.395157,15.574821},
				{58.395227,15.574976},
				{58.395191,15.575202},
				{58.395073,15.575266},
				{58.394972,15.575041},
				{58.394904,15.574687},
				{58.394994,15.574526},
				{58.395107,15.574526}};
		searchareas.add( linesearch );
		testobject.setSearchAreas(searchareas);
		*/
		
		
		testobject.mission( MissionType.AREA_COVERAGE );
		// Create search areas
		ArrayList<Area> searchareas = new ArrayList<Area>();
		Area tmparea = new Area();
		tmparea.area = new double[][] {{58.395157,15.574821},
				{58.395227,15.574976},
				{58.395191,15.575202},
				{58.395073,15.575266},
				{58.394972,15.575041},
				{58.394904,15.574687},
				{58.394994,15.574526},
				{58.395107,15.574526}};
		Area tmparea1 = new Area();
		searchareas.add( tmparea );
		tmparea1.area = new double[][] {{58.394983,15.57606},
				{58.394916,15.576317},
				{58.394724,15.576092},
				{58.394685,15.575856},
				{58.394831,15.575759}};
		searchareas.add( tmparea1 );
		testobject.setSearchAreas(searchareas);

		// Create forbidden areas
		ArrayList<Area> forbiddenareas = new ArrayList<Area>();
		Area tmpforbiddenarea = new Area();
		tmpforbiddenarea.area = new double[][] {{58.395067,15.574794},
				{58.395067,15.575019},
				{58.394944,15.574912}};
		forbiddenareas.add( tmpforbiddenarea );
		testobject.setForbiddenAreas(forbiddenareas);
		

		
		// Calculate the trajectory
		CalculateTrajectory calculatetrajectory = new CalculateTrajectory(Matlab);
		double[][] trajectory = calculatetrajectory.getTrajectory(testobject);

		// Print the calculated trajectory and corresponding data
		AssignmentPlanerMain.print3D("Trajectory",trajectory);
		AssignmentPlanerMain.print3D("Velocity",testobject.getReferenceVelocity());
		AssignmentPlanerMain.print3D("Trajectory length",testobject.getTrajectoryLength());
		AssignmentPlanerMain.print3D("Coverage area",testobject.getCoverageArea());
		AssignmentPlanerMain.print3D("Mission time",testobject.getMissionTime());
		
		
		/*
		MatlabProxy proxy = Matlab.getMatlabProxy();
		System.out.println("Matlab called");
	    //By specifying 3 return arguments, returns as String arrays the loaded M-files, MEX files, and Java classes
	    Object[] inmem = proxy.returningFeval("inmem", 3);
	    System.out.println("Java classes loaded:");
	    System.out.println(Arrays.toString((String[]) inmem[2]));
	    
	    long starttime = System.currentTimeMillis();
	    for (int counter=1; counter < 1001; counter++) {
	    //Retrieve MATLAB's release date by providing the -date argument
	    Object[] releaseDate = proxy.returningFeval("version", 1, "-date");
	    }
	    System.out.println(System.currentTimeMillis() - starttime + "\n");
	    */

		
		Matlab.terminateMatlab();
		System.out.println("\nProgram terminated");
	}
	
	public static void print3D(String string, double[][] variable) {
		System.out.println(string + ": ");
		for (int i = 0; i < variable.length; i++)
		{
			for (int j = 0; j < variable[0].length; j++)
			{
				System.out.print(variable[i][j]);
				System.out.print(" ");
			}

			System.out.println("");
		}
	}

}
