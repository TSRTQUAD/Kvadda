package kvaddakopter.gui.components.shapes;

import java.util.HashMap;
import java.util.Map.Entry;

import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.components.TargetMarker;
import kvaddakopter.maps.GPSCoordinate;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;

public class TargetMap extends AbstractMapShape implements MapShapeInterface {
	
	
	protected String currentTitle;
	
	public TargetMap(GoogleMap map) {
		super(map);
	}
	
	
	public void addListOfTargets(HashMap<String, GPSCoordinate> newTargetList){
		
		this.remove();

		for(Entry<String, GPSCoordinate> entry : newTargetList.entrySet()) {
		    currentTitle = entry.getKey();
		    GPSCoordinate coordinate = entry.getValue();
		    this.addCoordinateWithoutDrawing(new LatLong(coordinate.getLatitude(), coordinate.getLongitude()));
		}
		this.draw();
	}
	
	@Override
	protected AbstractGPSMarker usedMarkerType(LatLong clickedCoordinate){
		TargetMarker marker = new TargetMarker(clickedCoordinate);
		marker.getMarker().setTitle(currentTitle);
		return marker;
	}	
	
	
	@Override
	public boolean isValid(){
		return this.markers.size() >= 1;
	}

}
