package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.utils.MatchTests;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class BackgroundSubtraction  extends DetectionClass{
	ImageObject mPreviousImageData;

	/*Constants & Parameters: */
	
	//Subtraction
	static final int BLUR_KERNEL_SIZE 	 = 45;
	static final int THRESHOLD_LEVEL 	 = 30;
	
	//Morphology 
	static final int MORPH_KERNEL_SIZE = 66;
	static final int MORPH_KERNEL_TYPE = Imgproc.MORPH_ELLIPSE;

	
	@Override
	public ArrayList<TargetObject> start(ImageObject currentImageData) {

		// Compute key points and descriptors for the current image
		currentImageData.computeKeyPoints(FeatureDetector.FAST);
		currentImageData.computeDescriptors(DescriptorExtractor.ORB);


		if(mPreviousImageData == null) {
			
			// No previous image data to work with. 
			// Store the current image object as the previous object.
			mPreviousImageData = currentImageData;
			
			// Terminate function by returning null.
			return null;
		}
		
		//1. Correspondences
		MatOfDMatch matches       = currentImageData.findMatches(mPreviousImageData);
		MatOfDMatch inlierMatches = new MatOfDMatch();
		Mat fundamentalMatrix 	  = MatchTests.computeFundamentalMatrix(matches, currentImageData.getKeyPoints(), mPreviousImageData.getKeyPoints(),0.02, inlierMatches);
		mIntermeditateResult = new Mat();
		Features2d.drawMatches(
				currentImageData.getImage(),
				currentImageData.getKeyPoints(), 
				mPreviousImageData.getImage(),
				mPreviousImageData.getKeyPoints(), 
				inlierMatches,
				mIntermeditateResult
				);

		//2. Camera Matrix blabla. - NOT IMPLEMENTED
//		
//		//3. Warp - NOT IMPLEMENTED
//		warpPreviousImage();
//		
//		//4. Background subtraction
//		Mat movingForeground = subtractBackground(currentImageData);
//		
//		
//		//5. Morphology
//		Mat morphedImage = morphBinaryImage(movingForeground);
//
//		//6. Bounding Boxes
//		ArrayList<Rect> boundingBoxes = getBoundingBoxes(morphedImage);
//		
//		//Print boxes on a gray scale Image
//		Mat currentFrameGray = new Mat(); 
//		Imgproc.cvtColor(currentImageData.getImage(), currentFrameGray, Imgproc.COLOR_RGB2GRAY);
//		mIntermeditateResult = currentFrameGray;
//		for (Rect rect : boundingBoxes) {
//			Core.rectangle(mIntermeditateResult, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,0),4);
//		}
//		
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
		
		//Closing Kernel
		/*	Size size = new Size(MORPH_KERNEL_SIZE,MORPH_KERNEL_SIZE);
		Mat kernel = Imgproc.getStructuringElement(MORPH_KERNEL_TYPE,size);
	
		//Closing		
		Mat closedImage = new Mat(dilatedImage.rows(),dilatedImage.cols(),dilatedImage.type());
		Imgproc.morphologyEx(dilatedImage, closedImage, Imgproc.MORPH_CLOSE, kernel);
		*/
		return dilatedImage;
		
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
		mIntermeditateResult = currentFrameGray;

		
		Mat previousFrameGray = new Mat(); 
		Imgproc.cvtColor(mPreviousImageData.getImage(), previousFrameGray, Imgproc.COLOR_RGB2GRAY);


		// Averaging, using a gaussian blur filter 
		Size kernelSize = new Size(BLUR_KERNEL_SIZE,BLUR_KERNEL_SIZE);
		
		Mat currentFrameBlurred = new Mat(currentFrameGray.rows(),currentFrameGray.cols(),currentFrameGray.type());
		Imgproc.GaussianBlur(currentFrameGray,currentFrameBlurred,kernelSize,0);

		Mat previousFrameBlurred = new Mat(previousFrameGray.rows(),previousFrameGray.cols(),previousFrameGray.type());
		Imgproc.GaussianBlur(previousFrameGray,previousFrameBlurred,kernelSize,0);

	
		// Subtracting
		Mat differenceImage = new Mat();
		Core.subtract(currentFrameBlurred,previousFrameBlurred, differenceImage);
		

		// Threshold
		Mat thresholdedImage = new Mat();
		Imgproc.threshold(differenceImage, thresholdedImage, THRESHOLD_LEVEL, 255, Imgproc.THRESH_BINARY);

		return thresholdedImage; 
		
	}
	
	// To make the background subtraction more generic and robust. Some of the parameters might
	// ought to be adjusted during runtime. The light level and background noise might vary a lot
	// depending on the time of the day or the scene.
	
	/**
	 * Adjust threshold value, maybe with regard to sum( thresholded pixels outside of the bounding box(es) )
	 * 
	 * Adjust blur filter kernel size to the noise level of the image.
	 * 
	 * Adjust morphology kernel size to the number of contours detected. If the number of contours is way 
	 * more than we reasonable can expect, we might want to increase the kernel size in order to detect 
	 * fewer but larger blobs.
	 * 
	 * The kernel sizes are also heavily dependent on the image size.
	 * 
	 */
	private void  adjustParameters(){
		
	}

}
