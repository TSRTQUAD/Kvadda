package kvaddakopter.gui.components.shapes;

import netscape.javascript.JSObject;

import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;

public class GPSCircle extends AbstractMapShape implements MapShapeInterface{

	public GPSCircle(GoogleMap map) {
		super(map);
	}
	

	/**
	 * Polyline representation of the path  
	 */
	protected Circle circle = null;
	
	
	@Override
	public void addCoordinate(LatLong clickedCoordinate) {
		if (this.markers.size() == 0){
			super.addCoordinate(clickedCoordinate);
		}
	}
	
	
	/**
	 * Draws a polyLine Path to the current Map.
	 * @param listOfCoordinates
	 */
	@Override
	public void draw(){
		
		//Undraw everything
		this.unDraw();
		
		//Draw Coordinates
		super.draw();
		//Draw Circle
		if (this.markers.size() == 1){

			CircleOptions cOpts = new CircleOptions()
			.center(this.markers.get(0).getLatLong())
			.radius(20.1011)
			.strokeColor("green")
			.strokeWeight(2)
			.fillColor("green")
			.fillOpacity(0.1);
			this.circle = new Circle(cOpts);
			map.addMapShape(this.circle);
			map.addUIEventHandler(this.circle, UIEventType.click, (JSObject obj) -> {
				this.circle.setEditable(!this.circle.getEditable());
				this.circle.setCenter(this.markers.get(0).getLatLong());
			});
			this.map.addMapShape(this.circle);
		}
		
	}
	
	public double getRadus(){
		return this.circle.getRadius();
	}

	@Override
	public void unDraw() {
		super.unDraw();
		if(circle != null){
			map.removeMapShape(circle);
		}	
	}
	
	@Override
	public void remove(){
		super.remove();
		this.unDraw();
		circle = null;
	}
	

}
