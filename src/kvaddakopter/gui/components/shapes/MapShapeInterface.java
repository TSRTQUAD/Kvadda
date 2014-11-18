package kvaddakopter.gui.components.shapes;

import java.util.ArrayList;

import kvaddakopter.gui.components.AbstractGPSMarker;

public interface MapShapeInterface {
	
	/**
	 * Uses GPSMarkes in list and draws the shape on the map
	 */
	public void draw(ArrayList<AbstractGPSMarker> listOfCoordinates);
	
	
	/**
	 * Remove all traces of the shape from the map
	 */
	
	public void remove();
	
}
