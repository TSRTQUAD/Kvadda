package kvaddakopter.gui.components;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.Area;
import kvaddakopter.gui.components.shapes.MapShapeInterface;

public class GpsToAreaTransformer {
	public static ArrayList<Area> transform(ArrayList<MapShapeInterface> shapes){
		ArrayList<Area> list = new ArrayList<Area>();
		
		
		int j = 0;
		for(MapShapeInterface shape: shapes){

			ArrayList<AbstractGPSMarker> markers = shape.getMarkers();
			Area currentAreaCoordinates = new Area();
			currentAreaCoordinates.area = new double[markers.size()][2];
			j = 0;
			for (AbstractGPSMarker marker: markers){
				currentAreaCoordinates.area[j][0] = marker.getLatitude();
				currentAreaCoordinates.area[j][1] = marker.getLongitude();
				j++;
			}
			list.add(currentAreaCoordinates);
		}

		return list;
	}
	
}
