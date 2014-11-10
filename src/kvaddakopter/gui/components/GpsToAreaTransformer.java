package kvaddakopter.gui.components;

import java.util.ArrayList;

import com.lynden.gmapsfx.javascript.object.LatLong;

import kvaddakopter.assignment_planer.Area;
import kvaddakopter.maps.GPSCoordinate;

public class GpsToAreaTransformer {
	public static ArrayList<Area> transform(ArrayList<GPSMarker> gpsCoordinates){
		Area areaList = new Area();
		ArrayList<Area> list = new ArrayList<Area>();

		int i = 0;
		for(GPSMarker coord: gpsCoordinates){
			areaList.area[i][1] = coord.getLatitude();
			areaList.area[i][2] = coord.getLongitude();
			i++;
		}

		list.add(areaList);

		return list;
	}
	
}
