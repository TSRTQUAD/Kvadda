package kvaddakopter.gui.components;


import kvaddakopter.maps.MapMarkerEnum;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class TargetMarker extends AbstractGPSMarker {

	public TargetMarker(LatLong coordinate) {
		super(coordinate);
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
		return MapMarkerEnum.TARGET_MARKER;
	}
	
}
