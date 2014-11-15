package kvaddakopter.control_module.signals;
public class ReferenceData {
	public double Yaw;
	public double Latitud;
	public double Longitud;
	public double Xpos;
	public double Ypos;
	public double Height;
	public double ForVel;
	public double time;
	public boolean Mission;
	long lStartTime, lEndTime, difference;
	private boolean running;
	public double initiallat;
	public double initiallon;
	public double radius;
	
	
	
	// Initialize coordinate system XY with origo in Initiallat and initiallon
	public ReferenceData(double inlat, double inlong){		
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
	
	
	public void refine(RefinedSensorData rsdata){
		//Mission = FALSE
		//Update reference data if close enough and reference time at coordinate is reached.
		if (Math.abs(rsdata.getHeight()-Height)<1 && 
			Math.abs(rsdata.getXpos()-Xpos)<0.3   && 
			Math.abs(rsdata.getYpos()-Ypos)<0.3   &&
			!Mission										){
			
			
			if (!running){
				lStartTime= System.currentTimeMillis();
				this.running = true;
			}
			
			if (running && Math.abs(System.currentTimeMillis() - lStartTime) > time){
			this.running = false;
			
			// Update reference data
			// Update reference data
			// Update reference data			
			}
		}	
		// Mission = TRUE
		//Update Yaw every iteration and Reference data if close enough
		if (Math.abs(rsdata.getHeight()-Height)<1 && 
				Math.abs(rsdata.getXpos()-Xpos)<0.3 && 
				Math.abs(rsdata.getYpos()-Ypos)<0.3 &&
				Mission){
		
			// Update reference data
			// Update reference data
			// Update reference data
		}		
		if (Mission){
		Yaw = ( (Yaw - rsdata.getYaw()) > 0) ?
				Yaw - Math.atan((rsdata.getXpos() - Xpos)/(rsdata.getYpos() - Ypos)):
				Yaw + Math.atan((rsdata.getXpos() - Xpos)/(rsdata.getYpos() - Ypos));
		}
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
}

