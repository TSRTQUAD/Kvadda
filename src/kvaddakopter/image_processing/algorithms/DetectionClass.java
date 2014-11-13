package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.List;

import kvaddakopter.ImageProcessingMain;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

//TODO calculate moments of contours to get center of mass
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
	protected ArrayList<Rect> getBoundingBoxes(List<MatOfPoint> contours, Mat hierarchy, double areaThreshold){	
		//Selecting blobs that are big enough
		ArrayList<Rect> boxes = new ArrayList<Rect>();
		for (MatOfPoint c : contours) {
			double contourArea = Imgproc.contourArea(c);
			//System.out.println(contourArea);
			if(contourArea > areaThreshold){
				boxes.add(Imgproc.boundingRect(c));
			}
		}
		return boxes;
	}
	
	/**
	 * Convertion of boundingBoxes to targetObjects
	 * @param boundingBoxes
	 * @param drawInImage
	 * @return
	 */
	protected ArrayList<TargetObject> convertToTargets(ArrayList<Rect> boundingBoxes, Mat drawInImage){
		//Initialize
		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
		
		// Convert blobs to target objects
		// Temporary solution using bounding boxes
		//System.out.println(boundingBoxes.size());
		for(int i = 0; i < boundingBoxes.size(); i++){
			Rect boundingBox = boundingBoxes.get(i);
			targetObjects.add(new TargetObject(boundingBox, 1));
			Core.rectangle(
					drawInImage, 
					new Point(boundingBox.x, boundingBox.y), 
					new Point(boundingBox.x + boundingBox.width, boundingBox.y + boundingBox.height), 
					new Scalar(255,0,0), 
					1);
		}
		return targetObjects;
	}
}
