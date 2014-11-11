package kvaddakopter.control_module.signals;
public class ReferenceData {
	public double Yaw;
	public double LateralPosition;
	public double LongtudePosition;
	public double Xpos;
	public double Ypos;
	public double Height;
	public double ForVel;
	public double time;
	public boolean Mission;
	long lStartTime, lEndTime, difference;
	private boolean running;
	
	
	
	
	public void refine(RefinedSensorData rsdata){
		

		//Mission = FALSE
		//Update reference data if close enough and reference time at coordinate is reached.
		if (Math.abs(rsdata.getHeight()-Height)<1 && 
			Math.abs(rsdata.getXpos()-Xpos)<0.3   && 
			Math.abs(rsdata.getYpos()-Ypos)<0.3   &&
			!Mission										){
			
			
			if (!running){
				lStartTime= System.currentTimeMillis();
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
	public double getLateralPosition() {
		return LateralPosition;
	}
	public void setLateralPosition(double lateralPosition) {
		LateralPosition = lateralPosition;
	}
	public double getLongtudePosition() {
		return LongtudePosition;
	}
	public void setLongtudePosition(double longtudePosition) {
		LongtudePosition = longtudePosition;
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

