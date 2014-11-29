package kvaddakopter.image_processing.algorithms;

import java.util.ArrayList;

import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.utils.MatchesHelpFunctions;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

public class TemplateMatch  extends DetectionClass{


	ArrayList<FormTemplate> mTemplates;

	public TemplateMatch() {

	}

	public void setTemplates(ArrayList<FormTemplate> templates){
		mTemplates = templates;
	}
	/**
	 *  - Compute features in the incoming image <br>
	 *  - Match with template object <br>
	 *  - Compute homography <br>
	 *  - Determine/Compute ROI in the incoming image 
	 *  - Store detected target object into list
	 *  @param imageObject ALSFNJLGHJNGH
	 *  @return List of targets
	 */
	@Override
	public ArrayList<TargetObject> runMethod(ImageObject imageObject) {

		ArrayList<TargetObject> targetObjects = new ArrayList<TargetObject>();

		/*Copy input image to the result image */
		mIntermeditateResult = imageObject.getImage().clone();

		//Compute KP and descriptors for incoming image
		imageObject.computeKeyPoints(FeatureDetector.SIFT);
		imageObject.computeDescriptors(DescriptorExtractor.SIFT);

		for(FormTemplate template: mTemplates){
			if(template.isActive()){
				int minumumRequiredMatches = 4;
				MatOfDMatch matches = template.getImageObject().findMatches(imageObject, minumumRequiredMatches);
				if(matches != null){

					// Save copy of key points
					MatOfKeyPoint keyPointsTemplate = new MatOfKeyPoint();
					MatOfKeyPoint keyPointsVideo = new MatOfKeyPoint();
					Mat homo = MatchesHelpFunctions.findHomography_EXT(matches, template.getImageObject().getKeyPoints(), imageObject.getKeyPoints(),keyPointsTemplate,keyPointsVideo);

					if(homo != null){
						/* Transform box points */
						Mat transformBoxPoints = new Mat();
						Core.perspectiveTransform(template.getBoxPoints(), transformBoxPoints,homo);

						//    Convert box points from Mat to points //
						int rows = transformBoxPoints.rows();
						int cols = transformBoxPoints.cols();
						ArrayList<Point> boxCorners = new ArrayList<Point>();
						for(int i = 0; i <rows; i++){
							for(int j = 0; j < cols; j++){
								boxCorners.add(new Point(transformBoxPoints.get(i,j)));
							}
						}

						//   Detected object in list //
						ArrayList<Long> matchesList = new ArrayList<Long>();
						long numMatches = matches.width()*matches.height();
						float noiseLevel = 120.5f;
						targetObjects.add(new TargetObject(boxCorners,noiseLevel,template.getId(),numMatches));

						/* For the output image */					
						for (int i = 0; i < 4; i++) {
							Point start = new Point(transformBoxPoints.get(i, 0));
							Point end = new Point(transformBoxPoints.get((i+1) % 4, 0));

							Core.line(mIntermeditateResult, start, end, new Scalar(255,0,0,255), 3);
						}

						/*mIntermeditateResult = new Mat();
					Features2d.drawMatches(
							template.getImageObject().getImage(),
							keyPointsTemplate,  
							imageObject.getImage(),
							keyPointsVideo, 
							matches,
							mIntermeditateResult
							);*/
					}
				}
			}
		}


		return targetObjects;
	}


	/**
	 * Can be used when calibrating template object. Computing features in the template image. The features <br>
	 * will reside within the specified region of interest, described by the template box data.
	 *  
	 * @param kp
	 * @param image
	 * @param cutoffX
	 * @param cutOffY
	 */
	static private Mat calibrateCurrentImage(FormTemplate template){
		Mat image = template.getImageObject().getImage().clone();
		KeyPoint[] kpArray = template.getImageObject().getKeyPoints().toArray();


		int numKeyPoints = kpArray.length;
		double imageWidth = image.cols();
		double imageHeight = image.rows();

		double x[] = new double[numKeyPoints];
		double y[] = new double[numKeyPoints];

		double[] boxCenter = template.getScaledBoxCenter(imageWidth, imageHeight);
		double[] boxSize   = template.getScaledBoxSize(imageWidth, imageHeight);

		boxSize[0] /=2.0;
		boxSize[1] /=2.0;

		for (int i = 0; i < numKeyPoints; i++) {
			x[i] = kpArray[i].pt.x;
			y[i] = kpArray[i].pt.y;
		}

		int[] indicesTemp = new int[numKeyPoints];
		int ptr = 0;
		for (int i = 0; i < numKeyPoints; i++) {
			double distanceX = Math.abs(x[i] - boxCenter[0]);
			double distanceY = Math.abs(y[i] - boxCenter[1]);
			if(distanceX < boxSize[0] && distanceY < boxSize[1]){	
				indicesTemp[ptr++] = i;
			}
		}
		int indices[] = new int[ptr];
		System.arraycopy(indicesTemp, 0, indices, 0, ptr);
		KeyPoint[] kpArrayRefined = new KeyPoint[indices.length];

		for (int i = 0; i < kpArrayRefined.length; i++) {
			kpArrayRefined[i] = kpArray[indices[i]]; 
		}

		template.getImageObject().getKeyPoints().fromArray(kpArrayRefined);

		Mat boxPoints = getBoxPoints(boxCenter,boxSize,image);
		template.setBoxPoints(boxPoints);
		Features2d.drawKeypoints(image, template.getImageObject().getKeyPoints(), image);

		return image;

	}

	/**
	 * Get box corners
	 * 
	 * @param mean
	 * @param var
	 * @param image
	 * @param discardDistanceX
	 * @param discardDistanceY
	 */
	static private Mat getBoxPoints(double[] mean,double [] var,Mat image){
		double xMean = mean[0];
		double yMean = mean[1];
		double xVar = var[0];
		double yVar = var[1];
		double imageWidth = image.cols();
		double imageHeight = image.rows();
		double startCoord[] = new double[2];
		if(xMean - xVar > 0) startCoord[0]= xMean - xVar;
		if(yMean - yVar > 0) startCoord[1]= yMean - yVar;
		Point start = new Point(startCoord);

		double endCoord[] = new double[2];
		endCoord[0] = imageWidth;
		endCoord[1] = imageHeight;
		if(xMean + xVar < imageWidth) endCoord[0]= xMean +  xVar;
		if(yMean + yVar < imageHeight) endCoord[1]= yMean + yVar;
		Point end = new Point(endCoord);
		Core.rectangle(image, start, end, new Scalar(255, 0, 0, 255), 3);

		double p0[] = new double[]{xMean - xVar,yMean - yVar};
		double p1[] = new double[]{xMean + xVar,yMean - yVar};
		double p2[] = new double[]{xMean + xVar,yMean + yVar};
		double p3[] = new double[]{xMean - xVar,yMean + yVar};

		Mat boxPoints = new Mat(4,1,CvType.CV_64FC2); 
		boxPoints.put(0, 0, p0);
		boxPoints.put(1, 0, p1);
		boxPoints.put(2, 0, p2);
		boxPoints.put(3, 0, p3);
		return boxPoints;
	}


	/**
	 * Calibrate active template.
	 * This function should be called from the GUI template slider class
	 * 
	 * @param formTemplate
	 * @return
	 */
	static public Mat calibrateTemplate(FormTemplate formTemplate) {
		Mat result = null;
		if(formTemplate != null){
			/* Start of with computing properties of the template image */
			formTemplate.getImageObject().computeKeyPoints(FeatureDetector.SIFT);
			result = calibrateCurrentImage(formTemplate);
			formTemplate.getImageObject().computeDescriptors(DescriptorExtractor.SIFT);
		}
		return result;
	}

}


