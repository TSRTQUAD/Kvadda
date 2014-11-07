package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;
import java.util.List;

import kvaddakopter.image_processing.utils.MatchTests;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

public class ImageObject {
	
	// 
	private Mat mImage;
	
	// Key points (referred as points of interest in the design specification)
	private MatOfKeyPoint mKeyPoints;
	
	// Descriptors
	private Mat mDescriptors;
	
	
	private float mBlurLevel;
	
	/**
	 * Constructor
	 * @param image 
	 */
	public ImageObject(Mat image) {
		mImage = image;
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
	
// Blur detection
	public float getBlurLevel(){
		return mBlurLevel;
	}
	/**
	 * Find keypoint matches/correspondances between this ImageObject and an 
	 * external ImageObject.
	 * 
	 * 
	 * @param externalImageObject
	 * @return
	 */
	public MatOfDMatch findMatches(ImageObject externalImageObject){
		ImageObject internalImageObject = this;
		
		//First a check if key points and descriptors has been computed for 
		// both ImageObject's. If not, they are computed.
		
		if(!externalImageObject.hasKeyPoints())
			computeKeyPoints(FeatureDetector.FAST);	
		if(!externalImageObject.hasDescriptors())
			externalImageObject.computeDescriptors(DescriptorExtractor.ORB);
		
		if(!internalImageObject.hasKeyPoints())
			computeKeyPoints(FeatureDetector.FAST);
		if(!internalImageObject.hasDescriptors())
			externalImageObject.computeDescriptors(DescriptorExtractor.ORB);
		
		//Find matches (referred as correspondences in the design specification)
		DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		
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

		MatchTests.ratioTest(correspondences1);
		MatchTests.ratioTest(correspondences2);
		
		MatOfDMatch matches = MatchTests.symmetryTest(correspondences1, correspondences2);
		return matches;
	}
	
	
	
	public void detectBlur(){
		
		//TODO: 
		
		// To gray
		
		// Sobel x(y)
		
		// Determine edge width
		
		// Find local maximum
		
		// Compute mean value of every local maximum
		
		// Set blur level 
		
	}
}
