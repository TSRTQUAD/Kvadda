package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;

import netscape.javascript.JSObject;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.Area;
import kvaddakopter.assignment_planer.MissionType;
import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.components.GPSMarkerNormal;
import kvaddakopter.gui.components.GPSMarkerForbidden;
import kvaddakopter.gui.components.GPSMarkerWithCircle;
import kvaddakopter.gui.components.GpsToAreaTransformer;
import kvaddakopter.gui.components.shapes.GPSPath;
import kvaddakopter.gui.components.shapes.GreenGPSPolygon;
import kvaddakopter.gui.components.shapes.RedGPSPolygon;
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
	private ArrayList<AbstractGPSMarker> navigationCoordinates = new ArrayList<AbstractGPSMarker>();

	/**
	 * Forbidden Areas Markers
	 */
	private ArrayList<AbstractGPSMarker> forbiddenAreasCoordinates = new ArrayList<AbstractGPSMarker>();
	
	/**
	 * GPS paths 
	 */
	private GPSPath gpsPath = null;
	
	
	/**
	 * GPS Polygons
	 */
	private GreenGPSPolygon missionAreaGpsPolygon = null;
	private RedGPSPolygon   forbiddenAreaGpsPolygon = null;
	
	
	
	/**
	 * Is map initialized Initialized
	 */
	boolean isMapInitialized = false;
	
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
		
		this.isMapInitialized = true;
		this.createMapWithStartLocation(58.409719, 15.622071);
		this.gpsPath = new GPSPath(this.map);
		this.missionAreaGpsPolygon = new GreenGPSPolygon(this.map);
		this.forbiddenAreaGpsPolygon = new RedGPSPolygon(this.map);
		this.addMapEventListeners();
		this.clearMap();

	}
	
	/**
	 * Clear all navigation markers on the map.
	 */
	public void clearNavigationCoordinates(){
		if (isMapInitialized){
			for(AbstractGPSMarker marker : this.navigationCoordinates){
				marker.clearFromMap(map);
			}
			this.gpsPath.remove();
			this.missionAreaGpsPolygon.remove();
			this.navigationCoordinates.clear();
		}
	}
	
	/**
	 * Clear all Forbidden Area markers on the map.
	 */
	public void clearForbiddenAreasCoordinates(){
		if (isMapInitialized){
			for(AbstractGPSMarker marker : this.forbiddenAreasCoordinates){
				marker.clearFromMap(map);
			}
			this.forbiddenAreaGpsPolygon.remove();
			this.forbiddenAreasCoordinates.clear();
		}
	}
	
	/**
	 * Clears both forbidden areas and navigation coordiantes
	 */
	public void clearMap(){
		
		if (isMapInitialized){
			this.clearForbiddenAreasCoordinates();
			this.clearNavigationCoordinates();
		}
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
	 * Sets all event listeners for the map.
	 */
	private void addMapEventListeners() {
		//EVENT FOR USER CLICKED MAP
		this.map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
			
			//Coordinate where the user clicked.
			LatLong clickedCoordinate = new LatLong((JSObject) obj.getMember("latLng"));
			MissionType missionType = this.owningController.getCurrentSelectedMissionType();

			if ( this.owningController.addMissionCoordinatesMode() ){
				
				// 3 cases for each mission type
				if (missionType == MissionType.AROUND_COORDINATE && this.navigationCoordinates.size() < 1){
					
                        GPSMarkerWithCircle mapMarkerCircle =  new GPSMarkerWithCircle(clickedCoordinate);
                        mapMarkerCircle.attachToMap(this.map, this.navigationCoordinates);
				}
				else if (missionType == MissionType.ALONG_TRAJECTORY ){
					GPSMarkerNormal normalMarker = new GPSMarkerNormal(clickedCoordinate);
					normalMarker.attachToMap(map, this.navigationCoordinates);
					this.gpsPath.draw(this.navigationCoordinates);
				}
				else if (missionType == MissionType.AREA_COVERAGE){
					GPSMarkerNormal normalMarker = new GPSMarkerNormal(clickedCoordinate);
					normalMarker.attachToMap(map, this.navigationCoordinates);
					this.missionAreaGpsPolygon.draw(this.navigationCoordinates);
				}

			}
			if ( this.owningController.addForbiddenAreasMode() ){
                        //this.addGpsPoint(coordinate, MapMarkerEnum.FORBIDDEN_AREAS, this.forbiddenAreasCoordinates);
                        GPSMarkerForbidden mapMarker =  new GPSMarkerForbidden(clickedCoordinate);
                        mapMarker.attachToMap(map, this.forbiddenAreasCoordinates);
                        this.forbiddenAreaGpsPolygon.draw(this.forbiddenAreasCoordinates);
			}
			

		});
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
