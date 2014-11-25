package kvaddakopter.gui.components.shapes;


import kvaddakopter.gui.components.AbstractGPSMarker;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.Polygon;
import com.lynden.gmapsfx.shapes.PolygonOptions;

public abstract class GPSPolygon extends AbstractMapShape implements MapShapeInterface{
	
	
	
	
	/**
	 * The Google maps polygon 
	 */
	protected Polygon polygon;
	
	
	/**
	* Creates a relation to a Google Map
	 */
	public GPSPolygon(GoogleMap map) {
		super(map);
	}
	

	/**
	 * Returns a Polygon option object used to create the polygon
	 * @param mvc Google Maps Mvc array used to create the polygon.
	 * @return A options object
	 */
	abstract protected PolygonOptions getOptions(MVCArray mvc);
	
	
	@Override
	public void draw() {

		//Undraw everything
		this.unDraw();
		
		//Draw Coordinates
		super.draw();
		//If the listSize is 1 we are done. No Path can be  drawn for ONE point.
		int listsize = this.markers.size();
		//Draw the new path
		if ( listsize > 2){
			LatLong[] ary = new LatLong[listsize];
			int i = 0;
			for(AbstractGPSMarker marker : this.markers){
				//ary[i] = marker.getLatLong();
				ary[i] = new LatLong(marker.getLatitude(),marker.getLongitude());
				i++;
			}
			MVCArray mvc = new MVCArray(ary);

			PolygonOptions polygOpts = this.getOptions(mvc);
			this.polygon = new Polygon(polygOpts);
			this.map.addMapShape(this.polygon);
		} 
	}
	
	

	
	@Override
	public void unDraw() {
		//Undraw Coordinates
		super.unDraw();
		//Undraw shape
		if(polygon != null){
			map.removeMapShape(polygon);
		}	
	}
	
	@Override
	public void remove(){
		this.unDraw();
		super.remove();
		polygon = null;
	}
	
	
}
