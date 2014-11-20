package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;

import netscape.javascript.JSObject;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.Area;
import kvaddakopter.assignment_planer.MissionType;
import kvaddakopter.gui.components.GpsToAreaTransformer;
import kvaddakopter.gui.components.factories.MapShapeFactory;
import kvaddakopter.gui.components.shapes.GPSCircle;
import kvaddakopter.gui.components.shapes.MapShapeInterface;
import kvaddakopter.gui.controllers.TabPlaneraController;



/**
 * Used as a high-level representation of the Google Map used for planning.
 */
public class PlanningMap extends BaseMap implements MapComponentInitializedListener{

	/**
	 * Owning Controller
	 */
	private TabPlaneraController owningController;

	
	
	private ArrayList<MapShapeInterface> navigationMapShapes;
	private ArrayList<MapShapeInterface> forbiddenShapes;
	
	private int currentActiveMissionAreaCounter = 0;
	private int currentActiveForbiddenAreaCounter = 0;
	
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
		
		this.createMapWithStartLocation();
		this.navigationMapShapes = new ArrayList<MapShapeInterface>();
		this.forbiddenShapes = new ArrayList<MapShapeInterface>();
		this.isMapInitialized = true;
		
		
		this.addMapEventListeners();

	}
	
	/**
	 * Clear all navigation markers on the map.
	 */
	public void clearNavigationCoordinates(){
		if (this.isMapInitialized){
			for(MapShapeInterface shape : this.navigationMapShapes){
				shape.remove();
			}
			this.navigationMapShapes.clear();
		}
	}
	
	/**
	 * Clear all Forbidden Area markers on the map.
	 */
	public void clearForbiddenAreasCoordinates(){
		if (this.isMapInitialized){
			for(MapShapeInterface shape : this.forbiddenShapes){
				shape.remove();
			}
			this.forbiddenShapes.clear();
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
	 * The current set circle radius for the mission area
	 * @return radius
	 */
	public double[] getCircleRadius(){
		ArrayList<Double> radiusList = new ArrayList<Double>();
		if(this.owningController.getCurrentSelectedMissionType() == MissionType.AROUND_COORDINATE){
			for(MapShapeInterface shape : this.navigationMapShapes){
				if (shape instanceof GPSCircle){
					radiusList.add( ((GPSCircle) shape).getRadus());
				}
			}
		}
		int listSize = radiusList.size();
		double[] radius = new double[listSize];
		int j = 0;
		for (Double value : radiusList){
			radius[j] = value.doubleValue();
			j++;
		}
		return radius;
	}
	
	
	/**
	 * Returns an array of all navigation coordiantes
	 * @return list of coordinates
	 */
	public ArrayList<Area> allNavigationCoordinates() {
		return GpsToAreaTransformer.transform(this.navigationMapShapes);
		
	}

	
	/**
	 * Returns an array of all forbidden area coordinates
	 * @return list of coordinates
	 */
	public ArrayList<Area> allForbiddenAreaCoordinates() {
		return GpsToAreaTransformer.transform( this.forbiddenShapes );
	}
	
	/**
	 * Where should we start the mapView. The world is yours!
	 * @return
	 */
	protected LatLong startCoordinate(){
		return new LatLong(58.406659, 15.620358);
	}
	

	/**
	 * Sets all event listeners for the map.
	 */
	private void addMapEventListeners() {
		//EVENT FOR USER CLICKED MAP
		this.map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
			
			//Coordinate where the user clicked.
			LatLong clickedCoordinate = new LatLong((JSObject) obj.getMember("latLng"));
			//MissionType missionType = this.owningController.getCurrentSelectedMissionType();

			if (this.owningController.addMissionCoordinatesMode() ){
				this.navigationMapShapes.get(this.currentActiveMissionAreaCounter)
				.addCoordinate(clickedCoordinate);
			}
			
			if ( this.owningController.addForbiddenAreasMode() ){
				this.forbiddenShapes
				.get(this.currentActiveForbiddenAreaCounter)
				.addCoordinate(clickedCoordinate);	
			}
			
		});
	}
	
	
	public void createNewMapShape() {
		/**
		MissionType missionType = this.owningController.getCurrentSelectedMissionType();
		if (missionType == MissionType.AROUND_COORDINATE){
			GPSCircle coordinate = new GPSCircle(this.map);
			this.navigationGPSCoordinate.add(coordinate);
			this.currentActiveMissionAreaCounter = this.navigationGPSCoordinate.size() -1;
		} else if (missionType == MissionType.ALONG_TRAJECTORY){
			GPSPath trajectory = new GPSPath(this.map);
			this.navigationGpsTrajectories.add(trajectory);
			this.currentActiveMissionAreaCounter = this.navigationGpsTrajectories.size() -1;
		}else if(missionType == MissionType.AREA_COVERAGE){
			GreenGPSPolygon polygon = new GreenGPSPolygon(this.map);
			this.navigationGPSPolygons.add(polygon);
			this.currentActiveMissionAreaCounter = this.navigationGPSPolygons.size() - 1;
		}
		**/
		MissionType missionType = this.owningController.getCurrentSelectedMissionType();
		MapShapeInterface newShape =  MapShapeFactory.make(missionType, this.map);
		System.out.println(newShape);
		this.navigationMapShapes.add(newShape);
		this.currentActiveMissionAreaCounter = this.navigationMapShapes.size() - 1;
	}


	public void createNewForbiddenArea(){
		/**
		RedGPSPolygon redPolygon = new RedGPSPolygon(this.map);
		this.forbiddenGPSPolygons.add(redPolygon);
		this.currentActiveForbiddenAreaCounter = this.forbiddenGPSPolygons.size() - 1;
		**/
		MapShapeInterface newShape =  MapShapeFactory.make(MissionType.NULL_MISSION, this.map);
		this.forbiddenShapes.add(newShape);
		this.currentActiveForbiddenAreaCounter = this.forbiddenShapes.size() - 1;
	}
	



}
