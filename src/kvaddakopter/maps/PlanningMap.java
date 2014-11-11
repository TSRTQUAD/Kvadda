package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;


import netscape.javascript.JSObject;


import java.util.ArrayList;


import kvaddakopter.assignment_planer.Area;
import kvaddakopter.assignment_planer.MissionType;
import kvaddakopter.gui.components.GPSMarker;
import kvaddakopter.gui.components.GPSMarkerForbidden;
import kvaddakopter.gui.components.GPSMarkerWithCircle;
import kvaddakopter.gui.components.GPSMarkerWithPath;
import kvaddakopter.gui.components.GpsToAreaTransformer;
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
	private ArrayList<GPSMarker> navigationCoordinates = new ArrayList<GPSMarker>();

	/**
	 * Forbidden Areas Markers
	 */
	private ArrayList<GPSMarker> forbiddenAreasCoordinates = new ArrayList<GPSMarker>();

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
	 * Clear all navigation markers on the map.
	 */
	public void clearNavigationCoordinates(){
		for(GPSMarker marker : this.navigationCoordinates){
			marker.clearFromMap(map);
		}
		this.navigationCoordinates.clear();
	}
	
	/**
	 * Clear all Forbidden Area markers on the map.
	 */
	public void clearForbiddenAreasCoordinates(){
		for(GPSMarker marker : this.forbiddenAreasCoordinates){
			marker.clearFromMap(map);
		}
		this.forbiddenAreasCoordinates.clear();
	}
	
	/**
	 * Clears both forbidden areas and navigation coordiantes
	 */
	public void clearMap(){
		this.clearForbiddenAreasCoordinates();
		this.clearNavigationCoordinates();
	}
	

	/**
	 * Sets all event listeners for the map.
	 */
	private void addMapEventListeners() {

		//EVENT FOR USER CLICKED MAP
		this.map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
			
			//Coordinate where the user clicked.
			LatLong coordinate = new LatLong((JSObject) obj.getMember("latLng"));
			
			if ( this.owningController.addMissionCoordinatesMode() ){
				
				// 3 cases
				MissionType missionType = this.owningController.getCurrentSelectedMissionType();

	
				if (missionType == MissionType.AROUND_COORDINATE && this.navigationCoordinates.size() < 1){
					
                        GPSMarkerWithCircle mapMarker =  new GPSMarkerWithCircle(coordinate);
                        mapMarker.attachToMap(map, this.navigationCoordinates);
				}
				if (missionType == MissionType.ALONG_TRAJECTORY ){
                        GPSMarkerWithPath mapMarker = new GPSMarkerWithPath(coordinate);
                        mapMarker.attachToMap(map, this.navigationCoordinates);
				
				}

			}
			if ( this.owningController.addForbiddenAreasMode() ){
                        //this.addGpsPoint(coordinate, MapMarkerEnum.FORBIDDEN_AREAS, this.forbiddenAreasCoordinates);
                        GPSMarkerForbidden mapMarker =  new GPSMarkerForbidden(coordinate);
                        mapMarker.attachToMap(map, this.forbiddenAreasCoordinates);
			}

		});
	}





	/**
	 * Returns an array of all placed markers
	 * @return
	 */
	public ArrayList<Area> allNavigationCoordinates() {
		return GpsToAreaTransformer.transform(this.navigationCoordinates);
		
	}



	/**
	 * Returns an array of all placed markers
	 * @return
	 */
	public ArrayList<Area> allForbiddenAreaCoordinates() {
		return GpsToAreaTransformer.transform( this.forbiddenAreasCoordinates );
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
