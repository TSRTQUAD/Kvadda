package kvaddakopter.gui.components;

import java.util.ArrayList;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

public class GPSMarkerWithPath extends GPSMarker{
	
	
	/**
	 * ONE Polyline should be shared between all GPSMarkers
	 * therefore its static
	 */
	protected static Polyline path = null;
	
	public GPSMarkerWithPath(LatLong coordinate) {
		super(coordinate);
	}
	
	@Override
	public void attachToMap(GoogleMap map, ArrayList<GPSMarker> listOfCoordinates){

		super.attachToMap(map, listOfCoordinates);
		this.updatePath(map, listOfCoordinates);
		

	}
	

	
	public void clearFromMap(GoogleMap map){
		if(path != null){
			map.removeMapShape(path);
		}
		super.clearFromMap(map);
	}
	
	
	/**
	 *  Action to take when this marker is removed.
	 * @param map
	 * @param listOfCoordinates
	 */
	@Override
	public void remove(GoogleMap map,  ArrayList<GPSMarker> listOfCoordinates){
		super.remove(map, listOfCoordinates);
		this.updatePath(map, listOfCoordinates);
	};
	
	
	/**
	 * Updates the Trajectory path when its a change of coordinates
	 * @param map
	 * @param listOfCoordinates
	 */
	private void updatePath(GoogleMap map, ArrayList<GPSMarker> listOfCoordinates){
		int listsize = listOfCoordinates.size();
		if(path != null){
			map.removeMapShape(path);
		}
		if ( listsize > 1){

			LatLong[] ary = new LatLong[listsize];
			int i = 0;
			for(GPSMarker marker : listOfCoordinates){
				ary[i] = new LatLong(marker.getLatitude(),marker.getLongitude());
				i++;
			}

			MVCArray mvc = new MVCArray(ary);
			PolylineOptions options = new PolylineOptions().path(mvc).strokeColor("green").strokeWeight(2);
			path = new Polyline(options);
			map.addMapShape(path);
		}
	}
	
}
