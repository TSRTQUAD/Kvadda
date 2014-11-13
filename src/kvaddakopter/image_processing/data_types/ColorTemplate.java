package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;

import org.opencv.core.Scalar;

public class ColorTemplate {
	
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
	public void adapt(ArrayList<Double> objectHSVChannels,int hueWindow, int satWindow, int valWindow){
		int T = adaptationConstant; // number of updates to 63%
		//Hue update
		hueLow = (int) (hueLow + ((objectHSVChannels.get(0)-hueWindow/2) - hueLow)/T);
		hueHigh = (int) (hueHigh + ((objectHSVChannels.get(0)+hueWindow/2) - hueHigh)/T);
		
		//Saturation update
		saturationLow = (int) (saturationLow +(objectHSVChannels.get(1)-satWindow/2 - saturationLow)/T);
		saturationHigh = (int) (saturationHigh + (objectHSVChannels.get(1)+satWindow/2 - saturationHigh)/T);
		
		//Value update
		valueLow = (int) (valueLow + (objectHSVChannels.get(2)-valWindow/2 - valueLow)/T);
		valueHigh = (int) (valueHigh + (objectHSVChannels.get(2)+valWindow/2 - valueHigh)/T);
	}
	
	/**
	 * Adapt color template towards original bounds
	 * Use this to adapt if no targets are found
	 */
	public void adaptToOriginalBounds(){
		int T = adaptationConstant; // number of updates to 63%
		//Hue update
		hueLow = (int) (hueLow + ((oHueLow) - hueLow)/T);
		hueHigh = (int) (hueHigh + ((oHueHigh) - hueHigh)/T);
		
		//Saturation update
		saturationLow = (int) (saturationLow +(oSaturationLow - saturationLow)/T);
		saturationHigh = (int) (saturationHigh + (oSaturationHigh - saturationHigh)/T);
		
		//Value update
		valueLow = (int) (valueLow + (oValueLow - valueLow)/T);
		valueHigh = (int) (valueHigh + (oValueHigh - valueHigh)/T);
	}
}
