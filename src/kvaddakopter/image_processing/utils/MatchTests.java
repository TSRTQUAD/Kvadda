package kvaddakopter.image_processing.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;

import org.opencv.features2d.DMatch;
import org.opencv.features2d.KeyPoint;


public class MatchTests {

	static final double RATIO_THRESHOLD = 0.8;
	/**
	 * This function test 
	 * @param matchesList
	 */
	public static void ratioTest(List<MatOfDMatch> matchesList){


		int numElements = -1;
		DMatch[] matchArray;

		for (Iterator<MatOfDMatch> iterator = matchesList.iterator(); iterator.hasNext();) {

			MatOfDMatch matOfDMatch = (MatOfDMatch) iterator.next();
			// Converting mat to array
			matchArray = matOfDMatch.toArray();

			// Number of elements in array
			numElements = matchArray.length;

			// We are expecting two elements / neighbor matches to be able to compare distances
			if(numElements >  1){
				double distanceRatio = matchArray[0].distance/matchArray[1].distance;
				if(distanceRatio > RATIO_THRESHOLD)
					iterator.remove();	
			}else{
				// There are less than two neighbor, current match do not pass the 
				// ratio test due we cannot compare distances.
				iterator.remove();
			}
		}


	}
	/**
	 * This function performs a test of the symmetry between two sets of matches. 
	 * 
	 * @param matchesList1 - Matches found when matching Img1 -> Img2 eg. Current frame -> Previous Frame) <br>
	 * @param matchesList2 - Matches found when matching Img2 -> Img1 eg. (Previous frame -> Current Frame) <br>
	 * @return
	 */
	public static MatOfDMatch symmetryTest(List<MatOfDMatch> matchesList1, List<MatOfDMatch> matchesList2){
		int numElements = -1;
		DMatch[] matchArray1,matchArray2;

		List<DMatch> passedMatches = new ArrayList<DMatch>();

		for (Iterator<MatOfDMatch> iterator1 = matchesList1.iterator(); iterator1.hasNext();) {

			MatOfDMatch matOfDMatch1 = (MatOfDMatch) iterator1.next();
			// Converting mat to array
			matchArray1 = matOfDMatch1.toArray();

			// Number of elements in array
			numElements = matchArray1.length;

			// If less than to two matches no result added to result for the current index
			if(numElements <  2)
				continue;

			for (Iterator<MatOfDMatch> iterator2 = matchesList2.iterator(); iterator2.hasNext();) {

				MatOfDMatch matOfDMatch2 = (MatOfDMatch) iterator2.next();
				// Converting mat to array
				matchArray2 = matOfDMatch2.toArray();

				//Same check for the second list
				numElements = matchArray2.length;
				if(numElements <  2)
					continue;

				// Test symmetry, the backward- and forward match indices must correspond
				// to pass this test.				
				if(
						matchArray1[0].queryIdx == matchArray2[0].trainIdx &&
						matchArray1[0].trainIdx == matchArray2[0].queryIdx
						){
					passedMatches.add(matchArray1[0]);
				}
			}
		}
		//Finally converting into a MatOfDMatch.
		DMatch[] resultArray = new DMatch[passedMatches.size()]; 
		passedMatches.toArray(resultArray); //(DMatch[]) passedMatches.toArray();
		MatOfDMatch resultMat = new MatOfDMatch(resultArray);
		return resultMat;

	}
	/**
	 * Computes the fundamental matrix using openCV function "Calib3d.findFundamentalMat". <br>
	 * <br>
	 * Also return a MatOfMatch-object holding matches that fulfill: <br>  
	 * x_1 * F * x_2' < e <br> 
	 * where <br>
	 * x_1 and x_2 are two corresponding keypoints,
	 * F is the fundamental matrix <br>
	 * e is and epsilon ( a small value)
	 * 
	 * @param inMatches  Input matches
	 * @param keypoints1 Keypoints from frame1
	 * @param keypoints2 Keypoints from frame2
	 * @param epsilon threshold for determine inlier matches.
	 * @param outMatches Output, returning passed matches after the ransac test.
	 * @return Fundamental matrix
	 */
	public static Mat computeFundamentalMatrix(MatOfDMatch inMatches, MatOfKeyPoint keypoints1,MatOfKeyPoint keypoints2,double epsilon,MatOfDMatch outMatches){

		//Converting to arrays
		DMatch[] matchArray = inMatches.toArray();
		int numMatches = matchArray.length;

		KeyPoint[] kp1Array = keypoints1.toArray();
		KeyPoint[] kp2Array = keypoints2.toArray();

		Point[] points1 = new Point[numMatches];
		Point[] points2 = new Point[numMatches];

		int kpIndex;
		double x,y;


		for (int i = 0; i < numMatches; i++) {

			kpIndex = matchArray[i].queryIdx;
			x = kp1Array[kpIndex].pt.x;
			y = kp1Array[kpIndex].pt.y;
			Point p = new Point(x, y);
			points1[i] = p;

			kpIndex = matchArray[i].trainIdx;
			x = kp2Array[kpIndex].pt.x;
			y = kp2Array[kpIndex].pt.y;
			p = new Point(x, y);
			points2[i] = p;

		}
		Mat fundamentalMatrix = Calib3d.findFundamentalMat(new MatOfPoint2f(points1), new MatOfPoint2f(points2),Calib3d.FM_RANSAC,3,0.99);
		List<DMatch> inlierMatches = new ArrayList<DMatch>();
		for (int i = 0; i < numMatches; i++) {

			Mat m1 = new Mat(1,3,CvType.CV_64F);
			m1.put(0, 0, points1[i].x);
			m1.put(0, 1, points1[i].y);
			m1.put(0, 2, 1.0);
			
			Mat m2 = new Mat(1,3,CvType.CV_64F);
			m2.put(0, 0, points2[i].x);
			m2.put(0, 1, points2[i].y);
			m2.put(0, 2, 1.0);

	
			Mat m2Transposed = new Mat();
			Core.transpose(m2,m2Transposed);
			Mat zeroMatrix = new Mat(1,3,CvType.CV_64F);
			zeroMatrix.setTo(new Scalar(0));
			
			Mat tempMat = new Mat();
			Core.gemm(fundamentalMatrix, m2Transposed, 1,zeroMatrix, 0, tempMat);
		
			Mat resMat = new Mat();
			Core.gemm(m1,tempMat, 1, zeroMatrix, 0, resMat);
			
			double res[] = resMat.get(0, 0);
			if(res[0] < 0.1){
				inlierMatches.add(matchArray[i]);
			}
		}
		DMatch[] inlierMatchesArray = new DMatch[inlierMatches.size()];
		inlierMatches.toArray(inlierMatchesArray);
		outMatches.fromArray(inlierMatchesArray); 
		
		return fundamentalMatrix;

	}

}
