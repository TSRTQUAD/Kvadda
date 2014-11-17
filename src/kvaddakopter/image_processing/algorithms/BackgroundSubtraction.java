package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kvaddakopter.ImageProcessingMain;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.image_processing.utils.MatchTests;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class BackgroundSubtraction  extends DetectionClass{
	ImageObject mPreviousImageData;
	ImageObject mBackgroundImageData;
	/*Constants & Parameters: */

	//Subtraction
	static final int BLUR_KERNEL_SIZE 	 = 21;
	static final int THRESHOLD_LOWEST_LEVEL 	 = 71;

	//Morphology 
	static final int MORPH_KERNEL_SIZE = 33;
	static final int MORPH_KERNEL_TYPE = Imgproc.MORPH_ELLIPSE;

	//Contours
	static final double CONTOUR_AREA_LOWER_THRESHOLD = 0.01;
	double mContourAreaThreshold = CONTOUR_AREA_LOWER_THRESHOLD;
	@Override
	public ArrayList<TargetObject> start(ImageObject currentImageData) {

		// Compute key points and descriptors for the current image
		currentImageData.computeKeyPoints(FeatureDetector.SURF);
		currentImageData.computeDescriptors(DescriptorExtractor.ORB);


		if(mPreviousImageData == null) {

			// No previous image data to work with. 
			// Store the current image object as the previous object.
			mPreviousImageData = currentImageData;


			mBackgroundImageData = new ImageObject(currentImageData.getImage().clone());
			// Assigning initial background
			Mat firstBackgroundImage = mBackgroundImageData.getImage();

			Mat grayBackground = new Mat(); 
			Imgproc.cvtColor(firstBackgroundImage, grayBackground, Imgproc.COLOR_RGB2GRAY);

			// Do some blur 
			Mat blurredBackground = new Mat(grayBackground.rows(),grayBackground.cols(),grayBackground.type());
			Size kernelSize = new Size(BLUR_KERNEL_SIZE,BLUR_KERNEL_SIZE);
			Imgproc.GaussianBlur(blurredBackground,firstBackgroundImage,kernelSize,0);


			// Terminate function by returning null.
			return null;
		}

		//1. Correspondences
		MatOfDMatch matches       = currentImageData.findMatches(mPreviousImageData);

		//2. Find fundamental matrix
		MatOfDMatch inlierMatches = new MatOfDMatch();
		Mat fundamentalMatrix 	  = MatchTests.computeFundamentalMatrix(matches, currentImageData.getKeyPoints(), mPreviousImageData.getKeyPoints(),0.1, inlierMatches);
		if(fundamentalMatrix != null){

			//2.a Getting inlier keypoints
			MatOfKeyPoint kpCurrent = new MatOfKeyPoint();
			MatOfKeyPoint kpPrev = new MatOfKeyPoint();
			MatchTests.getMatchingKeyPoints(currentImageData.getKeyPoints(), mPreviousImageData.getKeyPoints(), kpCurrent, kpPrev, inlierMatches);

			Mat matchesImg= currentImageData.getImage().clone();
//			Features2d.drawMatches(
//					currentImageData.getImage(),
//					kpCurrent, 
//					mPreviousImageData.getImage(),
//					kpPrev,  
//					inlierMatches,
//					matchesImg
//					);
//			§
			
			for (int i = 0; i < kpCurrent.rows(); i++) {
				for (int j = 0; j < kpCurrent.cols(); j++) {
					
					Core.line(matchesImg, new Point(kpCurrent.get(i, j)), new Point(kpPrev.get(i, j)), new Scalar(255,255,255));
					
					double[] p1 = kpCurrent.get(i, j);
					double[] p2 = kpPrev.get(i, j);
					double dist = Math.sqrt(Math.pow(p1[0]-p2[0],2.0) + Math.pow(p1[1]-p2[1],2.0));
					if(dist > 40)
						System.out.println("Distance: " +dist);
				}
			}
			//2.b Computing Homography matrix using inlier keypoints. number of inlier point must be more than 4
			if(inlierMatches.cols() *inlierMatches.rows() >=4){
				Mat homo = MatchTests.getHomoMatrix(kpCurrent, kpPrev);
				//2.c Warp Image
				Mat homoImg = new Mat();
				Imgproc.warpPerspective(mPreviousImageData.getImage(), homoImg,homo, mPreviousImageData.getImage().size());

				//Output
				mIntermeditateResult = new Mat();
				List<Mat> concatMat = new ArrayList<Mat>();
				concatMat.add(homoImg);
				concatMat.add(matchesImg);
				Core.hconcat(concatMat,mIntermeditateResult);
			}

		}

		//2. Camera Matrix blabla. - NOT IMPLEMENTED

		//3. Warp - NOT IMPLEMENTED
		//		warpPreviousImage();
		//
		//		//4. Background subtraction
		//		Mat movingForeground = subtractBackground(currentImageData);
		//
		//		//5. Morphology
		//		Mat morphedImage = morphBinaryImage(movingForeground);
		//
		//		//6. Find contours 
		//		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//		//Find contours that have an normalized area that is larger than CONTOUR_AREA_LOWER_THRESHOLD; 
		//		Mat contourImage = findContours(morphedImage, contours);
		//		//Dilate the image once more in order smaller contours into larger ones 
		//		Mat diletedImage = morphBinaryImage(contourImage);
		//		//Find contour once again
		//		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
		//
		//		for(MatOfPoint c: contours){
		//			Moments m = Imgproc.moments(c);
		//			double[] targetPosition = new double[2];
		//			targetPosition[0] = m.get_m10()/m.get_m00();
		//			targetPosition[1] = m.get_m10()/m.get_m00();
		//			Core.circle(diletedImage, new Point(targetPosition), 8, new Scalar(0.5f));
		//			//			TargetObject newTarget = new TargetObject(position_, noise_level);
		//
		//		}
		//		//
		//		//Output
		//		List<Mat> concatMat = new ArrayList<Mat>();
		//		concatMat.add(morphedImage);
		//		concatMat.add(diletedImage);
		//		mIntermeditateResult = new Mat();
		//		Core.hconcat(concatMat,mIntermeditateResult);

		//6. Bounding Boxes
		//Get contours and hierarchy of binary image


		//				ArrayList<Rect> boundingBoxes = getBoundingBoxes(contours,hierarchy,0.1);

		//Print boxes on a gray scale Image
		//		Mat currentFrameGray = new Mat(); 
		//		Imgproc.cvtColor(currentImageData.getImage(), currentFrameGray, Imgproc.COLOR_RGB2GRAY);
		//		mIntermeditateResult = currentFrameGray;
		//		for (Rect rect : boundingBoxes) {
		//			Core.rectangle(mIntermeditateResult, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,0),4);
		//		}

		//7. Adjust parameters


		// TODO: Assign the box data to the TargetObjects
		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();
		/*	Mat m = new Mat(1,2,CvType.CV_64F);
		m.put(0, 0, 0);
		m.put(0, 1, 1);
		TargetObject target = new TargetObject(m, 0);
		 */
		//Set previous image data to the current image data
		mPreviousImageData = currentImageData;
		//		mBackgroundImageData = currentImageData;
		return targetObjects;
	}


	private void warpPreviousImage(){
	}



	/**
	 * Only dilation with a large ass kernel for now...
	 * 
	 * @param binaryImage
	 * @return
	 */
	private Mat morphBinaryImage(Mat binaryImage){

		//Dilate Kernel
		Size size = new Size(MORPH_KERNEL_SIZE,MORPH_KERNEL_SIZE);
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,size);

		//Dilate in order to merge nearby blobs
		Mat dilatedImage = new Mat(binaryImage.rows(),binaryImage.cols(),binaryImage.type());
		Imgproc.morphologyEx(binaryImage, dilatedImage, Imgproc.MORPH_DILATE, kernel);
		return dilatedImage;
		//		//Closing Kernel
		//		Size sizeClose = new Size(MORPH_KERNEL_SIZE,MORPH_KERNEL_SIZE);
		//		Mat kernelClose = Imgproc.getStructuringElement(MORPH_KERNEL_TYPE,sizeClose);
		//
		//		//Closing		
		//		Mat closedImage = new Mat(dilatedImage.rows(),dilatedImage.cols(),dilatedImage.type());
		//		Imgproc.morphologyEx(dilatedImage, closedImage, Imgproc.MORPH_CLOSE, kernelClose);
		//
		//		return closedImage;

	}
	/**
	 * subtractBackground:
	 * To grey:		 	 Only one channel, less computations
	 * Averaging:		 More robust to noise and light variance 
	 * Pixel wise sub:   ...
	 * Thresholding:   	 Remove noise in the background
	 */
	private Mat subtractBackground(ImageObject currentImageData){

		// To gray scale
		Mat currentFrameGray = new Mat(); 
		Imgproc.cvtColor(currentImageData.getImage(), currentFrameGray, Imgproc.COLOR_RGB2GRAY);

		// Averaging, using a gaussian blur filter 
		Size kernelSize = new Size(BLUR_KERNEL_SIZE,BLUR_KERNEL_SIZE);
		Mat currentFrameBlurred = new Mat(currentFrameGray.rows(),currentFrameGray.cols(),currentFrameGray.type());
		Imgproc.GaussianBlur(currentFrameGray,currentFrameBlurred,kernelSize,0);

		// Subtracting
		Mat absDifferenceImage = new Mat();
		Core.absdiff(currentFrameBlurred, mBackgroundImageData.getImage(), absDifferenceImage);

		//Threshold
		double threshold = computeThreshold(absDifferenceImage);
		Mat thresholdedImage = new Mat();
		Imgproc.threshold(absDifferenceImage, thresholdedImage, threshold, 255, Imgproc.THRESH_BINARY);

		//		mIntermeditateResult = new Mat();
		//		//Output
		//		List<Mat> concatMat = new ArrayList<Mat>();
		//		concatMat.add(thresholdedImage);
		//		concatMat.add(absDifferenceImage);
		//		Core.hconcat(concatMat,mIntermeditateResult);

		//Adapt background model
		adaptBackground(currentFrameBlurred);

		return thresholdedImage;

	}
	/**
	 * Selecting a threshold regarding the content of the image.
	 * @param greyImage
	 * @return
	 */
	private double computeThreshold(Mat greyImage){

		//Calc histogram over gray values
		List<Mat> images = new ArrayList<Mat>();
		images.add(greyImage);
		Mat hist = new Mat();
		Imgproc.calcHist(images, new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(0f,256f));

		//Normalize histogram
		Mat histNorm = new Mat();
		Core.normalize(hist, histNorm);

		//Read values from Mat
		double values[] = new double[255]; 
		double sum = 0; 
		for (int i = 0; i < 255; i++) {
			double[] val = hist.get(i+1,0);
			sum 		+=val[0];
			values[i] 	= val[0];
		}
		//Normalize
		for (int i = 0; i < 255; i++) {
			values[i] /= sum;
		}

		double N = 255;
		double expectationValue = 0;
		for (double x = 0; x < N; x++) {
			/*
			double[] val =  hist.get(i+1,0);
			expectationValue +=x*val[0];*/
			expectationValue +=x*values[(int)x];
		}
		//expectationValue/=N;
		//System.out.printf("Exp val: \n " +expectationValue);
		//Selecting threshold:  MAX(80% of the expected grey value,THRESHOLD_LOWEST_LEVEL ) 
		double threshold = Math.max(expectationValue*0.8,THRESHOLD_LOWEST_LEVEL);
		return threshold;
	}

	/**
	 * Adapt background model: <br> 
	 * BACKGROUND[N+1] =  BACKGROUND[N] + k*(IMG[N] - BACKGROUND[N])
	 * @param currentFrameBlurred
	 */
	private void adaptBackground(Mat currentFrameBlurred){
		double adaptSpeed = 0.4;	
		//Reference to background image
		Mat backgroundImg = mBackgroundImageData.getImage();
		int cols = currentFrameBlurred.cols();
		int rows = currentFrameBlurred.rows();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double[] backgroundValue = backgroundImg.get(i,j);
				double[] curFrameValue   = currentFrameBlurred.get(i,j);
				int numElements = backgroundValue.length;
				double[] newBGVal = new double[numElements];
				for (int k = 0; k < numElements; k++) {
					newBGVal[k] = backgroundValue[k] + adaptSpeed*(curFrameValue[k]-backgroundValue[k]);
				}
				backgroundImg.put(i,j,newBGVal);
			}
		}
	}

	private Mat findContours(Mat img, List<MatOfPoint> outContours){
		//Make sure list is empty
		outContours.clear();

		//Temp contour container
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Mat contourImage = img.clone(); //TODO: ta bort clone

		//Find contours
		Imgproc.findContours(contourImage, contours, hierarchy,Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		List<MatOfPoint> contoursFiltered = new ArrayList<MatOfPoint>();

		//Select all contours that have an area larger than a certain threshold
		double numPixels = contourImage.cols()*contourImage.rows();
		int heirarchyCols = hierarchy.cols();
		for (int i = 0; i < heirarchyCols; i++) {
			MatOfPoint contour = contours.get(i);
			//Normalized treshold
			double contourArea = Imgproc.contourArea(contour)/numPixels;
			if(contourArea > CONTOUR_AREA_LOWER_THRESHOLD){
				contoursFiltered.add(contour);
			}else 
				break;
		}

		// Draw them largest areas
		int numFiltCont = contoursFiltered.size();
		Mat filteredImg = new Mat(contourImage.size(),contourImage.type(), new Scalar(0));
		for(int i = 0; i < numFiltCont; i ++){ 
			Imgproc.drawContours(filteredImg, contoursFiltered, 0, new Scalar(255.0),-1);
		}

		//Set output
		outContours = contoursFiltered;
		return filteredImg;

	}

}
