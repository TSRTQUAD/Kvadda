package kvaddakopter.gui.components.shapes;

import com.lynden.gmapsfx.javascript.object.LatLong;


public interface MapShapeInterface {
	
	
	/**
	 * Uses GPSMarkes in list and draws the shape on the map
	 */
	public void draw();

	
	/**
	 *  Add coordinate to Path
	 */
	public void addCoordinate(LatLong clickedCoordinate);

	
	/**
	 * Remove all traces of the shape from the map
	 */
	
	public void remove();
	
}
