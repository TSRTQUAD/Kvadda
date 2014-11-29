package kvaddakopter.assignment_planer;

import java.util.ArrayList;

/**
 * Definition of an object for trajectory planing.
 */
public class MissionObject {
	
	// Variable declaration
	protected String missionName = "";
	
	protected MissionType mission = MissionType.NULL_MISSION;
	
	protected ArrayList<Area> searchareas 		= null;
	protected ArrayList<Area> forbiddenareas 	= null;

	protected double[][] startcoordinate 		= {{0}};
	protected double[] height 					= {0};
	protected double[] radius 					= {0};
	
	protected int imageTemplte 					= 0;
	protected int imageTemplate 				= 0;
	protected int colorTemplate 				= 0;
	protected int descriptor 					= 0;


	protected double[] returndata;
	protected double waitingtime 				= 15;
	protected double yaw 						= 0;
	
	
	
	
	// Get variables
	protected double[][] trajectory 			= {{0}};
	protected double[][] trajectoryfullsize 	= {{0}};
	protected double[][] trajectorylength 		= {{0}};
	protected double[][] coveragearea 			= {{0}};
	protected double[][] missiontime 			= {{0}};
	protected double[][] referencevelocity 		= {{0}};


	/*
	 * Get functions
	 */

	public String getMissionName(){
		return this.missionName;
	}
	
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
	public double getHeight(){
		return this.height[0];
	}
	public double[] getRadius(){
		return this.radius;
	}
	public int getImageTemplate(){
		return this.imageTemplate;
	}
	public int getColorTemplate(){
		return this.colorTemplate;
	}
	public int getDescriptor(){
		return this.descriptor;
	}
	public double[][] getTrajectory(){
		return this.trajectory;
	}
	public double[][] getTrajectoryFullSize(){
		return this.trajectoryfullsize;
	}
	public double[][] getTrajectoryLength(){
		return this.trajectorylength;
	}
	public double[][] getCoverageArea(){
		return this.coveragearea;
	}
	public double[][] getMissionTime(){
		return this.missiontime;
	}
	public double[][] getReferenceVelocity(){
		return this.referencevelocity;
	}
	
	
	/*
	 * Set functions
	 */
	// Assign the mission type to variable mission.
	public void setMissionType(MissionType missiontype){
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
	// Assign the radius to the variable radius.
	public void setRadius(double[] radiusvalue){
		radius = radiusvalue;
	}
	
	public void setImageTemplate(int template){
		imageTemplate = template;
	}
	
	public void setColorTemplate(int color){
		colorTemplate = color;
	}
	
	public void setDescriptor(int descriptor){
		this.descriptor = descriptor;
	}
	
	public void setTrajectory(double[][] tmptrajectory){
		this.trajectory =  tmptrajectory;
	}
	
	public void setTrajectoryFullSize(double[][] tmptrajectoryfullsize){
		this.trajectoryfullsize =  tmptrajectoryfullsize;
	}
	
	public void setTrajectoryLength(double[][] length){
		this.trajectorylength = length;
	}
	
	public void setCoverageArea(double[][] area){
		this.coveragearea = area;
	}
	
	public void setMissionTime(double[][] time){
		this.missiontime = time;
	}
	
	public void setReferenceVelocity(double[][] velocity){
		this.referencevelocity = velocity;
	}
	
	public void setMissionName(String name){
		this.missionName = name;
	}
	
	public String toString(){
		if(this.mission == MissionType.NULL_MISSION){
			return "Väj ett uppdrag...";
		}
		return this.missionName;
	}

	public double getWaitingtime() {
		return waitingtime;
	}

	public void setWaitingtime(double waitingtime) {
		this.waitingtime = waitingtime;
	}

	public double getYaw() {
		return yaw;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}
	
}
