package kvaddakopter.gui.components;

import kvaddakopter.maps.MapMarkerEnum;

import com.lynden.gmapsfx.javascript.object.LatLong;

public class GPSMarkerForbidden extends GPSMarkerNormal {
	
	
	public GPSMarkerForbidden(LatLong coordinate) {
		super(coordinate);
		
	}

	public MapMarkerEnum getIcon(){
		return MapMarkerEnum.FORBIDDEN_AREAS;	 
	}

}
