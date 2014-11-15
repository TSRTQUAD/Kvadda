package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;

import netscape.javascript.JSObject;

import java.util.ArrayList;

import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.controllers.TabUtforController;



/**
 * Used as a high-level representation of the Google Map used while executing a mission.
 */
public class MissionMap implements MapComponentInitializedListener{


	/**
	 * View that represent the map view.
	 */
	private GoogleMapView mapViewUtfor;


	/**
	 * The object representing the Map itself
	 */
	private GoogleMap map = new GoogleMap();

	/**
	 * Owning Controller
	 */
	private TabUtforController owningController;

	/**
	 * Navigation Markers
	 */
	private ArrayList<AbstractGPSMarker> navigationCoordinates = new ArrayList<AbstractGPSMarker>();

	/**
	 * Forbidden Areas Markers
	 */
	private ArrayList<AbstractGPSMarker> forbiddenAreasCoordinates = new ArrayList<AbstractGPSMarker>();
	
	
	
	/**
	 * Is map initialized Initialized
	 */
	boolean isMapInitialized = false;
	
	/**
	 * Constructor
	 *
	 * @param mapView A valid GoogleMapView obtained from the GUI XML.
	 */
	public MissionMap(GoogleMapView mapView, TabUtforController owningController) {
		this.mapViewUtfor = mapView;
		this.owningController = owningController;
		this.mapViewUtfor.addMapInializedListener(this);
		
	}

	/**
	 * WHEN MAP IS READY THIS RUNS ONCE.
	 */
	@Override
	public void mapInitialized() {
		
		this.isMapInitialized = true;
		this.createMapWithStartLocation(58.409719, 15.622071);
	}
	
	


	/**
	 * Used to initialize the wanted map with given options.
	 *
	 * @param startLat  Map center start Latitude.
	 * @param startLong Map center start Longitude.
	 */
	private void createMapWithStartLocation(double startLat, double startLong) {
		LatLong mapStartingPosition = new LatLong(startLat, startLong);
		MapOptions mapOptions = new MapOptions();

		mapOptions.center(mapStartingPosition)
		.mapType(MapTypeIdEnum.ROADMAP)
		.overviewMapControl(false)
		.panControl(false)
		.rotateControl(false)
		.scaleControl(false)
		.streetViewControl(false)
		.zoomControl(true)
		.zoom(17);
		this.map = mapViewUtfor.createMap(mapOptions);
	}




}
