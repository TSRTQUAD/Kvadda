package kvaddakopter.gui.components;

import java.util.ArrayList;

import netscape.javascript.JSObject;
import kvaddakopter.maps.MapMarkerEnum;
import kvaddakopter.maps.RouteMarker;

import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;

public abstract class GPSMarker {
	
	public LatLong coordinate;
	public Marker marker;
	
	public GPSMarker(LatLong coordinate){
		this.coordinate = coordinate;
		this.marker = RouteMarker.create(coordinate.getLatitude(), coordinate.getLongitude(), this.getIcon());
	}
	
	public MapMarkerEnum getIcon(){
		return MapMarkerEnum.NAVIGATION_NORMAL;	 
	}
	
	public double getLatitude() {
		return this.coordinate.getLatitude();
	}
	
	public double getLongitude(){
		return this.coordinate.getLongitude();
	}
	
	public Marker getMarker(){
		return this.marker;
	}
	
	public LatLong getLatLong(){
		return this.coordinate;
	}
	
	/**
	 * Add the marker to the supplied map
	 * @param map
	 * @param listOfCoordinates
	 */
	public void attachToMap(GoogleMap map, ArrayList<GPSMarker> listOfCoordinates){
		
		
		listOfCoordinates.add(this);
		map.addMarker(this.getMarker());
		map.addUIEventHandler(this.getMarker(), UIEventType.click, (JSObject obj) -> {
			this.remove(map, listOfCoordinates);
		});
		
		
	}
	
	/**
	 *  Action to take when this marker is removed.
	 * @param map
	 * @param listOfCoordinates
	 */
	public void remove(GoogleMap map,  ArrayList<GPSMarker> listOfCoordinates){
		
			this.clearFromMap(map);
			int markerIndex = listOfCoordinates.indexOf(this);
			if(markerIndex != -1){
                listOfCoordinates.remove(markerIndex);
			}
	};
	
	
	public void clearFromMap(GoogleMap map){
		map.removeMarker(this.getMarker());
	}
	
}
