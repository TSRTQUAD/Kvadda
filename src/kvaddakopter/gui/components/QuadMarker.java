package kvaddakopter.gui.components;


import kvaddakopter.maps.MapMarkerEnum;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;

public class QuadMarker extends AbstractGPSMarker {

	public QuadMarker(LatLong coordinate) {
		super(coordinate);
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
	 * Update the markers position
	 * @param latlng
	 */
	public void updatePosition(LatLong latlng){
		this.setLatLong(latlng);
	}
	
	@Override
	public MapMarkerEnum getIcon() {
		
		return MapMarkerEnum.QUAD_MARKER;
	}
	
}
