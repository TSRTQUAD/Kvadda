package kvaddakopter.gui.components;

import java.util.ArrayList;

import netscape.javascript.JSObject;

import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;

public class GPSMarkerWithCircle extends GPSMarkerNormal{
	
	protected double circleRadius = 20.123123;
	protected Circle circle;
	
	public GPSMarkerWithCircle(LatLong coordinate) {
		super(coordinate);
	}
	
	public double getCircleRadius(double radius){
		return this.circleRadius;
	}
	
	
	public Circle getCircle(){
		return this.circle;
	}
	
	public void attachToMap(GoogleMap map){
		
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
        	this.circle.setCenter(this.coordinate);
        });
	}

	
	public void clearFromMap(GoogleMap map){
		map.removeMapShape(this.circle);
		super.clearFromMap(map);
	}
	
}
