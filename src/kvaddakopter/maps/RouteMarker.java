package kvaddakopter.maps;

import java.util.HashMap;

import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;


/**
 * Created by per on 2014-09-25.
 */
public class RouteMarker {


    /**
     * Map with all image paths for the different types of markers
     */
    private static HashMap<MapMarkerEnum, String> markerPaths;


    static {
        markerPaths = new HashMap<>();
        markerPaths.put(MapMarkerEnum.NAVIGATION_NORMAL, "waypoint_marker.png");
        markerPaths.put(MapMarkerEnum.FORBIDDEN_AREAS, "waypoint_marker_forbidden.png");
        markerPaths.put(MapMarkerEnum.QUAD_MARKER, "quad_marker.png");
        markerPaths.put(MapMarkerEnum.TARGET_MARKER, "target-marker.png");
    }


    /**
     * Factory for creating Route markers.
     *
     * @param latitude  For the new Marker
     * @param longitude 
     * @param iconType  IconType to be showed.
     * @param title     The title for the marker.
     * @return Marker
     */
    public static Marker create(double latitude, double longitude, MapMarkerEnum iconType, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLong(latitude, longitude));
        if (title != null) markerOptions.title(title);

        markerOptions.icon(RouteMarker.figureOutIconPath(iconType));
        return new Marker(markerOptions);
    }


    public static Marker create(double latitude, double longitude, MapMarkerEnum iconType) {
        return RouteMarker.create(latitude, longitude, iconType, null);
    }


    /**
     * Uses counters to figure out what icon to use for the marker.
     *
     * @param iconType Type of icon to be used
     * @return The icon image path
     */
    private static String figureOutIconPath(MapMarkerEnum iconType) {

        return markerPaths.get(iconType);
    }
}
