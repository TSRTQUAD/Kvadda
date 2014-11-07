package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.List;

import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class DetectionClass {
	Mat mIntermeditateResult;
	
	public ArrayList<TargetObject> start(ImageObject imageObject){
		return null;
	};
	
	public Mat getIntermediateResult(){
		return mIntermeditateResult;
	}
	
	public boolean hasIntermediateResult(){
		return mIntermeditateResult != null && !mIntermeditateResult.empty();
	}
	
	/**
	 * 
	 * @param binaryImage
	 * @param areaThreshold
	 * @return
	 */
	protected ArrayList<Rect> getBoundingBoxes(Mat binaryImage, double areaThreshold){

		
		//Using openCV findContours-routine to get pixel coordinates of the current blobs.
		
		//Parameters ( and return values) for the findContour
		Mat hierarchy  = new Mat();
		Mat contourImage = binaryImage.clone(); // remove clone 
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		
		Imgproc.findContours(contourImage, contours, hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		
		//Selecting the largest blob

		ArrayList<Rect> boxes = new ArrayList<Rect>();
		for (MatOfPoint c : contours) {
			double contourArea = Imgproc.contourArea(c);
			if(contourArea > areaThreshold){
				boxes.add(Imgproc.boundingRect(c));	
			}
		}
		return boxes;
	}
	
		
	
		
}
