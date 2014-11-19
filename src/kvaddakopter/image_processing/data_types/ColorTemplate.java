package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;

import org.opencv.core.Scalar;

public class ColorTemplate {
	private static final String DEFAULT_DESCRIPTION = "undefined";
	private static final int DEFAULT_HUE_LOW = 0;
	private static final int DEFAULT_HUE_HIGH = 179;
	private static final int DEFAULT_SAT_LOW = 0;
	private static final int DEFAULT_SAT_HIGH = 255;
	private static final int DEFAULT_VAL_LOW = 0;
	private static final int DEFAULT_VAL_HIGH = 400;
	
	public static final int FORM_CIRLE = 1;
	public static final int FORM_SQUARE = 2;
	
	//
	String description;
	private int hueLow, hueHigh, saturationLow, saturationHigh, valueLow, valueHigh;
	private boolean isActive;
	
	//Original Values
	private int oHueLow, oHueHigh, oSaturationLow, oSaturationHigh, oValueLow, oValueHigh;
	
	//Adaptation rate
	private int adaptationConstant;
	
	public ColorTemplate(){
		description = DEFAULT_DESCRIPTION;
		hueLow = DEFAULT_HUE_LOW;
		hueHigh = DEFAULT_HUE_HIGH;
		saturationLow = DEFAULT_SAT_LOW;
		saturationHigh = DEFAULT_SAT_HIGH;
		valueLow = DEFAULT_VAL_LOW;
		valueHigh = DEFAULT_VAL_HIGH;
		isActive = true;
		
		//original values
		oHueLow = hueLow;
		oHueHigh = hueHigh;
		oSaturationLow = saturationLow;
		oSaturationHigh = saturationHigh;
		oValueLow = valueLow;
		oValueHigh = valueHigh;
		
		//Adaptation constant set to 30
		adaptationConstant = 30;
	}
	
	public ColorTemplate(String description_, int hueLow_, int hueHigh_, int saturationLow_, int saturationHigh_, int valueLow_, int valueHigh_, int form_){
		description = description_;
		hueLow = hueLow_;
		hueHigh = hueHigh_;
		saturationLow = saturationLow_;
		saturationHigh = saturationHigh_;
		valueLow = valueLow_;
		valueHigh = valueHigh_;
		isActive = true;
		
		//original values
		oHueLow = hueLow_;
		oHueHigh = hueHigh_;
		oSaturationLow = saturationLow_;
		oSaturationHigh = saturationHigh_;
		oValueLow = valueLow_;
		oValueHigh = valueHigh_;
		
		//Adaptation constant set to 30
		adaptationConstant = 30;
	}
	
	public void setThresholds(){
		
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
	 * Adapt colorTemplate according to object HSV channels with low pass filtering
	 * @param objectHSVChannels
	 * @param hueWindow Value [0:255]
	 * @param satWindow Value [0:255]
	 * @param valWindow [0:255]
	 */
	public void adapt(ArrayList<Long> objectHSVChannels,int hueWindow, int satWindow, int valWindow){
		float T = adaptationConstant; // number of updates to 63%
		//Hue update
		hueLow = (int) (hueLow + ((float)(objectHSVChannels.get(0)-hueWindow/2 - hueLow))/T);
		hueHigh = (int) (hueHigh + ((float)(objectHSVChannels.get(0)+hueWindow/2 - hueHigh))/T);
		
		//Saturation update
		saturationLow = (int) (saturationLow +((float)(objectHSVChannels.get(1)-satWindow/2 - saturationLow))/T);
		saturationHigh = (int) (saturationHigh + ((float)(objectHSVChannels.get(1)+satWindow/2 - saturationHigh))/T);
		
		//Value update
		valueLow = (int) (valueLow + ((float)(objectHSVChannels.get(2)-valWindow/2 - valueLow))/T);
		valueHigh = (int) (valueHigh + ((float)(objectHSVChannels.get(2)+valWindow/2 - valueHigh))/T);
	}
	
	/**
	 * Adapt color template towards original bounds
	 * Use this to adapt if no targets are found
	 */
	public void adaptToOriginalBounds(){
		float T = adaptationConstant; // number of updates to 63%
		//Hue update
		hueLow = (int) ((float)(hueLow + ((oHueLow) - hueLow))/T);
		hueHigh = (int) ((float)(hueHigh + ((oHueHigh) - hueHigh))/T);
		
		//Saturation update
		saturationLow = (int) ((float)(saturationLow +(oSaturationLow - saturationLow))/T);
		saturationHigh = (int) ((float)(saturationHigh + (oSaturationHigh - saturationHigh))/T);
		
		//Value update
		valueLow = (int) ((float)(valueLow + (oValueLow - valueLow))/T);
		valueHigh = (int) ((float)(valueHigh + (oValueHigh - valueHigh))/T);
	}

	public void setHueLow(int val) {
		hueLow = val;
	}
	
	public void setHueHigh(int val) {
		hueHigh = val;
	}
	
	public void setSatLow(int val) {
		saturationLow = val;
	}
	
	public void setSatHigh(int val) {
		saturationHigh = val;
	}
	
	public void setValLow(int val) {
		valueLow = val;	
	}
	
	public void setValHigh(int val) {
		valueHigh = val;	
	}
	
	public int getHueLow() {
		return hueLow;
	}
	
	public int getHueHigh() {
		return hueHigh;
	}
	
	public int getSatLow() {
		return saturationLow;
	}
	
	public int getSatHigh() {
		return saturationHigh;
	}
	
	public int setValLow() {
		return valueLow;
	}
	
	public int setValHigh() {
		return valueHigh;	
	}
	
	
}
