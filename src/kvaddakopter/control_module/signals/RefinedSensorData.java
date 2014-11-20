package kvaddakopter.control_module.signals;



// This class is contains all estimated states. 

// Xpos is the estimated position in XY coordinate system
// Ypos is the estimated position in XY coordinate system
// ForVel is the estimated velocity in xy coordinate system (Forward velocity for platform)
// LatVel is the estimated velocity in xy coordinate system (Lateral velocity for platform)
// Height is the estimated height for platform


public class RefinedSensorData{
	public double yaw;
	public double Xpos;
	public double Ypos;
	public double Height;
	public double ForVel;
	public double LatVel;
	public double Xdot;
	public double Ydot;
	
	public void s2rs(SensorData sdata){
		this.yaw = sdata.getYaw();
		this.Height = sdata.getHeight();		
	}
	
	
	public void XYdot2Vel(){
		LatVel =    Math.cos(yaw)*Xdot + Math.sin(yaw)*Ydot;
		ForVel =  - Math.sin(yaw)*Xdot + Math.cos(yaw)*Ydot;
	}
	
	
	// set X,xdot (new states from kalman)
	public void setXstates(double[] xstates) {
		Xpos = xstates[0];
		Xdot = xstates[1];
	}
	// set Y,ydot (new states from kalman)
	public void setYstates(double[] ystates) {
		Ypos = ystates[0];
		Ydot = ystates[1];
	}
	
	public void print(){
		System.out.format("yaw: %.2f%n", yaw*180/Math.PI);
		System.out.format("Xpos: %.1f%n", Xpos);
		System.out.format("Ypos: %.2f%n", Ypos);
		System.out.format("Height: %.2f%n", Height);
		System.out.format("Forward Velocity: %.2f%n", ForVel);
		System.out.format("Lateral Velocity: %.2f%n", LatVel);
		System.out.format("Ydot: %.2f%n", Ydot);
		System.out.format("Xdot: %.2f%n", Xdot);
		
	}
	
	public double getYaw() {
		return yaw;
	}
	public void setYaw(double yaw2) {
		yaw = yaw2;
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
	public double getLatVel() {
		return LatVel;
	}
	public void setLatVel(double latVel) {
		LatVel = latVel;
	}



	
}
