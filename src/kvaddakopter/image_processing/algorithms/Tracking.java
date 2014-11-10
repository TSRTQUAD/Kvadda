package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.HashMap;

import kvaddakopter.image_processing.data_types.TargetObject;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import com.xuggle.xuggler.demos.VideoImage;

public class Tracking {
	Mat emptyMat; // For easier use with Core.gemm
	
	Mat H_small; 
	Mat H;
	
	HashMap<Integer, TargetObject> mInternalTargets;
	Mat x; // State matrix containing all targets state matrices
	Mat P; // Covariance matrix -||-
	
	Mat F_small; // Matrix describing the dynamics (constant acceleration)
	Mat F;
	Mat G; // Noise dependency
	Mat Q; // G*G'
	Mat R; // Measurement noise
	

	/**
	 *  <Lägg till beskrivning utav track här>
	 * 
	 * @param targetObject - List of detected targets
	 */
	
	public Tracking(){
		// Initialize matrices
		emptyMat = new Mat();
		
		// Create H_small as [1 0 0 0; 0 1 0 0]. H is then [H_s 0 ... 0; 0 H_s 0 ..0; ...; ... 0 H_s]
		H_small = Mat.zeros(2, 4, CvType.CV_64F);
		H_small.put(0, 0, 1.0);
		H_small.put(1, 1, 1.0);		
		
		H = new Mat();
		F_small = new Mat();
		
		x = new Mat(4, 1, CvType.CV_64F);
		mInternalTargets = new HashMap<Integer, TargetObject>();
		//Init window
	}
	
	
	public void update(ArrayList<TargetObject> targetObjects){
		// Create combined x and P matrices from mInternalTargets 
		//createCombinedMatrices();
		
		// Perform time update
		//timeUpdate();
		
		// Match new targetObjects with mInternalTargets by analyzing position difference and identifiers
		
		// Perform measurement update with matched objects
		//measurementUpdate(getZ(targetObjects));
		
		// Add non-matched objects to internal list
		if(mInternalTargets.size() == 0 && targetObjects.size() > 0){
			mInternalTargets.put(0, (TargetObject)targetObjects.get(0));
			System.out.print("Length of mInternalTargets: ");
			System.out.println(mInternalTargets.size());
		}
		
		if(targetObjects.size() > 0 && mInternalTargets.size() > 0){
			TargetObject oldTarget = mInternalTargets.get(0);
			TargetObject newTarget = targetObjects.get(0);
			oldTarget.setState(newTarget.getState());
		}
		
		
	}
	
	private void timeUpdate(){
		// Predicted state estimate:	x 	= F*x
		Core.gemm(F, x, 1, emptyMat, 0, x);
		
		// Predicted covariance:		P	= F*P*F' + Q
		Mat tmp = new Mat();
		Core.gemm(F.t(), P, 1, emptyMat, 0, tmp);
		Core.gemm(tmp, F.t(), 1, Q.t(), 1, P);
	}
	
	private void measurementUpdate(Mat z){
		// TODO make y = 0 where no measurments are found for the object
		// Innovation of measurement: 	y	= z-H*x 			// y = 0 where we have no matching targets
		Mat y = new Mat();
		Core.gemm(H.t(), x, -1, z, 1, y);
		
		// Covariance innovation:		S	= H*P*H' + R
		Mat S = new Mat();
		Mat tmp = new Mat();
		Core.gemm(H.t(), P, 1, emptyMat, 0, tmp);
		Core.gemm(tmp, H.t(), 1, R.t(), 1, S);
		
		// Optimal Kalman gain: 		K	= P*H'*inv(S)
		Mat K = new Mat();
		tmp = new Mat();
		Core.gemm(P.t(), H.t(), 1, emptyMat, 0, tmp);
		Core.gemm(tmp.t(), S.inv(), 1, emptyMat, 0, K);
		
		// Updated estimate:			x	= x + K*y
		Core.gemm(K.t(), y, 1, x.t(), 1, x);
		
		// Updated covariance			P	= (I - K*H)*P = P - K*H*P
		tmp = new Mat();
		Core.gemm(K.t(), H, 1, emptyMat, 0, tmp);
		Core.gemm(tmp.t(), P, -1, P, 1, P);
	}
	
	private Mat createF_small(double Ts){
		// Creates the dynamic matrix F as F_small = [1 0 Ts 0; 0 1 0 Ts; 0 0 1 0; 0 0 0 1]
		Mat res = Mat.eye(4, 4, CvType.CV_64F);
		res.put(0, 2, Ts);
		res.put(1, 3, Ts);		
		return res;
	}
	
	private Mat createQ(double Ts, double sigma_sq){
		// Creates the system noise Q = G*G'*sigma_squared
		// Given a1 = Ts^4/4, a2 = Ts^3/2, a3 = Ts^2 we have Q as follows
		// Q = [a1 a2 a1 a2; a2 a3 a2 a3; a1 a2 a1 a2; a2 a3 a2 a3]
		double a1 = Math.pow(Ts, 4) / 4;
		double a2 = Math.pow(Ts, 3) / 2;
		double a3 = Math.pow(Ts, 2);
		Mat res = new Mat(4, 4, CvType.CV_64F);
		
		res.put(0, 0, a1);
		res.put(0, 1, a2);
		res.put(0, 2, a1);
		res.put(0, 3, a2);

		res.put(1, 0, a2);
		res.put(1, 1, a3);
		res.put(1, 2, a2);
		res.put(1, 3, a3);
		
		res.put(2, 0, a1);
		res.put(2, 1, a2);
		res.put(2, 2, a1);
		res.put(2, 3, a2);

		res.put(3, 0, a2);
		res.put(3, 1, a3);
		res.put(3, 2, a2);
		res.put(3, 3, a3);
		
		Core.gemm(emptyMat, emptyMat, 0, res.t(), sigma_sq, res);
		
		return res;
	}
	
	public Mat getZ(ArrayList<TargetObject> targetObjects){
		Mat res = new Mat(targetObjects.size(), 1, CvType.CV_64F);
		for(TargetObject targetObject : targetObjects){
			Mat pos = targetObject.getPosition();
			//res.put();
		}
		
		
		
		return res;
	}
	
	public Mat getImage(int resWidth, int resHeight){
		Mat res = new Mat(resHeight, resWidth, CvType.CV_8U);
		res.setTo(new Scalar(0, 0, 0));
		if(mInternalTargets.size() > 0){
			for(int key : mInternalTargets.keySet()) {
			    TargetObject target = mInternalTargets.get(key);
			    Mat pos = target.getPosition();
				Core.rectangle(
						res, 
						new Point(pos.get(0, 0)[0] - 10, pos.get(1, 0)[0] - 10),
						new Point(pos.get(0, 0)[0] + 10, pos.get(1, 0)[0] + 10),
						new Scalar(255, 0, 0), 
						1);

				String txtString = String.format("ID:%d", key);
			    Core.putText(res, txtString, new Point(pos.get(0, 0)[0] - 12, pos.get(1, 0)[0] + 22) , Core.FONT_HERSHEY_SIMPLEX, .4, new Scalar(255, 255, 255), 1, 8, false);
			}
		}
		
		return res;
	}
	
}
