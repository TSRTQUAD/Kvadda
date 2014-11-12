package kvaddakopter.gui.components;

import java.util.ArrayList;

import netscape.javascript.JSObject;

import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;

public class GPSMarkerWithCircle extends GPSMarkerNormal{
	
	protected int circleRadius = 20;
	protected Circle circle;
	
	public GPSMarkerWithCircle(LatLong coordinate) {
		super(coordinate);
	}
	
	public void setCircleRadius(int radius){
		this.circleRadius = radius;
	}
	
	@Override
	public void attachToMap(GoogleMap map, ArrayList<AbstractGPSMarker> listOfCoordinates){
		
		super.attachToMap(map, listOfCoordinates);

		CircleOptions cOpts = new CircleOptions()
		.center(coordinate)
		.radius(this.circleRadius)
		.strokeColor("green")
		.strokeWeight(2)
		.fillColor("green")
		.fillOpacity(0.1);
		this.circle = new Circle(cOpts);
        map.addMapShape(this.circle);
        map.addUIEventHandler(this.circle, UIEventType.click, (JSObject obj) -> {
        	this.circle.setEditable(!this.circle.getEditable());
        });
	}

	
	public void clearFromMap(GoogleMap map){
		map.removeMapShape(this.circle);
		super.clearFromMap(map);
	}
	

}
