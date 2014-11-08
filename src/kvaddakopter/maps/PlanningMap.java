package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;

import netscape.javascript.JSObject;


import java.util.ArrayList;
import kvaddakopter.gui.controllers.TabPlaneraController;



/**
 * Used as a high-level representation of the Google Map used for planning.
 */
public class PlanningMap implements MapComponentInitializedListener {


	/**
	 * View that represent the map view.
	 */
	private GoogleMapView mapView;


	/**
	 * The object representing the Map itself
	 */
	private GoogleMap map = new GoogleMap();

	/**
	 * Owning Controller
	 */
	private TabPlaneraController owningController;

	/**
	 * Navigation Markers
	 */
	private ArrayList<GPSCoordinate> navigationCoordinates = new ArrayList<GPSCoordinate>();


	/**
	 * Forbidden Areas Markers
	 */
	private ArrayList<GPSCoordinate> forbiddenAreasCoordinates = new ArrayList<GPSCoordinate>();


	/**
	 * Constructor
	 *
	 * @param mapView A valid GoogleMapView obtained from the GUI XML.
	 */
	public PlanningMap(GoogleMapView mapView, TabPlaneraController owningController) {
		this.mapView = mapView;
		this.owningController = owningController;
		this.mapView.addMapInializedListener(this);
	}


	/**
	 * WHEN MAP IS READY THIS RUNS ONCE.
	 */
	@Override
	public void mapInitialized() {

		this.createMapWithStartLocation(58.409719, 15.622071);

		this.addMapEventListeners();

	}


	/**
	 * Sets all event listeners for the map.
	 */
	private void addMapEventListeners() {

		//EVENT FOR USER CLICKED MAP
		this.map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
			
			LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
			GPSCoordinate coord = new GPSCoordinate(ll.getLatitude(), ll.getLongitude());

			if ( this.owningController.possibleToAddMissionCoordinates() ){
				this.addNavigationPoint(coord, MapMarkerEnum.NAVIGATION_NORMAL, this.navigationCoordinates);
			}
			if ( this.owningController.possibleToAddForbinnenAreaCoordinates() ){
				this.addNavigationPoint(coord, MapMarkerEnum.FORBIDDEN_AREAS, this.forbiddenAreasCoordinates);
			}

		});
	}



	/**
	 * Add a navigation point to the given GPS Coordinate.
	 *
	 * @param coordinate
	 */
	public void addNavigationPoint(GPSCoordinate coordinate, MapMarkerEnum iconType, ArrayList<GPSCoordinate> list) {
		Marker marker = RouteMarker.create(coordinate.getLatitude(), coordinate.getLongitude(), iconType);
		list.add(coordinate);
		map.addMarker(marker);
	}



	/**
	 * Returns an array of all placed markers
	 * @return
	 */
	public ArrayList<GPSCoordinate> allNavigationCoordinates() {
		return this.navigationCoordinates;
	}



	/**
	 * Returns an array of all placed markers
	 * @return
	 */
	public ArrayList<GPSCoordinate> allForbiddenAreaCoordinates() {
		return this.forbiddenAreasCoordinates;
	}


	/**
	 * Used to initialize the wanted map with given options.
	 *
	 * @param startLat  Map center start Latitude.
	 * @param startLong Map center start Longitude.
	 * @return GoogleMap instance
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
		this.map = mapView.createMap(mapOptions);
	}



}
