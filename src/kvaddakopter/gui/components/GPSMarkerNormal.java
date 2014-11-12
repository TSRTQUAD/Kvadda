package kvaddakopter.gui.components;


import kvaddakopter.maps.MapMarkerEnum;
import com.lynden.gmapsfx.javascript.object.LatLong;


public class GPSMarkerNormal extends AbstractGPSMarker{
	

	public GPSMarkerNormal(LatLong coordinate) {
		super(coordinate);
	}
	
	
	@Override
	public MapMarkerEnum getIcon(){
		return MapMarkerEnum.NAVIGATION_NORMAL;	 
	}	
	
	



	
}
