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

	// TMP!!!!
	Mat HSVImage;

	public ColorDetection(){
		super();
		colorTemplates = new ArrayList<ColorTemplate>();
		//colorTemplates.add(new ColorTemplate("Blue ball", 90, 140, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
		//colorTemplates.add(new ColorTemplate("Yellow ball", 10, 50, 50, 255, 50, 255, ColorTemplate.FORM_CIRLE));
	}

	@Override
	public ArrayList<TargetObject> start(ImageObject imageObject) {

		// Convert RGB to HSV
		HSVImage = new Mat();
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
			
			//Detect targets
			//TODO should return targetObjects
			ArrayList<Rect> boundingBoxes = new ArrayList<Rect>();
			boundingBoxes = getBoundingBoxes(resultImage);
			// Convert blobs to target objects
			// Temporary solution using bounding boxes
			for(Rect boundingBox : boundingBoxes){
				Mat pos = new Mat(2, 1, CvType.CV_64F);
				pos.put(0, 0, boundingBox.x);
				pos.put(1, 0, boundingBox.y);
				targetObjects.add(new TargetObject(pos, 1));
				Core.rectangle(
						resultImage, 
						new Point(boundingBox.x, boundingBox.y), 
						new Point(boundingBox.x + boundingBox.width, boundingBox.y + boundingBox.height), 
						new Scalar(255,0,0), 
						1);
			}
		}

		// Create an intermediate result image
		//mIntermeditateResult = resultImage;

		return targetObjects;

	}
	
	//Should return ArrayList<TargetObject> 
	private ArrayList<Rect> getBoundingBoxes(Mat binaryImage){
		//List of targets in the image
		//Using openCV findContours-routine to get pixel coordinates of the current blobs.
		
		//Parameters ( and return values) for the findContour
		Mat hierarchy  = new Mat();
		Mat contourImage = binaryImage.clone(); // remove clone 
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		
		Imgproc.findContours(contourImage, contours, hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
				
		//Selecting the largest blob
		double largestArea = -1;
		int index = -1;
		ArrayList<Rect> boxes = new ArrayList<Rect>();
		for (MatOfPoint c : contours) {
			double contourArea = Imgproc.contourArea(c);
			if(contourArea > largestArea){
				largestArea = contourArea;
				index = contours.indexOf(c);
				}
			}
		/*System.out.println(
				"Num Contours: " + contours.size() + "\n" +
						"Largest at index: " + index 	 
						);*/
		
		
		
		
		// Cutout a region and calculate mean HSV values (in a bad way)
		// Then display them on the image (in a not so bad way)
		//
        // Create a mask for each contour to mask out that region from image.
        Mat mask = Mat.zeros(HSVImage.size(), CvType.CV_8UC1);
        Imgproc.drawContours(mask, contours, -1, new Scalar(255), Core.FILLED); // This is a OpenCV function

		Mat cutout = new Mat();
		HSVImage.copyTo(cutout, mask);
		
		
		double HVal = 0, SVal = 0, VVal = 0;
		
		double Htot = 0, Stot = 0, Vtot = 0;
		double numVals = 0;
		double[] tmpHSV;
		// Worthless mean function
		for(int r = 0; r < cutout.rows(); r++){
			for(int c = 0; c < cutout.cols(); c++){
				tmpHSV = cutout.get(r, c);
				if(tmpHSV[2] > 10){
					numVals++;
					Htot += tmpHSV[0];
					Stot += tmpHSV[1];
					Vtot += tmpHSV[2];
				}
			}
		}
		HVal = Htot/numVals;
		SVal = Stot/numVals;
		VVal = Vtot/numVals;
	
		String txtString = String.format("H = %4f", HVal);
	    Core.putText(cutout, txtString, new Point(25, 280) , Core.FONT_HERSHEY_SIMPLEX, .7, new Scalar(255, 255, 255), 2, 8, false);
	    
		txtString = String.format("S = %4f", SVal);
	    Core.putText(cutout, txtString, new Point(25, 300) , Core.FONT_HERSHEY_SIMPLEX, .7, new Scalar(255, 255, 255), 2, 8, false);
	    
		txtString = String.format("V = %4f", VVal);
	    Core.putText(cutout, txtString, new Point(25, 320) , Core.FONT_HERSHEY_SIMPLEX, .7, new Scalar(255, 255, 255), 2, 8, false);
	    
		mIntermeditateResult = cutout;
		// HSV value calculations end here
		
		
				
		if(index != -1)
			boxes.add(Imgproc.boundingRect(contours.get(index)));

		/*
		 * Find contours of img
		 * CV_RETR_LIST retrieves all of the contours without establishing any hierarchical relationships.
		 * CV_CHAIN_APPROX_SIMPLE compresses horizontal, vertical, and diagonal segments 
		 * and leaves only their end points. For example, an up-right rectangular contour is encoded with 4 points. 
		 */
		//Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		//float refArea = 0;
		//boolean objectFound = false;
		
//		Moments oMoments = Imgproc.moments(img);
//		Point pos = new Point(0,0);
//		double dM01 = oMoments.get_m01();
//		double dM10 = oMoments.get_m10();
//		double dArea = oMoments.get_m00();
//		
//		Mat returnImage = originalImage.clone();
//		if(dArea>MINIMUM_OBJECT_SIZE){
//			//calculate the position
//			double[] vals = new double[2];
//			vals[0] = dM01/dArea;
//			vals[1] = dM10/dArea;
//			pos.set(vals);
//			Core.circle(returnImage, pos, 10, new Scalar(0,0,255), 1);
//
//		}
		
		
		return boxes;
	}

	// Adds a color template and returns handler id (ArrayList id)
	public int addTemplate(String description_, int hueLow_, int hueHigh_, int saturationLow_, int saturationHigh_, int valueLow_, int valueHigh_, int form_type_){
		colorTemplates.add(new ColorTemplate(description_, hueLow_, hueHigh_, saturationLow_, saturationHigh_, valueLow_, valueHigh_, form_type_));
		return colorTemplates.size() - 1;
	}

	public void activateTemplate(int id){
		synchronized (colorTemplates) {
			if(id >= colorTemplates.size() || id < 0) return;
			colorTemplates.get(id).activate();
		}
	}

	public void deactivateTemplate(int id){
		if(id >= colorTemplates.size() || id < 0) return;
		colorTemplates.get(id).deactivate();
	}
	
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
	
}
