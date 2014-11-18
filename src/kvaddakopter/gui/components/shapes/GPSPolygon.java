package kvaddakopter.gui.components.shapes;

import java.util.ArrayList;

import kvaddakopter.gui.components.AbstractGPSMarker;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.Polygon;
import com.lynden.gmapsfx.shapes.PolygonOptions;

public abstract class GPSPolygon implements MapShapeInterface{
	
	
	/**
	 * The Map object associated with this Marker
	 */
	protected GoogleMap map;
	
	
	/**
	 * The Gmaps polygon 
	 */
	protected Polygon polygon;
	
	
	public GPSPolygon(GoogleMap map) {
		this.map = map;
	}



	@Override
	public void draw(ArrayList<AbstractGPSMarker> listOfCoordinates) {
		//Clean the current polygon if it exist
		this.remove();
		//If the listSize is 1 we are done. No Path can be  drawn for ONE point.
		int listsize = listOfCoordinates.size();
		//Draw the new path
		if ( listsize > 2){

			LatLong[] ary = new LatLong[listsize];
			int i = 0;
			for(AbstractGPSMarker marker : listOfCoordinates){
				ary[i] = new LatLong(marker.getLatitude(),marker.getLongitude());
				i++;
			}

			MVCArray mvc = new MVCArray(ary);
			PolygonOptions polygOpts = this.getOptions(mvc);
			this.polygon = new Polygon(polygOpts);
			map.addMapShape(this.polygon);
		}

	}
	
	
	/**
	 * Returns a Polygon option object used to create the polygon
	 * @param mvc
	 * @return
	 */
	abstract protected PolygonOptions getOptions(MVCArray mvc);
	


	@Override
	public void remove() {
		if(polygon != null){
			map.removeMapShape(polygon);
			polygon = null;
		}	
		
	}
	
	
	
}
