package kvaddakopter.gui.components.shapes;

import java.util.ArrayList;

import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.components.GPSMarkerNormal;
import netscape.javascript.JSObject;

import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;


/**
 * Used to represent a map shape. This should be extended by a super class before it could be used.
 */
public abstract class AbstractMapShape {
	
	
	/**
	 * Any type of marker can be used.
	 */
	protected ArrayList<AbstractGPSMarker> markers = new ArrayList<>();
	
	/**
	 * Object to represent the Google map.
	 */
	protected GoogleMap map;
	
	
	/**
	 * Assigns a Google map when created.
	 * @param map
	 */
	public AbstractMapShape(GoogleMap map){
		this.map = map;
	}

	
	/**
	 * Add coordinate to shape. Also re-draws the entire shape
	 * @param clickedCoordinate The LatLong where a coordinate should be added to the map shape.
	 */
	public void addCoordinate(LatLong clickedCoordinate) {
		AbstractGPSMarker newMarker =    this.usedMarkerType(clickedCoordinate);
		this.markers.add(newMarker);
		this.draw();
		this.map.addUIEventHandler(newMarker.getMarker(), UIEventType.click, (JSObject obj2) -> {
			newMarker.clearFromMap(this.map);
			this.removeMarkerFromList(newMarker);
			this.draw();
		});
	}
	
	/**
	 * Add coordinate to shape. No re-drawing occurs (used for adding tons of coordinates at once).
	 * @param clickedCoordinate The LatLong where a coordinate should be added to the map shape.
	 */
	public void addCoordinateWithoutDrawing(LatLong clickedCoordinate){
		AbstractGPSMarker newMarker =    this.usedMarkerType(clickedCoordinate);//new GPSMarkerNormal(clickedCoordinate);
		this.markers.add(newMarker);
	}
	
	/**
	 * Return all markers associated with this shape
	 * @return Array of GPSmarkers
	 */
	public ArrayList<AbstractGPSMarker> getMarkers(){
		return this.markers;
	}
	
	/**
	 * The coordinate marker used in the map shape.
	 * @param clickedCoordinate The LatLong for the added Marker.
	 * @return GPSMarker
	 */
	protected AbstractGPSMarker usedMarkerType(LatLong clickedCoordinate){
		return new GPSMarkerNormal(clickedCoordinate);
	}
	
	
	/**
	 * Draws the map shape to the Google Map.
	 */
	public void draw(){
		//First start from scratch
		for(AbstractGPSMarker marker : this.markers){
			//Draw markers
			marker.attachToMap(this.map);
			this.map.setZoom(this.map.getZoom() - 1);
			this.map.setZoom(this.map.getZoom() + 1);
		}	
	}
	
	
	/**
	 * Removes the map shape from the Google map.
	 */
	public void unDraw(){
		
		for(AbstractGPSMarker marker : this.markers){
			marker.clearFromMap(this.map);
			this.map.setZoom(this.map.getZoom() - 1);
			this.map.setZoom(this.map.getZoom() + 1);
		}	
	}
	
	
	
	/**
	 * Removes the marker from the internal list.
	 * @param marker The marker that should be removed.
	 */
	public void removeMarkerFromList(AbstractGPSMarker marker){
		int markerIndex = this.markers.indexOf(marker);
		System.out.println(markerIndex);
		if(markerIndex != -1){
            this.markers.remove(markerIndex);
		}
	}
	
	/**
	 * Removes the map shape and remove all coordinates associated with this shape.
	 */
	public void remove(){
		this.unDraw();
		this.markers.clear();
	}
	
	abstract public boolean isValid();
}
