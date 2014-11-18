package kvaddakopter.gui.components;

import java.util.ArrayList;

import kvaddakopter.assignment_planer.Area;

public class GpsToAreaTransformer {
	public static ArrayList<Area> transform(ArrayList<AbstractGPSMarker> gpsCoordinates){
		Area areaList = new Area();
		areaList.area = new double[gpsCoordinates.size()][2];
		ArrayList<Area> list = new ArrayList<Area>();

		int i = 0;
		for(AbstractGPSMarker coord: gpsCoordinates){
				areaList.area[i][0] = coord.getLatitude();
				areaList.area[i][1] = coord.getLongitude();
			i++;
		}

		list.add(areaList);

		return list;
	}
	
}
