package kvaddakopter.gui.interfaces;

public interface MainBusGUIInterface {
	
	/**
	 * Quads current latitude Position
	 * eg. 50.123123123
	 * @return
	 */
	public double getCurrentQuadLatitudePosition();
	/**
	 * Quads current longitude Position
	 * eg. 49.122311232
	 * @return
	 */
	public double getCurrentQuadLongitudePosition();
	
	/**
	 * Current speed as a double 
	 * eg. 5 km/h
	 * @return
	 */
	public double getCurrentSpeed();
}
