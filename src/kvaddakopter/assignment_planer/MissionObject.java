package kvaddakopter.assignment_planer;

import java.util.ArrayList;

/**
 * Definition of an object for trajectory planing.
 */
public class MissionObject {
	// Variable declaration
	protected MissionType mission = MissionType.ALONG_TRAJECTORY;
	protected ArrayList<Area> searchareas = null;
	protected ArrayList<Area> forbiddenareas = null;
	protected double[][] startcoordinate = null;
	protected double[] height = null;
	protected double[] radius = null;
	
	// Get variables
	public MissionType getMissionType(){
		return this.mission;
	}
	public ArrayList<Area> getSearchAreaCoordinates(){
		return this.searchareas;
	}
	public ArrayList<Area> getForbiddenAreaCoordinates(){
		return this.forbiddenareas;
	}
	public double[][] getStartCoordinate(){
		return this.startcoordinate;
	}
	public double[] getHeight(){
		return this.height;
	}
	public double[] getRadius(){
		return this.radius;
	}
	
	// Assign values to the different variables.
	// Assign the mission type to variable mission.
	public void mission(MissionType missiontype){
		mission = missiontype;
	}
	// Assign the search area, line or target coordinates to variable searchareas.
	public void setSearchAreas(ArrayList<Area> searchcoordinates){
		searchareas =  searchcoordinates;
	}
	// Assign the forbidden area coordinates to variable forbiddenareas.
	public void setForbiddenAreas(ArrayList<Area> forbiddencoordinates){
		forbiddenareas =  forbiddencoordinates;
	}
	// Assign the startcoordinate to variable startcoordinate.
	public void setStartCoordinate(double[][] coord){
		startcoordinate =  coord;
	}
	// Assign the height to the variable height.
	public void setHeight(double[] heightvalue){
		height = heightvalue;
	}
	// Assign the radius to the variable	radius.
	public void setRadius(double[] radiusvalue){
		radius = radiusvalue;
	}
}
