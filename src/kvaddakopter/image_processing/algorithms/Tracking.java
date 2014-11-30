package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import kvaddakopter.communication.QuadData;
import kvaddakopter.image_processing.data_types.Identifier;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.maps.GPSCoordinate;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class Tracking {	
	ArrayList<TargetObject> mInternalTargets;
	int highestKey = -1;
	
	SimpleMatrix H; // Measurments from state matrix
	SimpleMatrix F; // Matrix describing the dynamics (constant acceleration)
	SimpleMatrix Q; // G*G'
	
	double Ts = 0.15;
	double sigmaSquared = 200;

	//CircularFifoQueue<double[]> trajectoryX = new CircularFifoQueue<double[]>(30);
	//CircularFifoQueue<double[]> trajectoryM = new CircularFifoQueue<double[]>(30);
	long lastTime;
	
	boolean debugPrintMatches = false;
	
	/**
	 * Constructor which initializes lastTime as current time.
	 */
	public Tracking(){
		// Initialize matrices
		H = new SimpleMatrix(2, 4, true, 
				1, 0, 0, 0, 
				0, 1, 0, 0);
		
		// Initialize target list
		mInternalTargets = new ArrayList<TargetObject>();
		lastTime = System.currentTimeMillis();
		
		// Debug
		mInternalTargets.add(new TargetObject(new SimpleMatrix(2, 1, true, 0, 0), 50, null));
		mInternalTargets.get(0).setGPSCoordinate(new GPSCoordinate(58.40708, 15.62126));
	}
	

	/**
	 * Updates the tracker by running time update in the Kalman filter, matching tracked targets with 
	 * incoming targetObjects and then running measurement update in the Kalman filter. 
	 * 
	 * @param targetObjects ArrayList of type {@link TargetObject} with new observations of targets.
	 * @see TargetObject
	 * @see #measurementUpdate
	 */
	public void update(ArrayList<TargetObject> targetObjects, QuadData qData){
		// Elapsed time since last update  
		double elapsedTime = ((double)(System.currentTimeMillis() - lastTime)) / 1000;
		lastTime = System.currentTimeMillis();
		// Updates matrices F and Q that are time dependent
		updateTimeDependentMatrices(elapsedTime);
		
		// Perform time update
		timeUpdate();
		
		// If no targets are observed, skip matching and measurement update and return instead
		if(targetObjects.size() == 0) return;
		
		// Match new targetObjects with mInternalTargets by analyzing position difference and identifiers
		int[] matchedIDs = matchTargets(targetObjects);
		
		// Perform measurement update with matched objects
		for(int i = 0; i < matchedIDs.length; i++){
			if(matchedIDs[i] == -1){
				// Create new target
				TargetObject newTarget = targetObjects.get(i);
				newTarget.setID(++highestKey);
				mInternalTargets.add(newTarget);
				continue;
			}
			// Extract position measurment and noise from measurments
			SimpleMatrix z = H.mult(targetObjects.get(i).getState());
			SimpleMatrix R = (H.mult(targetObjects.get(i).getCovariance())).mult(H.transpose());
			// Perform measurement update on target
			measurementUpdate(TargetObject.getTargetByID(mInternalTargets, matchedIDs[i]), z, R);
			
			
			// TODO Add optinal trajectories again for debugging purposes
		}
		
		if(qData != null){
			estimateGeo(qData);
		}
		
		// Remove old internal targets with too high covariance
		// If ||P|| > threshold we remove the target from tracked targets
		Iterator<TargetObject> iter = mInternalTargets.iterator();
		while(iter.hasNext()){
		    TargetObject target = iter.next();
			if(target.getCovariance().normF() > 1000){
				iter.remove();
				System.out.println("Tracking: Removing target");
		    }
		}
	}

	/**
	 * Performs the Kalman filter time update aka. prediction step on all tracked targets.
	 */
	private void timeUpdate(){
		for(TargetObject target : mInternalTargets){
			// Get state and covariance
			SimpleMatrix x = target.getState();
			SimpleMatrix P = target.getCovariance();
			
			// Predicted state estimate:	x 	= F*x
			x = F.mult(x);
			
			// Predicted covariance:		P	= F*P*F' + Q
			P = (F.mult(P)).mult(F.transpose()).plus(Q);
			// Make sure P is symmetric
			P = (P.plus(P.transpose())).scale(.5);
			
			// Update state and covariance	
			target.setState(x);
			target.setCovariance(P);
		}
	}
	
	/**
	 * Performs the Kalman filter measurement update aka. update step on a single target.
	 * @param target 	The tracked target that is observed.
	 * @param z 		The observed measurement.
	 * @param R 		The covariance of the measurement.
	 */
	private void measurementUpdate(TargetObject target, SimpleMatrix z, SimpleMatrix R){ 
		SimpleMatrix x = target.getState();
		SimpleMatrix P = target.getCovariance();
		
		// Innovation of measurement: 	y	= z-H*x
		SimpleMatrix y = z.minus(H.mult(x));
		
		// Covariance innovation:		S	= H*P*H' + R
		SimpleMatrix S = ((H.mult(P)).mult(H.transpose())).plus(R);
		
		// Optimal Kalman gain: 		K	= P*H'*inv(S)
		SimpleMatrix K = (P.mult(H.transpose()).mult(S.pseudoInverse()));
		
		// Updated estimate:			x	= x + K*y
		x = x.plus(K.mult(y));
		
		// Updated covariance			P	= (I - K*H)*P = P - K*H*P
		P = P.minus((K.mult(H)).mult(P));
		// Make sure P is symmetric
		P = (P.plus(P.transpose())).scale(.5);

		target.setState(x);
		target.setCovariance(P);
	}
	
	/**
	 * Matches observed targets with tracked targets.
	 * @param targetObjects
	 * @return List of tracked target IDs for each observed target or -1 if no match is good enough.
	 * @see #compareDistance(TargetObject, TargetObject) compareDistance which compares two positions with regard to covariance.
	 * @see Identifier#compare(Identifier, Identifier) which compares two identifiers.
	 */
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
		
		
		
		// Enables debug output of table of matches
		if(debugPrintMatches){
			System.out.print("   |");
			for(TargetObject target : targetObjects){
				System.out.format("%4d|", targetObjects.indexOf(target));
			}
			System.out.println();
		}		
		
		
		// For each entry in the matching table, set matching value as a combination of methods Identigier.compare and compareDistance.
		for(TargetObject internalTarget : mInternalTargets){
			if(debugPrintMatches) System.out.format("|%2d|", internalTarget.getID());
			for(TargetObject target : targetObjects){
				
				matchTable[internalTarget.getID()][targetObjects.indexOf(target)] = 
						(Identifier.compare(internalTarget.getIdentifier(), target.getIdentifier().setmeanHSVValuesCertainty(1)) + 
						compareDistance(internalTarget, target)) / 2;
				if(debugPrintMatches) System.out.format("%.2f|", matchTable[internalTarget.getID()][targetObjects.indexOf(target)]);
			}
			if(debugPrintMatches) System.out.println();
		}
		if(debugPrintMatches) System.out.println();
		
		
		
		
		// For each observed target. Say the highest likely match is a match if the likelihood is large enough.
		// Each internal target could in that way be updated with multiple observed targets.
		// If best matched value is lower than matchThreshold then the initial -1 is not overwritten by tracked target ID. 
		for(int i = 0; i < numTargets; i++){
			float bestMatchVal = 0;
			int bestMatchId = -1;
			for(int keyI = 0; keyI < numInternalTargets; keyI++){
				if(matchTable[keyI][i] > bestMatchVal){
					bestMatchVal = matchTable[keyI][i];
					bestMatchId = mInternalTargets.get(keyI).getID();
				}
			}
			if(bestMatchVal > matchThreshold){
				matchedIDs[i] = bestMatchId;
			}
		}
		
		return matchedIDs;
	}

	/**
	 * Compares the matching probability by comparing the distance between two objects and their uncertainty. 
	 * @param internalTarget 	The tracked target.
	 * @param target			The observed target.
	 * @return					The matching probability. 
	 */
	private float compareDistance(TargetObject internalTarget, TargetObject target) {
		// Compares distance between objects with regard of covariance
		// XXX uncertainDistance only depends on one axis of the covariance
		// TODO calculate eigen values for the covariance to solve the uncertainDistance directions.

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

	/**
	 * Updates the time dependent matrices F and Q for the Kalman filter.
	 * @param elapsedTime
	 */
	private void updateTimeDependentMatrices(double elapsedTime) {
		// Updates matrices F and Q
		F = createF(elapsedTime);
		Q = createQ(elapsedTime, sigmaSquared);
	}

	/**
	 * Creates the matrix F for the Kalman filter which describes the dynamics of the system.
	 * @param Ts	Time passed since last update.
	 * @return F
	 */
	private SimpleMatrix createF(double Ts){
		// Creates the dynamic matrix F as FSmall = [1 0 Ts 0; 0 1 0 Ts; 0 0 1 0; 0 0 0 1]
		return new SimpleMatrix(4, 4, true,
				1, 0, Ts, 0,
				0, 1, 0, Ts,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}
	
	/**
	 * Creates the noise matrix Q for the Kalman filter which describes the noise effect on the system.
	 * @param Ts			Time passed since last update.
	 * @param sigmaSquared	System noise.
	 * @return Q
	 */
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
	
	
	/**
	 * A temporary function for estimating the targets geo coordinates by estimating angles in
	 * the image and projecting the target to the ground.
	 * @param qData
	 */
	private void estimateGeo(QuadData qData){
		for(TargetObject target : mInternalTargets) {
			double fullYAngle = 45.1040; // Field of view in vertical
			double fullYPixels = 180; // half the image pixel height
			double yAngle = Math.atan(((fullYPixels - target.getPosition().get(1,0)) / fullYPixels) * Math.tan(Math.toRadians(fullYAngle / 2)));
			double yDist = qData.getAltitude() * Math.tan(Math.toRadians(qData.getPitch()) + yAngle);

			double fullXAngle = 80.1849; // Field of view in horizontal
			double fullXPixels = 320; // Half the image pixel width
			double xAngle = Math.atan(((target.getPosition().get(0,0) - fullXPixels) / fullXPixels) * Math.tan(Math.toRadians(fullXAngle / 2)));
			double xDist = qData.getAltitude() * Math.tan(Math.toRadians(-qData.getRoll()) + xAngle);
			
			double latDist = -xDist*Math.cos(Math.toRadians(qData.getYaw())) + yDist*Math.sin(Math.toRadians(qData.getYaw()));
			double lonDist = xDist*Math.sin(Math.toRadians(qData.getYaw())) + yDist*Math.cos(Math.toRadians(qData.getYaw()));
			
			double linearizedLat = 0.0000091796; // Degrees per meter
			double linearizedLon = 0.000017198; // Degrees per meter
			
			target.setGPSCoordinate(new GPSCoordinate(qData.getGPSLat() + latDist * linearizedLat, qData.getGPSLong() + lonDist * linearizedLon));
		}
	}
	
	/**
	 * Overlays tracking information with tracked targets on inputed image res. The covariance is
	 * currently displayed to only work with equal amount of uncertainty in both axis.
	 * @param res	The input image to be overlayed.
	 * @return 		The resulting image.
	 */
	public Mat getImage(int w_, int h_, Mat res){
		// TODO: Remove w_ and h_ in method call
		if(mInternalTargets.size() > 0){
			for(TargetObject target : mInternalTargets) {
			    SimpleMatrix pos = target.getPosition();
				SimpleMatrix cov = target.getCovariance();
				
				// Draw ellipse corresponding to the covariance of the position
				// TODO Calculate eigenvectors and eigenvalues to visualize the covariance correctly
				Core.ellipse(res, new Point(pos.get(0, 0), pos.get(1, 0)), new Size(new double[]{cov.get(0, 0), cov.get(1, 1)}), 0.0, 0.0, 360.0, new Scalar(255, 255, 255));

				// Draw ID information
				String txtString = String.format("ID:%d", target.getID());
			    Core.putText(res, txtString, new Point(pos.get(0, 0) - 12, pos.get(1, 0) + 22) , Core.FONT_HERSHEY_SIMPLEX, .4, new Scalar(255, 255, 255), 1, 8, false);
			}
		}
		
		return res;
	}
	
	/**
	 * Returns hash map with tracked targets
	 * @return tracked targets
	 */
	public ArrayList<TargetObject> getTargets(){
		return mInternalTargets;
	}
}
