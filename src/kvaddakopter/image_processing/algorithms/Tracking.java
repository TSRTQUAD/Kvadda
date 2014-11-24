package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import kvaddakopter.image_processing.data_types.Identifier;
import kvaddakopter.image_processing.data_types.TargetObject;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class Tracking {	
	HashMap<Integer, TargetObject> mInternalTargets;	
	int highestKey = -1;
	
	SimpleMatrix H; // Measurments from state matrix
	SimpleMatrix F; // Matrix describing the dynamics (constant acceleration)
	SimpleMatrix Q; // G*G'
	
	double Ts = 0.15;
	double sigmaSquared = 50;

	CircularFifoQueue<double[]> trajectoryX = new CircularFifoQueue<double[]>(30);
	CircularFifoQueue<double[]> trajectoryM = new CircularFifoQueue<double[]>(30);
	long lastTime = 0;
	
	boolean debugPrintMatches = false;
	/**
	 *  <Lägg till beskrivning utav track här>
	 * 
	 * @param targetObject - List of detected targets
	 */
	
	public Tracking(){
		// Initialize matrices
		H = new SimpleMatrix(2, 4, true, 
				1, 0, 0, 0, 
				0, 1, 0, 0);
		
		// Initialize target list
		mInternalTargets = new HashMap<Integer, TargetObject>();
	}
	
	
	public void update(ArrayList<TargetObject> targetObjects){
		// Elapsed time since last update
		double elapsedTime = ((double)(System.currentTimeMillis() - lastTime)) / 1000;
		lastTime = System.currentTimeMillis();
		updateTimeDependentMatrices(elapsedTime);
		System.out.println(elapsedTime);
		
		// Perform time update
		timeUpdate();
		
		/*if(mInternalTargets.size() > 0){
			trajectoryX.add(new double[]{mInternalTargets.get(0).getPosition().get(0, 0), mInternalTargets.get(0).getPosition().get(1, 0)});
		}*/
		
		if(targetObjects.size() == 0) return;
		
		// Match new targetObjects with mInternalTargets by analyzing position difference and identifiers
		int[] matchedIDs = matchTargets(targetObjects);
		
		// Perform measurement update with matched objects
		for(int i = 0; i < matchedIDs.length; i++){
			if(matchedIDs[i] == -1){
				// Create new target
				mInternalTargets.put(++highestKey, targetObjects.get(i));
				continue;
			}
			// Extract position measurment and noise from measurments
			SimpleMatrix z = H.mult(targetObjects.get(i).getState());
			SimpleMatrix R = (H.mult(targetObjects.get(i).getCovariance())).mult(H.transpose());
			measurementUpdate(mInternalTargets.get(matchedIDs[i]), z, R);
		}
		
		// Remove old internal targets with too high covariance
		// If P > threshold we remove the target from internal targets
		Iterator<Map.Entry<Integer, TargetObject>> iter = mInternalTargets.entrySet().iterator();
		while(iter.hasNext()){
		    Map.Entry<Integer, TargetObject> entry = iter.next();
			if(entry.getValue().getCovariance().normF() > 1000){
				iter.remove();
		    }
		}		
	}
	
	private int[] matchTargets(ArrayList<TargetObject> targetObjects) {
		// Match targetObjects against mInternalTargets
		// Create a table with mInternalTargets and targetObjects
		int numInternalTargets = mInternalTargets.size();
		int numTargets = targetObjects.size();
		int[] matchedIDs = new int[numTargets];
		Arrays.fill(matchedIDs, -1);
		
		// If no internal targets exist, return -1 array.
		if(numInternalTargets == 0) return matchedIDs; 
		
		
		float matchThreshold = 0.3f;
		float[][] matchTable = new float[numInternalTargets][numTargets];
		
		
		
		// Print table
		if(debugPrintMatches){
			System.out.print("   |");
			for(TargetObject target : targetObjects){
				System.out.format("%4d|", targetObjects.indexOf(target));
			}
			System.out.println();
		}
		
		ArrayList<Integer> keyArray = new ArrayList<Integer>(mInternalTargets.keySet());
		for(int keyI = 0; keyI < numInternalTargets; keyI++){
			if(debugPrintMatches) System.out.format("|%2d|", keyArray.get(keyI));
			for(TargetObject target : targetObjects){
				TargetObject internalTarget = mInternalTargets.get(keyArray.get(keyI));
				
				matchTable[keyI][targetObjects.indexOf(target)] = 
						(Identifier.compare(internalTarget.getIdentifier(), target.getIdentifier().setmeanHSVValuesCertainty(1)) + 
						compareDistance(internalTarget, target)) / 2;
				if(debugPrintMatches) System.out.format("%.2f|", matchTable[keyI][targetObjects.indexOf(target)]);
			}
			if(debugPrintMatches) System.out.println();
		}
		if(debugPrintMatches) System.out.println();
		
		
		
		
		// For each target found. Say the highest likely match is a match if the likelihood is large enough.
		// Each internal target could in that way be updated with multiple new targets.
		for(int i = 0; i < numTargets; i++){
			float bestMatchVal = 0;
			int bestMatchId = -1;
			for(int keyI = 0; keyI < numInternalTargets; keyI++){
				if(matchTable[keyI][i] > bestMatchVal){
					bestMatchVal = matchTable[keyI][i];
					bestMatchId = keyArray.get(keyI);
				}
			}
			if(bestMatchVal > matchThreshold){
				matchedIDs[i] = bestMatchId;
			}
		}
		
		return matchedIDs;
	}


	private float compareDistance(TargetObject internalTarget, TargetObject target) {
		// Compares distance between objects with regard of covariance
		// XXX uncertainDistance only depends on one axis of the covariance

		// For now use distances converted from pixels 
		double uncertainDistance = (.1) * (float)(internalTarget.getCovariance().get(0, 0) + target.getCovariance().get(0, 0));
		double distance = (float)Math.sqrt(
				Math.pow((0.01) * (internalTarget.getPosition().get(0, 0) - target.getPosition().get(0, 0)), 2) + 
				Math.pow((0.01) * (internalTarget.getPosition().get(1, 0) - target.getPosition().get(1, 0)), 2));
		
		// Comparation of distances and uncertainties
		double retVal = 0;
		
		// Uncertain distance effect
		double scale = 0.5f;
		retVal = (Math.exp((double)(scale * (-uncertainDistance) - 2)) *
				Math.pow((double)((scale * (-uncertainDistance) - 2)),2) / 
				(Math.exp(-2) * 4));
		
		// Ratio between distance and uncertain distance
		scale = 1.7f;
		retVal *= Math.max(0, (1 - Math.pow(((distance / uncertainDistance) / scale), 2)));
		
		return (float)retVal;
	}


	private void updateTimeDependentMatrices(double elapsedTime) {
		// Updates matrices F and Q
		F = createF(elapsedTime);
		Q = createQ(elapsedTime, sigmaSquared);
	}

	private void timeUpdate(){ // TODO: Variable time updates
		for(Entry<Integer, TargetObject> entry: mInternalTargets.entrySet()){
			// Get state and covariance
			TargetObject target = entry.getValue();
			SimpleMatrix x = target.getState();
			SimpleMatrix P = target.getCovariance();
			
			// Predicted state estimate:	x 	= F*x
			x = F.mult(x);
			
			// Predicted covariance:		P	= F*P*F' + Q
			P = (F.mult(P)).mult(F.transpose()).plus(Q);
			
			// Update state and covariance	
			target.setState(x);
			target.setCovariance(P);
		}
	}
	
	private void measurementUpdate(TargetObject target, SimpleMatrix z, SimpleMatrix R){
		SimpleMatrix x = target.getState();
		SimpleMatrix P = target.getCovariance();
		
		// TODO make y = 0 where no measurments are found for the object
		// Innovation of measurement: 	y	= z-H*x 			// y = 0 where we have no matching targets
		SimpleMatrix y = z.minus(H.mult(x));
		
		// Covariance innovation:		S	= H*P*H' + R
		SimpleMatrix S = ((H.mult(P)).mult(H.transpose())).plus(R);
		
		// Optimal Kalman gain: 		K	= P*H'*inv(S)
		SimpleMatrix K = (P.mult(H.transpose()).mult(S.pseudoInverse()));
		
		// Updated estimate:			x	= x + K*y
		x = x.plus(K.mult(y));
		
		// Updated covariance			P	= (I - K*H)*P = P - K*H*P
		P = P.minus((K.mult(H)).mult(P));

		// DEBUG!
		target.setState(x);
		target.setCovariance(P);
	}

	private SimpleMatrix createF(double Ts){
		// Creates the dynamic matrix F as FSmall = [1 0 Ts 0; 0 1 0 Ts; 0 0 1 0; 0 0 0 1]
		return new SimpleMatrix(4, 4, true,
				1, 0, Ts, 0,
				0, 1, 0, Ts,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}
	
	private SimpleMatrix createQ(double Ts, double sigmaSquared){
		// Creates the system noise Q = G*G'*sigma_squared
		// Given a1 = Ts^4/4, a2 = Ts^3/2, a3 = Ts^2 we have Q as follows
		double a1 = Math.pow(Ts, 4) / 4;
		double a2 = Math.pow(Ts, 3) / 2;
		double a3 = Math.pow(Ts, 2);
		SimpleMatrix res = new SimpleMatrix(4, 4, true, 
				a1, 0, a2, 0,
				0, a1, 0, a2,
				a2, 0, a3, 0,
				0, a2, 0, a3);
		res.scale(sigmaSquared);
		return res;
	}
	
	public Mat getImage(int w_, int h_, Mat res){
		//Mat res = new Mat(w_, h_, CvType.CV_8U);
		//res.setTo(new Scalar(0, 0, 0));
		if(mInternalTargets.size() > 0){
			for(int key : mInternalTargets.keySet()) {
			    TargetObject target = mInternalTargets.get(key);
			    
			    // Draw trajectory
			    Object[] trajs = trajectoryX.toArray();
			    for(int i = 1; i < trajs.length; i++){
			    	Core.line(
			    			res, 
			    			new Point(((double[])trajs[i - 1])[0], ((double[])trajs[i - 1])[1]), 
			    			new Point(((double[])trajs[i])[0], ((double[])trajs[i])[1]), 
			    			new Scalar(0, 200, 0));
			    }

			    // Draw measurements
			    Object[] measurements = trajectoryM.toArray();
			    for(int i = 1; i < measurements.length; i++){
			    	Core.circle(
			    			res, 
			    			new Point(((double[])measurements[i - 1])[0], ((double[])measurements[i - 1])[1]), 
			    			3, 
			    			new Scalar(0, 0, 255));
			    }
			    
			    SimpleMatrix pos = target.getPosition();
				Core.rectangle(
						res, 
						new Point(pos.get(0, 0) - 10, pos.get(1, 0) - 10),
						new Point(pos.get(0, 0) + 10, pos.get(1, 0) + 10),
						new Scalar(255, 0, 0), 
						1);
				
				SimpleMatrix cov = target.getCovariance();
				Core.ellipse(res, new Point(pos.get(0, 0), pos.get(1, 0)), new Size(new double[]{cov.get(0, 0), cov.get(1, 1)}), 0.0, 0.0, 360.0, new Scalar(255, 255, 255));

				String txtString = String.format("ID:%d", key);
			    Core.putText(res, txtString, new Point(pos.get(0, 0) - 12, pos.get(1, 0) + 22) , Core.FONT_HERSHEY_SIMPLEX, .4, new Scalar(255, 255, 255), 1, 8, false);
			}
		}
		
		return res;
	}
	
}
