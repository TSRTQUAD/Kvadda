package kvaddakopter.image_processing.data_types;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class TargetObject {
	private Mat x; // State [x1, x2, dot(x1), dot(x2)]'
	private Mat P; // Covariance
	// private Identifier identifier; // Holds identification info about the target
	
	public TargetObject(Mat position_, float noise_level){
		// Create the state matrix with given position measurements
		x = Mat.zeros(4, 1, CvType.CV_64F);
		x.put(0, 0, position_.get(0, 0));
		x.put(1, 0, position_.get(1, 0));
		
		// Create the covariance matrix with noise_level on diagonal 
		P = Mat.eye(4, 4, CvType.CV_32F);
		P.mul(P, noise_level);
	}
	
	public TargetObject(Rect boundingBox, float noise_level){
		// Create the state matrix with given position measurements
		x = Mat.zeros(4, 1, CvType.CV_64F);
		x.put(0, 0, boundingBox.x);
		x.put(1, 0, boundingBox.y);
		
		// Create the covariance matrix with noise_level on diagonal 
		P = Mat.eye(4, 4, CvType.CV_32F);
		P.mul(P, noise_level);
	}
	
	public Mat getState(){
		return x;
	}
	
	public Mat getCovariance(){
		return P;
	}
	
	public void setState(Mat state_){
		x = state_;
	}
	
	public void setCovariance(Mat covariance_){
		P = covariance_;
	}
	
	public Mat getPosition(){
		Mat res = new Mat(2, 1, CvType.CV_64F);
		res.put(0, 0, x.get(0, 0));
		res.put(1, 0, x.get(1, 0));
		return res;
	}
	
	public Mat getVelocity(){
		return x.rowRange(2, 3);
	}
	
	public double getSpeed(){
		return Math.sqrt(Math.pow(x.get(2, 0)[0], 2) + Math.pow(x.get(3, 0)[0], 2));
	}
}
