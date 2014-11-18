package kvaddakopter.maps;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;

import java.util.ArrayList;

import kvaddakopter.gui.components.AbstractGPSMarker;
import kvaddakopter.gui.components.QuadMarker;
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
	 * Is map initialized Initialized
	 */
	boolean isMapInitialized = false;
	
	
	private QuadMarker quadMarker;
	
	
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
	}
	
	
	/**
	 * Draw the quad marker on the specified position.
	 * @param latitude
	 * @param longitude
	 */
	public void drawQuad(double latitude, double longitude){
		if (!this.isMapInitialized) return;
		LatLong coordinate = new LatLong(latitude, longitude);
	    if(this.quadMarker != null){
	    	System.out.println("AND IS DELETED:");
	    	System.out.println(this.quadMarker.getMarker());
	    	System.out.println("------------------------");
	    	this.quadMarker.clearFromMap(this.map);
	    	this.quadMarker.clearFromMap(this.map);
	    	this.quadMarker.clearFromMap(this.map);
	    	this.quadMarker = null;
	    }
	    QuadMarker marker = new QuadMarker(coordinate);
		marker.attachToMap(this.map);
		this.quadMarker = marker;
    System.out.println("CREATED!!!!");
		System.out.println(this.quadMarker.getMarker());
	   

		
	}
	
	
	/**
	 * Where should we start the mapView. The world is yours!
	 * @return
	 */
	protected LatLong startCoordinate(){
		return new LatLong(58.406659, 15.620358);
	}
	

	



}
