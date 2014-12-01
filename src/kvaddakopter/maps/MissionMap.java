package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;

import java.util.ArrayList;
import java.util.HashMap;

import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.components.GPSMarkerNormal;
import kvaddakopter.gui.components.QuadMarker;
import kvaddakopter.gui.components.shapes.TargetMap;
import kvaddakopter.gui.controllers.TabUtforController;



/**
 * Used as a high-level representation of the Google Map used while executing a mission.
 */
public class MissionMap extends BaseMap implements MapComponentInitializedListener{



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
	 * Target container
	 */
	private TargetMap targets;
	
	
	/**
	 * The quad Marker
	 */
	private QuadMarker quadMarker;
	
	
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
		this.createMapWithStartLocation();
	
		//this.quadMarker.attachToMap(map);
		///this.createMapWithStartLocation();
	}
	
	
	/**
	 * Draw the quad marker on the specified position.
	 * @param latitude
	 * @param longitude
	 */
	public void drawQuad(double latitude, double longitude) {
		if (this.quadMarker == null){
			this.quadMarker = new QuadMarker(new LatLong(latitude, longitude));  
			this.quadMarker.attachToMap(map);
		}
		this.quadMarker.updatePosition(new LatLong(latitude, longitude));
		map.setZoom(map.getZoom() - 1);
		map.setZoom(map.getZoom() + 1);
	}
	
	
	/**
	 * Where should we start the mapView. The world is yours!
	 * @return
	 */
	protected LatLong startCoordinate(){
		return new LatLong(58.395128,15.575096);
	}

	public void drawTargetsOnMap(HashMap<String, GPSCoordinate> targets) {
		if (this.targets == null){
			this.targets = new TargetMap(this.map);  
		}
		if( targets.size() == 0){
			this.targets.remove();
		} else {
			this.targets.addListOfTargets(targets);			
		}
		map.setZoom(map.getZoom() - 1);
		map.setZoom(map.getZoom() + 1);
		
	}
	

	



}
