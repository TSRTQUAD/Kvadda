package kvaddakopter.gui.components;


import kvaddakopter.maps.MapMarkerEnum;

import com.lynden.gmapsfx.javascript.object.LatLong;


public class StartMarker extends AbstractGPSMarker{
	

	public StartMarker(LatLong coordinate) {
		super(coordinate);
	}
	
	
	
	
	@Override
	public MapMarkerEnum getIcon(){
		return MapMarkerEnum.START_MARKER;	 
	}
	
}
