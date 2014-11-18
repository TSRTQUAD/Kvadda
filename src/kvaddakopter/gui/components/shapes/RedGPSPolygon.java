package kvaddakopter.gui.components.shapes;

import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.shapes.PolygonOptions;

public class RedGPSPolygon extends GPSPolygon{
	
	public RedGPSPolygon(GoogleMap map) {
		super(map);
	}

	/**
	 * Returns the shape Options
	 * @param mvc
	 * @return
	 */
	protected PolygonOptions getOptions(MVCArray mvc){
		return new PolygonOptions()
			.paths(mvc)
			.strokeColor("red")
			.strokeWeight(2)
			.editable(false)
			.fillColor("red")
			.fillOpacity(0.3);
	}
}
