package kvaddakopter.gui.interfaces;

import java.util.ArrayList;

import kvaddakopter.maps.GPSCoordinate;

public class MockMainBus implements MainBusGUIInterface{
	
	protected ArrayList<GPSCoordinate> coords = new ArrayList<>();
	
	
	protected int counter = -1;
	protected int inc = 1;
	
	
	public MockMainBus() {
		coords.add(new GPSCoordinate(58.406659, 15.620358));
		coords.add(new GPSCoordinate(58.406674, 15.620414));
		coords.add(new GPSCoordinate(58.406786, 15.620588));
		coords.add(new GPSCoordinate(58.406854, 15.620936));
		coords.add(new GPSCoordinate(58.406782, 15.621156));
		coords.add(new GPSCoordinate(58.406768, 15.621454));
		coords.add(new GPSCoordinate(58.406857, 15.621765));
		coords.add(new GPSCoordinate(58.406906, 15.622006));
	}

	@Override
	public double getCurrentSpeed() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public GPSCoordinate getCurrentQuadPosition() {
		this.counter += this.inc;
		if(this.counter== 0) this.inc = 1;
        if(this.counter == coords.size() -1){this.inc = -1;}
		
		return coords.get(this.counter);
	}
	
}
