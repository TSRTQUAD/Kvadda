package kvaddakopter.gui.components.shapes;

import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.components.GPSMarkerForbidden;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.PolygonOptions;

public class RedGPSPolygon extends GPSPolygon{
	
	public RedGPSPolygon(GoogleMap map) {
		super(map);
	}

	@Override
	protected PolygonOptions getOptions(MVCArray mvc){
		return new PolygonOptions()
			.paths(mvc)
			.strokeColor("red")
			.strokeWeight(2)
			.editable(false)
			.fillColor("red")
			.fillOpacity(0.3);
	}
	
	
	@Override
	protected AbstractGPSMarker usedMarkerType(LatLong clickedCoordinate){
		return new GPSMarkerForbidden(clickedCoordinate);
	}
}
