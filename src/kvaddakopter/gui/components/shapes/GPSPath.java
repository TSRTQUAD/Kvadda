package kvaddakopter.gui.components.shapes;

import java.util.ArrayList;

import kvaddakopter.gui.components.AbstractGPSMarker;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

public class GPSPath implements MapShapeInterface{

	/**
	 * Polyline representation of the path  
	 */
	protected Polyline path = null;
	
	
	
	/**
	 * The Map object associated with this Marker
	 */
	protected GoogleMap map;
	
	
	
	public GPSPath(GoogleMap map) {
		this.map = map;
	}
	
	
	/**
	 * Draws a polyLine Path to the current Map.
	 * @param listOfCoordinates
	 */
	public void draw(ArrayList<AbstractGPSMarker> listOfCoordinates){
		//Clean the current Path if it exist
		this.remove();
		//If the listSize is 1 we are done. No Path can be  drawn for ONE point.
		int listsize = listOfCoordinates.size();
		//Draw the new path
		if ( listsize > 1){

			LatLong[] ary = new LatLong[listsize];
			int i = 0;
			for(AbstractGPSMarker marker : listOfCoordinates){
				ary[i] = new LatLong(marker.getLatitude(),marker.getLongitude());
				i++;
			}
			
			MVCArray mvc = new MVCArray(ary);
			PolylineOptions options = new PolylineOptions().path(mvc).strokeColor("green").strokeWeight(2);
			this.path = new Polyline(options);
			this.map.addMapShape(this.path);
		}
		
	}


	@Override
	public void remove() {
		if(path != null){
			map.removeMapShape(path);
			path = null;
		}	
	}
	
}
