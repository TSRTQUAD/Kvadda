package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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

	SimpleMatrix H; // Measurments from state matrix
	SimpleMatrix F; // Matrix describing the dynamics (constant acceleration)
	SimpleMatrix Q; // G*G'
	SimpleMatrix R; // Measurement noise
	
	double Ts = 0.15;
	double sigmaSquared = 50;

	CircularFifoQueue<double[]> trajectoryX = new CircularFifoQueue<double[]>(30);
	CircularFifoQueue<double[]> trajectoryM = new CircularFifoQueue<double[]>(30);
	long lastTime = 0;
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
		R = SimpleMatrix.diag(1, 1).scale(50);
		
		// Initialize target list
		mInternalTargets = new HashMap<Integer, TargetObject>();
	}
	
	
	public void update(ArrayList<TargetObject> targetObjects){
		// Elapsed time since last update
		double elapsedTime = ((double)(System.currentTimeMillis() - lastTime)) / 1000;
		lastTime = System.currentTimeMillis();
		updateTimeDependentMatrices(elapsedTime);
		
		
		// Perform time update
		timeUpdate();
		
		if(mInternalTargets.size() > 0){
			trajectoryX.add(new double[]{mInternalTargets.get(0).getPosition().get(0, 0), mInternalTargets.get(0).getPosition().get(1, 0)});
		}
		
		// Match new targetObjects with mInternalTargets by analyzing position difference and identifiers
		matchTargets(targetObjects);
		
		// Perform measurement update with matched objects
		
		// DEBUG!
		if(targetObjects.size() > 0 && mInternalTargets.size() > 0){
			SimpleMatrix z = new SimpleMatrix(2, 1, true, targetObjects.get(0).getPosition().get(0, 0), targetObjects.get(0).getPosition().get(1, 0));
			measurementUpdate(mInternalTargets.get(0), z);
			trajectoryM.add(new double[]{z.get(0, 0), z.get(1, 0)});
		} else {
			trajectoryM.add(new double[]{0, 0});
		}
		//measurementUpdate(getZ(targetObjects));
		
		// Add non-matched objects to internal list
		if(mInternalTargets.size() == 0 && targetObjects.size() > 0){
			mInternalTargets.put(0, (TargetObject)targetObjects.get(0));
			System.out.print("Length of mInternalTargets: ");
			System.out.println(mInternalTargets.size());
			
		}
		
	}
	
	private void matchTargets(ArrayList<TargetObject> targetObjects) {
		// Match targetObjects against mInternalTargets
		
		
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
	
	private void measurementUpdate(TargetObject target, SimpleMatrix z){
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
	
	public SimpleMatrix getZ(ArrayList<TargetObject> targetObjects){
		SimpleMatrix res = new SimpleMatrix();/*Mat(targetObjects.size(), 1, CvType.CV_64F);
		for(TargetObject targetObject : targetObjects){
			Mat pos = targetObject.getPosition();
			//res.put();
		}*/
			
		
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

			    SimpleMatrix x = target.getState();
			    String stateString = String.format("X:%1$dn Y:%2$d, nX:%3$d, nY:%4$d.", (int)x.get(0,0), (int)x.get(1,0), (int)x.get(2,0), (int)x.get(3,0));
			    Core.putText(res, stateString, new Point(20, 20) , Core.FONT_HERSHEY_SIMPLEX, .4, new Scalar(255, 255, 255), 1, 8, false);
			}
		}
		
		return res;
	}
	
}
