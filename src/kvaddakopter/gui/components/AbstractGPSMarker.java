package kvaddakopter.gui.components;

import java.util.ArrayList;

import netscape.javascript.JSObject;
import kvaddakopter.maps.MapMarkerEnum;
import kvaddakopter.maps.RouteMarker;

import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;

public abstract class AbstractGPSMarker {
	
	protected LatLong coordinate;
	protected Marker marker;

	public AbstractGPSMarker(LatLong coordinate){
		this.coordinate = coordinate;
		this.marker = RouteMarker.create(coordinate.getLatitude(), coordinate.getLongitude(), this.getIcon());
	}
	
	public abstract MapMarkerEnum getIcon();
	
	public double getLatitude() {
		return this.coordinate.getLatitude();
	}
	
	public double getLongitude(){
		return this.coordinate.getLongitude();
	}
	
	public void setLatLong(LatLong latlng){
		this.coordinate = latlng;
		this.marker.setPosition(latlng);
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
	public void attachToMap(GoogleMap map){
		
		map.addMarker(this.getMarker());
		
	}
	
	
	/**
	 * Clear  marker from map.
	 * @param map
	 */
	public void clearFromMap(GoogleMap map){
		map.removeMarker(this.getMarker());
	}	
	
	
	
}
