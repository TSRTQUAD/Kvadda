package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;

public abstract class BaseMap {
	
	/**
	 * View that represent the map view.
	 */
	protected GoogleMapView mapView;


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
	
	
}
