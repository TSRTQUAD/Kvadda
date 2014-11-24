package kvaddakopter.gui.components.shapes;

import java.util.ArrayList;

import netscape.javascript.JSObject;

import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;

import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.components.GPSMarkerNormal;

public abstract class AbstractMapShape {
	
	
	/**
	 * Any type of marker
	 */
	protected ArrayList<AbstractGPSMarker> markers = new ArrayList<>();
	
	/**
	 * Google map object
	 */
	protected GoogleMap map;
	
	
	/**
	 * Construct
	 * @param map
	 */
	public AbstractMapShape(GoogleMap map){
		this.map = map;
	}

	
	/**
	 * Add coordinate to shape
	 * @param clickedCoordinate
	 */
	public void addCoordinate(LatLong clickedCoordinate) {
		AbstractGPSMarker newMarker =    this.usedMarkerType(clickedCoordinate);//new GPSMarkerNormal(clickedCoordinate);
		this.markers.add(newMarker);
		this.draw();
		this.map.addUIEventHandler(newMarker.getMarker(), UIEventType.click, (JSObject obj2) -> {
			newMarker.clearFromMap(this.map);
			this.removeMarkerFromList(newMarker);
			this.draw();
		});
	}
	
	public void addCoordinateWithoutDrawing(LatLong clickedCoordinate){
		AbstractGPSMarker newMarker =    this.usedMarkerType(clickedCoordinate);//new GPSMarkerNormal(clickedCoordinate);
		this.markers.add(newMarker);
	}
	
	/**
	 * Return all markers associated with this shape
	 * @return
	 */
	public ArrayList<AbstractGPSMarker> getMarkers(){
		return this.markers;
	}
	
	/**
	 * Use to marker in the shape
	 * @param clickedCoordinate
	 * @return
	 */
	protected AbstractGPSMarker usedMarkerType(LatLong clickedCoordinate){
		return new GPSMarkerNormal(clickedCoordinate);
	}
	
	public void draw(){
		//First start from scratch
		for(AbstractGPSMarker marker : this.markers){
			//Draw markers
			marker.attachToMap(this.map);
			this.map.setZoom(this.map.getZoom() - 1);
			this.map.setZoom(this.map.getZoom() + 1);
		}	
	}
	
	public void unDraw(){
		
		for(AbstractGPSMarker marker : this.markers){
			marker.clearFromMap(this.map);
			this.map.setZoom(this.map.getZoom() - 1);
			this.map.setZoom(this.map.getZoom() + 1);
		}	
	}
	
	
	
	/**
	 * Removes this marker from the supplied list.
	 * @param listOfCoordinates
	 */
	public void removeMarkerFromList(AbstractGPSMarker marker){
		int markerIndex = this.markers.indexOf(marker);
		System.out.println(markerIndex);
		if(markerIndex != -1){
            this.markers.remove(markerIndex);
		}
	}
	
	
	public void remove(){
		this.unDraw();
		this.markers.clear();
	}
}
