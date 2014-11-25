package kvaddakopter.gui.components.shapes;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.PolygonOptions;

public class GreenGPSPolygon extends GPSPolygon{
	
	public GreenGPSPolygon(GoogleMap map) {
		super(map);
	}

	@Override
	protected PolygonOptions getOptions(MVCArray mvc){
		return new PolygonOptions()
			.paths(mvc)
			.strokeColor("green")
			.strokeWeight(2)
			.editable(false)
			.fillColor("lightGreen")
			.fillOpacity(0.1);
	}
}
