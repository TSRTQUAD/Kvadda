package kvaddakopter.gui.components;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;

public class GPSMarker {
	
	public LatLong coordinate;
	public Marker marker;
	
	
	public GPSMarker(LatLong coordinate, Marker marker){
		this.coordinate = coordinate;
		this.marker = marker;
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
}
