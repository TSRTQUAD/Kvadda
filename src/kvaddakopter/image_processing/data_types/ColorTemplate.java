package kvaddakopter.image_processing.data_types;

import org.opencv.core.Scalar;

public class ColorTemplate {
	
	public static final int FORM_CIRLE = 1;
	public static final int FORM_SQUARE = 2;
	
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
	
	/**
	 * TODO Might need more fine tuning
	 * Adapt colorTemplate according to object HSV channels
	 * @param objectHSVChannels
	 * @param hueWindow Value [0:1]
	 * @param satWindow Value [0:1]
	 * @param valWindow [0:1]
	 */
	public void adapt(double[] objectHSVChannels,double hueWindow, double satWindow, double valWindow){
		hueLow = (int) (objectHSVChannels[0]*(1-hueWindow/2));
		hueHigh = (int) (objectHSVChannels[0]*(1+hueWindow/2));
		saturationLow = (int) (objectHSVChannels[1]*(1-satWindow/2));
		saturationHigh = (int) (objectHSVChannels[1]*(1+satWindow/2));
		valueLow = (int) (objectHSVChannels[2]*(1-valWindow/2));
		valueHigh = (int) (objectHSVChannels[2]*(1-valWindow/2));
	}
}
