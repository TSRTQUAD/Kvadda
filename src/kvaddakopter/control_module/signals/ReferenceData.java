package kvaddakopter.control_module.signals;

import kvaddakopter.assignment_planer.MissionObject;
import kvaddakopter.assignment_planer.ReferenceExtractor;


/*This module is split into three different functions depending on the integer mission
 * 
 * mission = 0: If mission is set to 0 then reference data is updated when quadcopter is close 
 * enough to reference coordinate and has been there for reference time. 
 * Obs: Yaw is set as a reference and is constant in between reference coordinates
 * 
 * mission = 1: If mission is set to 1 then the quad is controlled only by adjusting yaw angle
 * to always point towards next reference coordinate. Lateral velocity is set to 0 and forward 
 * velocity is set to constant/reference.
 * 
 * 
 * mission = 2: This kind of mission is for indoor testing. Reference data is updated every 
 * sampling interval and yaw angle is set by reference.
 * 
 * */

public class ReferenceData {
	public double Yaw;
	public double Latitud;
	public double Longitud;
	public double Height;
	public double ForVel;
	public double time;
	public int mission;
	public int start;
	public int land;
	public double[] latestreference;
	
	public double Xpos;
	public double Ypos;
	long lStartTime, lEndTime, difference;
	private boolean running;
	public double initiallat;
	public double initiallon;
	public double radius;
	private ReferenceExtractor referenceextractor = new ReferenceExtractor();
	
	
	// Initialize coordinate system XY with origo in Initiallat and initiallon
	public void initialize(double inlat, double inlong){		
	this.initiallat = inlat;
	this.initiallon = inlong;
	}
	
		
	public void GPS2XY(){
		double lat1=initiallat*Math.PI/180;
		double lon1=initiallon*Math.PI/180;
		
		double lat2=Latitud*Math.PI/180;
		double lon2=Longitud*Math.PI/180;

		double deltaLat=lat2-lat1;
		double deltaLon=lon2-lon1;

		this.Ypos=radius*deltaLon*Math.cos((lat1+lat2)/2)*1000;
		this.Xpos=radius*deltaLat*1000;
	}
	
	public void updateref(double[] latestreference){
// latestreference  = (latitude, longitud, height, yaw, time, 
//				  forward velocity, mission type, start,land)
	this.Latitud = latestreference[0];
	this.Longitud = latestreference[1];
	this.Height = latestreference[2];
	this.Yaw = latestreference[3];
	this.time = latestreference[4];
	this.ForVel = latestreference[5];
	this.mission = (int) latestreference[6];
	this.start = (int) latestreference[7];
	this.land = (int) latestreference[8];
}
	

	
	public void update(RefinedSensorData rsdata, MissionObject missionobject){
		//mission = FALSE


		
		if (Math.abs(rsdata.getHeight()-Height)<1 && 
			Math.abs(rsdata.getXpos()-Xpos)<0.3   && 
			Math.abs(rsdata.getYpos()-Ypos)<0.3   &&
			mission==0										){
			
			if (!running){
				lStartTime= System.currentTimeMillis();
				this.running = true;
			}
			
			if (running && Math.abs(System.currentTimeMillis() - lStartTime) > time){
			this.running = false;
			this.updateref( referenceextractor.update(missionobject) );	
			System.out.println("-.-.-.-.-.-.-.-.-.-.-.-"); //update ref 
			System.out.println("Updated referencedata mission = 0"); //update ref 	
			System.out.println("-.-.-.-.-.-.-.-.-.-.-.-"); //update ref 
			}
		}	
		// mission = TRUE
		//Update Yaw every iteration and Reference data if close enough
		else if	(	Math.abs(rsdata.getHeight()-Height)<1 && 
				Math.abs(rsdata.getXpos()-Xpos)<0.3 && 
				Math.abs(rsdata.getYpos()-Ypos)<0.3 &&
				mission==1){

			this.updateref( referenceextractor.update(missionobject) );		//update ref 
			
			System.out.println("-.-.-.-.-.-.-.-.-.-.-.-"); //update ref 
			System.out.println("Updated referencedata mission = 1"); //update ref 	
			System.out.println("-.-.-.-.-.-.-.-.-.-.-.-"); //update ref 			
		}		
		
		
		if (mission==1){
			
		if (Ypos == rsdata.getYpos()){
				if (Xpos > rsdata.getXpos()){
					this.Yaw = -Math.PI/2;
				}
				else{
					this.Yaw = Math.PI/2;
				}
					
		}
		else if (  Xpos >= rsdata.getXpos()  && Ypos > rsdata.getYpos() ){ 				
			this.Yaw = Math.atan( (rsdata.getXpos() - Xpos)  /  (Ypos - rsdata.getYpos()) ); 			
		}
		else if (  Xpos < rsdata.getXpos()  && Ypos > rsdata.getYpos() ){ 				
			this.Yaw = Math.atan( (rsdata.getXpos() - Xpos) /   (Ypos - rsdata.getYpos()) ); 			
		}
		else if (  Xpos < rsdata.getXpos()  && Ypos < rsdata.getYpos() ){ 				
			this.Yaw = Math.PI - Math.atan( (rsdata.getXpos() - Xpos) /   (rsdata.getYpos() - Ypos) ); 			
		}
		else if (  Xpos > rsdata.getXpos()  && Ypos < rsdata.getYpos() ){ 				
			this.Yaw = - (Math.PI - Math.atan( (Xpos - rsdata.getXpos()) /   (rsdata.getYpos() - Ypos) )); 			
		}
		
		}
		
		
		
		
		if (mission==2){
			this.updateref( referenceextractor.update(missionobject) );		//update ref  
		}		
	}
	
	
	public void print(){
		System.out.println("");
		System.out.format("Yaw: %.2f%n", Yaw);
		System.out.format("X: %.1f%n", Xpos);
		System.out.format("Y: %.2f%n", Ypos);
		System.out.format("Forwardvelocity: %.2f%n", ForVel);
		System.out.format("Start: %d%n", start);
		System.out.format("Land: %d%n", land);
		System.out.format("Mission: %d%n", mission);
		System.out.println("");
	}
	
	
	// Get Put
	public double getYaw() {
		return Yaw;
	}
	public void setYaw(double yaw) {
		Yaw = yaw;
	}
	public double getXpos() {
		return Xpos;
	}
	public void setXpos(double xpos) {
		Xpos = xpos;
	}
	public double getYpos() {
		return Ypos;
	}
	public void setYpos(double ypos) {
		Ypos = ypos;
	}
	public double getHeight() {
		return Height;
	}
	public void setHeight(double height) {
		Height = height;
	}

	public double getForVel() {
		return ForVel;
	}

	public void setForVel(double forVel) {
		ForVel = forVel;
	}


	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getLand() {
		return land;
	}


	public void setLand(int land) {
		this.land = land;
	}


	public int getMission() {
		return mission;
	}
}

