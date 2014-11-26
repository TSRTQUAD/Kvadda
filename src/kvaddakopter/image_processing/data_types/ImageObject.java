package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;
import java.util.List;
import kvaddakopter.image_processing.utils.MatchesHelpFunctions;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

public class ImageObject {

	// 
	private Mat mImage;

	// Key points (referred as points of interest in the design specification)
	private MatOfKeyPoint mKeyPoints;

	// Descriptors
	private Mat mDescriptors;

	private BlurLevels mBlurLevels;

	/**
	 * Constructor
	 * @param image 
	 */
	public ImageObject(Mat image) {
		mImage = image;
		mBlurLevels = new BlurLevels();
	}

	public void setImage(Mat image){
		mImage = image;
	}

	public Mat getImage(){
		return mImage;
	}



	//Key points 
	/**
	 * Computes points of interest (key points) of the image
	 * @param detectingMethod is the detecting method, see {@link FeatureDetector} class for which alternative to chose from.  
	 * Eg: FeatureDetector.SIFT
	 * @return
	 */
	public MatOfKeyPoint computeKeyPoints(int detectingMethod){

		//Feature Detector
		FeatureDetector featureDetector = FeatureDetector.create(detectingMethod);

		//Create new instance of key points
		mKeyPoints = new MatOfKeyPoint();

		//Detect key points in image
		featureDetector.detect(mImage, mKeyPoints);

		return mKeyPoints;
	}

	public MatOfKeyPoint getKeyPoints(){
		return mKeyPoints;
	}

	public boolean hasKeyPoints(){
		return mKeyPoints !=null;
	}

	// Descriptors
	/**
	 * Computes descriptors for each points of interest (key points).
	 * @param extractingMethod is the extracting method, see {@link DescriptorExtractor} class in order to find suitable methods. 
	 * Eg: DescriptorExtractor.SIFT
	 * 
	 * @return
	 */
	public Mat computeDescriptors(int extractingMethod){

		DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(extractingMethod);

		mDescriptors = new Mat();

		if(mKeyPoints == null)
			System.err.print("Error - do NOT extract descriptors before computing keypoints\n");


		descriptorExtractor.compute(mImage, mKeyPoints, mDescriptors);

		return mDescriptors;
	}
	/*
	 * 
	 */
	public boolean hasDescriptors(){
		return mDescriptors != null;
	}

	public Mat getDescriptors(){
		return mDescriptors;
	}


	/**
	 * Find keypoint matches/correspondances between this ImageObject and an 
	 * external ImageObject.
	 * 
	 * 
	 * @param externalImageObject
	 * @return
	 */
	public MatOfDMatch findMatches(ImageObject externalImageObject,int minNumberOfMatches){
		ImageObject internalImageObject = this;

		//First a check if key points and descriptors has been computed for 
		// both ImageObject's. If not, they are computed.

		if(!externalImageObject.hasKeyPoints())
			System.err.println("ImageObject: Keypoints has not been computed returning null");	
		
		if(!externalImageObject.hasDescriptors())
			System.err.println("ImageObject: Descriptors has not been computed returning null");

		if(!internalImageObject.hasKeyPoints())
			System.err.println("ImageObject: Keypoints has not been computed returning null");
		
		if(!internalImageObject.hasDescriptors())
			System.err.println("ImageObject: Descriptors has not been computed returning null");
						
		//If we dont have any keypoint return an empty MatOfDMatch
		if(internalImageObject.numberOfKeyPoints() <= 0 ||
				externalImageObject.numberOfKeyPoints() <= 0)
			return null;


		//Find matches (referred as correspondences in the design specification)
		DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

		List<MatOfDMatch> correspondences1 = new ArrayList<MatOfDMatch>();
		descriptorMatcher.knnMatch(
				internalImageObject.getDescriptors(), 
				externalImageObject.getDescriptors(), 
				correspondences1,
				2
				);

		List<MatOfDMatch> correspondences2 = new ArrayList<MatOfDMatch>();
		descriptorMatcher.knnMatch(
				externalImageObject.getDescriptors(),
				internalImageObject.getDescriptors(), 
				correspondences2,
				2
				);

		MatchesHelpFunctions.ratioTest(correspondences1);
		//MatchTests.ratioTest(correspondences2);

		MatOfDMatch matches = MatchesHelpFunctions.symmetryTest(correspondences1, correspondences2);

		//If we less than minNumberOfMatches return null
		int cols = matches.cols();
		int rows = matches.rows();

		if(cols*rows < minNumberOfMatches)
			return null;

		return matches;
	}

	private int numberOfKeyPoints() {
		return (int)( mKeyPoints.size().height*mKeyPoints.size().width);
	}

	/**
	 * Container for blur levels in horisontal/vertical directions
	 *
	 */
	public class BlurLevels{
		public float h;
		public float v;
	}

	public BlurLevels getBlurLevels(){
		return mBlurLevels;
	}

	public void setBlurLevels(float h, float v){
		mBlurLevels.h = h;
		mBlurLevels.v = v;
	}

	/**
	 * Thresholds image with HSV thresholds from ColorTemplate
	 * @param imageObject
	 * @param template
	 * @return
	 */
	public void thresholdImage(ColorTemplate template){
		Mat  HSVImage= new Mat();

		// Convert RGB to HSV
		Imgproc.cvtColor(mImage, HSVImage, Imgproc.COLOR_BGR2HSV);
		//Threshold
		Scalar lowerBounds;
		Scalar upperBounds;
		synchronized(template){
			lowerBounds = template.getLower().clone();
			upperBounds = template.getUpper().clone();
		}

		Core.inRange(HSVImage, lowerBounds, upperBounds, mImage);
	}
	/**
	 * Clean up image object.
	 */
	public void release() {
		if(mImage != null)
			mImage.release();
		if(mKeyPoints != null)
			mKeyPoints.release();
		if(mDescriptors != null)
			mDescriptors.release();
	}

}
