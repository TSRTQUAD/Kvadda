package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import kvaddakopter.image_processing.data_types.TargetObject;

public class Tracking {
	
	HashMap<Integer, TargetObject> mInternalTargets;
	
	/**
	 *  <Lägg till beskrivning utav track här>
	 * 
	 * @param targetObject - List of detected targets
	 */
	public void update(ArrayList<TargetObject> targetObjects){
		// Perform time update
		
		// Match new targetObjects with mInternalTargets by analyzing position difference and identifiers
		
		// Perform measurement update with matched objects
		
		// Add non-matched objects to internal list
	}
	
	private void timeUpdate(){
		// Predicted state estimate:	x 	= F*x
		// Predicted covariance:		P	= F*P*F' + Q
	}
	
	private void measurementUpdate(){
		// Innovation of measurement: 	y	= z-H*x
		// Covariance innovation:		S	= H*P*H' + R
		// Optimal Kalman gain: 		K	= P*H'*inv(S)
		// Updated estimate:			x	= x + K*y
		// Updated covariance			P	= (I - K*H)*P
	}
	
	
	
}
