package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;


public abstract class BaseMap {
	
	/**
	 * View that represent the map view.
	 */
	protected GoogleMapView mapView;

	
	protected Polyline generatedPath;
	
	/**
	 * The object representing the Map itself
	 */
	protected GoogleMap map = new GoogleMap();
	/**
	 * Used to initialize the wanted map with given options.
	 *
	 * @param startLat  Map center start Latitude.
	 * @param startLong Map center start Longitude.
	 * @return GoogleMap instance
	 */
	
	/**
	 * Where should we start the mapView. The world is yours!
	 * @return
	 */
	protected abstract LatLong startCoordinate();
	
	
	protected void createMapWithStartLocation() {
		
		MapOptions mapOptions = new MapOptions();
		mapOptions.center(this.startCoordinate())
		.mapType(MapTypeIdEnum.ROADMAP)
		.overviewMapControl(false)
		.panControl(false)
		.rotateControl(false)
		.scaleControl(false)
		.streetViewControl(false)
		.zoomControl(true)
		.zoom(19);
		this.map = mapView.createMap(mapOptions);
	}
	
	
	public void drawResultingTrajectory(double[][] trajectory) {

		if (this.generatedPath != null){
			this.map.removeMapShape(this.generatedPath);
		}
		System.out.println("Drawing coordinates on map...");
		LatLong[] ary = new LatLong[trajectory.length];
		for(int i = 0; i < trajectory.length; i++){
			ary[i] = new LatLong(trajectory[i][0], trajectory[i][1]);
		}
		MVCArray mvc = new MVCArray(ary);
		PolylineOptions options = new PolylineOptions().path(mvc).strokeColor("blue").strokeWeight(3);
		this.generatedPath = new Polyline(options);
		//Draw the trajectory
		this.map.addMapShape(this.generatedPath);
		
	}
}
