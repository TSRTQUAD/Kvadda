package kvaddakopter.image_processing.data_types;

import org.opencv.core.Scalar;

public class ColorTemplate {
	
	public static final int FORM_CIRLE = 1;
	
	String description;
	private int hueLow, hueHigh, saturationLow, saturationHigh, valueLow, valueHigh;
	private boolean isActive;
	
	public ColorTemplate(String description_, int hueLow_, int hueHigh_, int saturationLow_, int saturationHigh_, int valueLow_, int valueHigh_, int form_){
		description = description_;
		hueLow = hueLow_;
		hueHigh = hueHigh_;
		saturationLow = saturationLow_;
		saturationHigh = saturationHigh_;
		valueLow = valueLow_;
		valueHigh = valueHigh_;
		isActive = true;
	}
	
	public Scalar getLower(){
		return new Scalar(hueLow, saturationLow, valueLow);
	}
	
	public Scalar getUpper(){
		return new Scalar(hueHigh, saturationHigh, valueHigh);
	}
	
	public String getDescription(){
		return description;
	}
	
	public void activate(){
		isActive = true;
	}
	
	public void deactivate(){
		isActive = false;
	}
	
	public boolean isActive(){
		return isActive;
	}
}
