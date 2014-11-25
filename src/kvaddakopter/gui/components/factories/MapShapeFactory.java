package kvaddakopter.gui.components.factories;

import com.lynden.gmapsfx.javascript.object.GoogleMap;

import kvaddakopter.assignment_planer.MissionType;
import kvaddakopter.gui.components.shapes.GPSCircle;
import kvaddakopter.gui.components.shapes.GPSPath;
import kvaddakopter.gui.components.shapes.GreenGPSPolygon;
import kvaddakopter.gui.components.shapes.MapShapeInterface;
import kvaddakopter.gui.components.shapes.RedGPSPolygon;

public class MapShapeFactory {

	/**
	 * Returns the correct map shape (that is linked to a map) for a specified mission type. 
	 * @param typeOfMission  The current selected MissionType
	 * @param map A Google map
	 * @return A new mapShape
	 */
	public static MapShapeInterface make(MissionType typeOfMission, GoogleMap map){

		MapShapeInterface shape;
		
		switch (typeOfMission) {
		case AROUND_COORDINATE:
			shape = new GPSCircle(map);
			break;
		case ALONG_TRAJECTORY:
			shape = new GPSPath(map);
			break;
		case AREA_COVERAGE:
			shape = new GreenGPSPolygon(map);
			break;
		default:
			shape = new RedGPSPolygon(map);
			break;
		}
		
		return shape;
	}
		
}
