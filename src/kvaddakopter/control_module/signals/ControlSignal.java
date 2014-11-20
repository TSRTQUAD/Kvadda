package kvaddakopter.control_module.signals;

public class ControlSignal{
	
	
	protected double yawrate;
	protected double heightvelocity;
	protected double forwardvelocity;
	protected double lateralvelocity;
	protected int start;
	protected int land;
	
	
	
	public ControlSignal(){
		forwardvelocity = 0;
		heightvelocity = 0;
		lateralvelocity = 0;
		yawrate = 0;
		start = 0;
		land = 0;
	}
	
	public void print(){
		System.out.format("yawrate: %.2f%n", yawrate);
		System.out.format("Heightvelocity: %.1f%n", heightvelocity);
		System.out.format("Forwardvelocity: %.2f%n", forwardvelocity);
		System.out.format("Lateralvelocity: %.2f%n", lateralvelocity);
		System.out.format("Start: %d%n", start);
		System.out.format("Land: %d%n", land);		
	}

	public double getYawrate() {
		return yawrate;
	}

	public void setYawrate(double yawrate) {
		this.yawrate = yawrate;
	}

	public double getHeightvelocity() {
		return heightvelocity;
	}

	public void setHeightvelocity(double heightvelocity) {
		this.heightvelocity = heightvelocity;
	}

	public double getForwardvelocity() {
		return forwardvelocity;
	}

	public void setForwardvelocity(double forwardvelocity) {
		this.forwardvelocity = forwardvelocity;
	}

	public double getLateralvelocity() {
		return lateralvelocity;
	}

	public void setLateralvelocity(double lateralvelocity) {
		this.lateralvelocity = lateralvelocity;
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

	
	
}

