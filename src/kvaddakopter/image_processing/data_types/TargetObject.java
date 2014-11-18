package kvaddakopter.image_processing.data_types;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Rect;

public class TargetObject {
	private SimpleMatrix x; // State [x1, x2, dot(x1), dot(x2)]'
	private SimpleMatrix P; // Covariance
	// private Identifier identifier; // Holds identification info about the target
	
	public TargetObject(SimpleMatrix position_, float noise_level){
		// Create the state matrix with given position measurements
		x = new SimpleMatrix(4, 1, true, position_.get(0, 0), position_.get(1, 0), 0, 0);
		
		// Create the covariance matrix with noise_level on diagonal 
		P = SimpleMatrix.diag(1, 1, 3, 3);
		P.scale(noise_level);
	}
	
	public TargetObject(Rect boundingBox, float noise_level){
		// Create the state matrix with given position measurements
		x = new SimpleMatrix(4, 1, true, boundingBox.x + boundingBox.width / 2, boundingBox.y + boundingBox.height / 2, 0, 0);
		
		// Create the covariance matrix with noise_level on diagonal 
		P = SimpleMatrix.diag(1, 1, 3, 3);
		P = P.scale(noise_level);
	}
	
	public SimpleMatrix getState(){
		return x;
	}
	
	public SimpleMatrix getCovariance(){
		return P;
	}
	
	public void setState(SimpleMatrix state_){
		x = state_;
	}
	
	public void setCovariance(SimpleMatrix covariance_){
		P = covariance_;
	}
	
	public SimpleMatrix getPosition(){
		return new SimpleMatrix(2, 1, true, x.get(0, 0), x.get(1, 0));
	}
	
	public SimpleMatrix getVelocity(){
		return new SimpleMatrix(2, 1, true, x.get(3, 0), x.get(4, 0));
	}
	
	public double getSpeed(){
		return Math.sqrt(Math.pow(x.get(2, 0), 2) + Math.pow(x.get(3, 0), 2));
	}
}
