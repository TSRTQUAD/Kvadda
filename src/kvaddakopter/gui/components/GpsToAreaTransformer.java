package kvaddakopter.gui.components;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.Area;

public class GpsToAreaTransformer {
	public static ArrayList<Area> transform(ArrayList<AbstractGPSMarker> gpsCoordinates){
		Area areaList = new Area();
		ArrayList<Area> list = new ArrayList<Area>();

		int i = 0;
		for(AbstractGPSMarker coord: gpsCoordinates){
			areaList.area[i][1] = coord.getLatitude();
			areaList.area[i][2] = coord.getLongitude();
			i++;
		}

		list.add(areaList);

		return list;
	}
	
}
