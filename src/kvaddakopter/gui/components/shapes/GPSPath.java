package kvaddakopter.gui.components.shapes;


import kvaddakopter.gui.components.AbstractGPSMarker;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

public class GPSPath extends AbstractMapShape implements MapShapeInterface{

	/**
	 * Polyline representation of the path  
	 */
	protected Polyline path = null;
	
	public GPSPath(GoogleMap map) {
		super(map);
	}
	
	/**
	 * Draws a polyLine Path to the current Map.
	 * @param listOfCoordinates
	 */
	public void draw(){
		
		//Undraw everything
		this.unDraw();
		
		//Draw Coordinates
		super.draw();
		//Draw Trajectory
		int listsize = this.markers.size();
		if ( listsize > 1){
			LatLong[] ary = new LatLong[listsize];
			int i = 0;
			for(AbstractGPSMarker marker : this.markers){
				ary[i] = new LatLong(marker.getLatitude(),marker.getLongitude());
				i++;
			}
			MVCArray mvc = new MVCArray(ary);
			PolylineOptions options = new PolylineOptions().path(mvc).strokeColor("green").strokeWeight(2);
			this.path = new Polyline(options);
			//Draw the trajectory
			this.map.addMapShape(this.path);
		}
		
	}

	@Override
	public void unDraw() {
		super.unDraw();
		if(path != null){
			map.removeMapShape(path);
		}	
	}
	
	@Override
	public void remove(){
		super.remove();
		this.unDraw();
		path = null;
	}
	
	
	
}
