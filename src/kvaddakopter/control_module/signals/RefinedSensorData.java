package kvaddakopter.control_module.signals;

public class RefinedSensorData{
	public double Yaw;
	public double Xpos;
	public double Ypos;
	public double Height;
	public double ForVel;
	public double LatVel;
	
	// Get Set
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
	public double getLatVel() {
		return LatVel;
	}
	public void setLatVel(double latVel) {
		LatVel = latVel;
	}

	public void print(){
		System.out.format("Yaw: %.2f%n", Yaw);
		System.out.format("Xpos: %.1f%n", Xpos);
		System.out.format("Ypos: %.2f%n", Ypos);
		System.out.format("Height: %.2f%n", Height);
		System.out.format("Forward Velocity: %.2f%n", ForVel);
		System.out.format("Lateral Velocity: %.2f%n", LatVel);	
	}

	
}
