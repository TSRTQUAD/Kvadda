package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.List;

import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class ColorDetection  extends DetectionClass{

	// Color threshold
	static final int HUE_LOW = 175 - 20;
	static final int HUE_HIGH = 175 + 20;
	static final int SATURATION_LOW = 50;
	static final int SATURATION_HIGH = 255;
	static final int VALUE_LOW = 50;
	static final int VALUE_HIGH = 255;
	
	// Minimum object size
	static final int MINIMUM_OBJECT_SIZE = 10000;

	//Morphology 
	static final int MORPH_KERNEL_SIZE = 16;
	static final int MORPH_KERNEL_TYPE = Imgproc.MORPH_ELLIPSE;

	// Color templates
	ArrayList<ColorTemplate> colorTemplates;
	
	//Adaptive coloring
	private boolean mUsingAdaptiveColoring = true;

	public ColorDetection(){
		super();
		colorTemplates = new ArrayList<ColorTemplate>();
		//colorTemplates.add(new ColorTemplate("Blue ball", 90, 140, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
		//colorTemplates.add(new ColorTemplate("Yellow ball", 10, 50, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
	}

	@Override
	public ArrayList<TargetObject> start(ImageObject imageObject) {

		// Convert RGB to HSV
		Mat HSVImage = new Mat();
		Imgproc.cvtColor(imageObject.getImage(), HSVImage, Imgproc.COLOR_BGR2HSV);

		Mat resultImage = new Mat(HSVImage.rows(), HSVImage.cols(), CvType.CV_8U);
		resultImage.setTo(new Scalar(0));


		Mat thresholdImage = new Mat();
		Size morphSize = new Size(MORPH_KERNEL_SIZE, MORPH_KERNEL_SIZE);
		Mat dilatedImage = new Mat();
		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
		for(ColorTemplate colorTemplate : colorTemplates){
			// Threshold with inRange
			Core.inRange(HSVImage, colorTemplate.getLower(), colorTemplate.getUpper(), thresholdImage);


			// Do morphological operations
			Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, morphSize);
			//Opening
			Imgproc.morphologyEx(thresholdImage, dilatedImage, Imgproc.MORPH_OPEN, kernel);
			//Closing
			Imgproc.morphologyEx(dilatedImage, dilatedImage, Imgproc.MORPH_CLOSE, kernel);

			// Add results to binary result image
			Core.bitwise_or(resultImage, dilatedImage, resultImage);
			
			//Target detection
			//detectTargets()
			ArrayList<Rect> boundingBoxes = new ArrayList<Rect>();
			
			//Get contours of binary image
			List<MatOfPoint> contours = getContours(resultImage);
			
			//Calculate bounding boxes from contours, 
			//calculate cutout image from HSVImage and contours
			//set target HSV channels for adaptive color detection
			boundingBoxes = getBoundingBoxes(contours,1000);
			ArrayList<Double> targetHSVChannels = new ArrayList<Double>();
			mIntermeditateResult = cutout(HSVImage,contours,targetHSVChannels);
			
			//Convert boundingboxes to targetObjects, draw boundingboxes in resultImage
			targetObjects = convertToTargets(boundingBoxes, resultImage);
		}

		return targetObjects;

	}
	
	/**
	 * TODO does this work for multiple  particles that look the same???? //Martin
	 * Cutout of contours in Image, sets objectHSVChannels to mean value of the cutout
	 * @param fromImage image to cut out from
	 * @param contours countours to cut out
	 * @param objectHSVChannels mean value of HSV channels of cutout in input image
	 * @return cutout Image with HSV channel information
	 */
	private Mat cutout(Mat fromImage, List<MatOfPoint> contours, ArrayList<Double> objectHSVChannels){
		// Cutout a region and calculate mean HSV values (in a bad way)
		// Then display them on the image (in a not so bad way)
		//
        // Create a mask for each contour to mask out that region from image.
        Mat mask = Mat.zeros(fromImage.size(), CvType.CV_8UC1);
        Imgproc.drawContours(mask, contours, -1, new Scalar(255), Core.FILLED); // This is a OpenCV function

		Mat cutout = new Mat();
		fromImage.copyTo(cutout, mask);
		
		//Calculate mean HSV channel values with 10 as value threshhold
		objectHSVChannels = calculateMeanHSVValues(cutout,10);
	
		String txtString = String.format("H = %4f", objectHSVChannels.get(0));
	    Core.putText(cutout, txtString, new Point(25, 280) , Core.FONT_HERSHEY_SIMPLEX, .7, new Scalar(255, 255, 255), 2, 8, false);
	    
		txtString = String.format("S = %4f", objectHSVChannels.get(1));
	    Core.putText(cutout, txtString, new Point(25, 300) , Core.FONT_HERSHEY_SIMPLEX, .7, new Scalar(255, 255, 255), 2, 8, false);
	    
		txtString = String.format("V = %4f", objectHSVChannels.get(2));
	    Core.putText(cutout, txtString, new Point(25, 320) , Core.FONT_HERSHEY_SIMPLEX, .7, new Scalar(255, 255, 255), 2, 8, false);
	    
		return cutout;
	}
	
	/**
	 * Calculates mean of HSV channels in cutout Image
	 * @param cutout
	 * @param threshold
	 * @return ArrayList<Double> where index 0->Hue,1->Saturation,2->Value
	 */
	private ArrayList<Double> calculateMeanHSVValues(Mat cutout,double threshold){
		ArrayList<Double> channelMeanValues = new ArrayList<Double>();
		
		double HVal = 0, SVal = 0, VVal = 0;
		
		double Htot = 0, Stot = 0, Vtot = 0;
		double numVals = 0;
		double[] tmpHSV;
		// Worthless mean function
		for(int r = 0; r < cutout.rows(); r++){
			for(int c = 0; c < cutout.cols(); c++){
				tmpHSV = cutout.get(r, c);
				if(tmpHSV[2] > threshold){
					numVals++;
					Htot += tmpHSV[0];
					Stot += tmpHSV[1];
					Vtot += tmpHSV[2];
				}
			}
		}
		HVal = Htot/numVals;
		channelMeanValues.add(HVal);
		SVal = Stot/numVals;
		channelMeanValues.add(VVal);
		VVal = Vtot/numVals;
		channelMeanValues.add(SVal);
		return channelMeanValues;

	}

	/**
	 * adds color template to method
	 * @param description_
	 * @param hueLow_
	 * @param hueHigh_
	 * @param saturationLow_
	 * @param saturationHigh_
	 * @param valueLow_
	 * @param valueHigh_
	 * @param form_type_
	 * @return
	 */
	public int addTemplate(String description_, int hueLow_, int hueHigh_, int saturationLow_, int saturationHigh_, int valueLow_, int valueHigh_, int form_type_){
		colorTemplates.add(new ColorTemplate(description_, hueLow_, hueHigh_, saturationLow_, saturationHigh_, valueLow_, valueHigh_, form_type_));
		return colorTemplates.size() - 1;
	}

	/**
	 * Activate template with id# id
	 * @param id
	 */
	public void activateTemplate(int id){
		synchronized (colorTemplates) {
			if(id >= colorTemplates.size() || id < 0) return;
			colorTemplates.get(id).activate();
		}
	}
	
	/**
	 * Deactivate template with id# id
	 * @param id
	 */
	public void deactivateTemplate(int id){
		if(id >= colorTemplates.size() || id < 0) return;
		colorTemplates.get(id).deactivate();
	}
	
	/**
	 * check if template with id# id is active
	 * @param id
	 * @return
	 */
	public boolean isActive(int id){
		if(id >= colorTemplates.size() || id < 0) return false;
		return colorTemplates.get(id).isActive();
	}
	
	/**
	 * Get current color templates descriptions. ID of a template is the position in the ArrayList. 
	 * @return ArrayList of descriptions.
	 */
	public ArrayList<String> getTemplates(){
		ArrayList<String> res = new ArrayList<String>();
		for(ColorTemplate colorTemplate : colorTemplates){
			res.add(colorTemplate.getDescription());
		}
		
		return res;
	}
	
	public boolean isUsingAdaptiveColoring(){
		return mUsingAdaptiveColoring;
	}
	
	public void setUsingAdaptiveColoring(boolean b){
		mUsingAdaptiveColoring = b;
	}
}
