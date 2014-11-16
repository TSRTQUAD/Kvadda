package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.List;

import kvaddakopter.Mainbus.Mainbus;
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
	
	// Minimum object size
	static final int MINIMUM_OBJECT_SIZE = 2000;
	static final int MAXIMUM_OBJECT_SIZE = 15000; //Not used yet

	//Morphology 
	static final int MORPH_KERNEL_SIZE = 16;
	static final int MORPH_KERNEL_TYPE = Imgproc.MORPH_ELLIPSE;

	// Color templates
	ArrayList<ColorTemplate> colorTemplates;
	
	//Adaptive coloring
	private boolean mUsingColorAdaption = true;

	public ColorDetection(){
		super();
		colorTemplates = new ArrayList<ColorTemplate>();
	}


	@Override
	public boolean isMethodActive(Mainbus mainbus) {
		return mainbus.isColorDetectionOn();
	}
	@Override
	public ArrayList<TargetObject> runMethod(ImageObject imageObject) {

		// Convert RGB to HSV
		Mat HSVImage = new Mat();
		Imgproc.cvtColor(imageObject.getImage(), HSVImage, Imgproc.COLOR_BGR2HSV);

		Mat resultImage = new Mat(HSVImage.rows(), HSVImage.cols(), CvType.CV_8U);

		Size morphSize = new Size(MORPH_KERNEL_SIZE, MORPH_KERNEL_SIZE);
		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
		
		
		Mat cutoutImage = Mat.zeros(HSVImage.size(), CvType.CV_8UC3);
		
		
		for(ColorTemplate colorTemplate : colorTemplates){
			//number of targets found from this template
			int numberOfTargetsFound = 0;
			// Threshold with inRange
			Mat thresholdImage = new Mat();
			Mat dilatedImage = new Mat();
			resultImage.setTo(new Scalar(0));
			
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
			ArrayList<Rect> boundingBoxes = new ArrayList<Rect>();
			
			//Get contours and hierarchy of binary image
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			Imgproc.findContours(resultImage, contours, hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
			
			//Calculate bounding boxes from contours, 
			//calculate cutout image from HSVImage and contours
			//set target HSV channels for adaptive color detection		
			boundingBoxes = getBoundingBoxes(contours, hierarchy, MINIMUM_OBJECT_SIZE);
			numberOfTargetsFound  = boundingBoxes.size();
			Core.bitwise_or(cutoutImage, cutout(HSVImage,contours), cutoutImage);
			
			//Calculate mean HSV channel values with 10 as value threshhold 
			ArrayList<Double> targetHSVChannels = calculateMeanHSVValues(cutoutImage, 10);
			
			mIntermeditateResult = cutoutImage;
			
			//Convert boundingboxes to targetObjects, draw boundingboxes in resultImage
			targetObjects = convertToTargets(boundingBoxes, resultImage);
			
			//Draw target HSV values
			for(TargetObject target:targetObjects){
				//System.out.println((int)target.getPosition().get(0,0)[0]);
				drawTargetHSVValues(cutoutImage, targetHSVChannels, (int)target.getPosition().get(0,0)[0], (int)target.getPosition().get(1,0)[0]);
			}
			
			//Adapt color template towards HSV-channels of detected target
			//If no target is found the template is adapted towards original bounds
			if(isUsingColorAdaption() && numberOfTargetsFound > 0){
				colorTemplate.adapt(targetHSVChannels, 100, 100, 100);
			}
			else if (isUsingColorAdaption()){
				colorTemplate.adaptToOriginalBounds();
			}
			
			//Free memory
			thresholdImage.release();
			dilatedImage.release();
		}

		return targetObjects;

	}
	
	/**
	 * Cutout of contours in Image, sets objectHSVChannels to mean value of the cutout
	 * @param fromImage image to cut out from
	 * @param contours countours to cut out
	 * @param objectHSVChannels mean value of HSV channels of cutout in input image
	 * @return cutout Image with HSV channel information
	 */
	private Mat cutout(Mat fromImage, List<MatOfPoint> contours){
		
		Mat resultImage = Mat.zeros(fromImage.size(), CvType.CV_8UC3);
		List<MatOfPoint> contourMask = new ArrayList<MatOfPoint>(); //Container for each of the contours in turn
		
		// Cutout a region and calculate mean HSV values (in a bad way)
		// Then display them on the image (in a not so bad way)
		//
        // Create a mask for each contour to mask out that region from image.
		for(MatOfPoint c:contours){
			//Place code here to do hsv cutout for each contour
			contourMask.clear();
			double contourArea = Imgproc.contourArea(c);

			//Only mask with real objects (big blobs)
			if(contourArea > MINIMUM_OBJECT_SIZE){
				contourMask.add(c);
				
				Mat mask = Mat.zeros(fromImage.size(), CvType.CV_8UC1);
		        Imgproc.drawContours(mask, contourMask, 0, new Scalar(255), Core.FILLED); // This is a OpenCV function

				Mat cutout = new Mat();
				fromImage.copyTo(cutout, mask);								
			    
			    //Add cutout to result
			    Core.add(resultImage, cutout, resultImage);
			    
			    //free memory
			    cutout.release();
			}
		}
		return resultImage;
	}
	
	/**
	 * TODO minimize calculation cost, doubles->longs???
	 * Calculates mean of HSV channels in cutout Image
	 * @param cutout
	 * @param threshold
	 * @return ArrayList<Double> where index 0->Hue,1->Saturation,2->Value
	 */
	private ArrayList<Double> calculateMeanHSVValues(Mat cutout, double threshold){
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
		channelMeanValues.add(SVal);
		VVal = Vtot/numVals;
		channelMeanValues.add(VVal);
		return channelMeanValues;

	}
	
	/**
	 * Draw mean HSV values of alike targets below targets
	 * @param image
	 * @param targetHSVChannels
	 * @param targetPosX
	 * @param targetPosY
	 */
	private void drawTargetHSVValues(Mat image, ArrayList<Double> targetHSVChannels, int targetPosX, int targetPosY){
		//Draw HSV channels, should be moved and draw under each object
		String txtString = String.format("H = %4f", targetHSVChannels.get(0));
	    Core.putText(image, txtString, new Point(targetPosX, targetPosY+20) , Core.FONT_HERSHEY_SIMPLEX, .5, new Scalar(255, 255, 255), 1, 8, false);
	    
		txtString = String.format("S = %4f", targetHSVChannels.get(1));
	    Core.putText(image, txtString, new Point(targetPosX, targetPosY+40) , Core.FONT_HERSHEY_SIMPLEX, .5, new Scalar(255, 255, 255), 1, 8, false);
	    
		txtString = String.format("V = %4f", targetHSVChannels.get(2));
	    Core.putText(image, txtString, new Point(targetPosX, targetPosY+60) , Core.FONT_HERSHEY_SIMPLEX, .5, new Scalar(255, 255, 255), 1, 8, false);
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
	
	public int addTemplate(ColorTemplate cTemplate){
		colorTemplates.add(cTemplate);
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
	
	public boolean isUsingColorAdaption(){
		return mUsingColorAdaption;
	}
	
	public void setUsingColorAdaption(boolean b){
		mUsingColorAdaption = b;
	}
}
