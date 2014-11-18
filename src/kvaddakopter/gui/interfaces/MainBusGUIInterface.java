package kvaddakopter.gui.interfaces;

import kvaddakopter.maps.GPSCoordinate;

public interface MainBusGUIInterface {
	
	
	/**
	 * Current speed as a double 
	 * eg. 5 km/h
	 * @return
	 */
	public double getCurrentSpeed();
	
	
	/**
	 * Quads current gps position.
	 * @return
	 */
	public GPSCoordinate getCurrentQuadPosition();

}
