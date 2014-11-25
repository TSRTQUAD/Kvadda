
package kvaddakopter.image_processing.algorithms;

import java.util.Vector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;

import kvaddakopter.image_processing.data_types.ImageObject;

/**
 * Class which has the purpose of containing and calculating the 
 * bluriness level of an image
 *
 */
public class BlurDetection{
	private static final float MAX_EDGE_INTENSITY = 200;
	private static final int EDGE_THRESHOLD = 50;
	private static final int DEFAULT_SQUARE_SIZE = 21;
	private static final int X_DIRECTION = 0;
	private static final int Y_DIRECTION = 1;
	private Mat mGradXImage;
	private Mat mGradYImage;
	
	private float mVBlurLevel; //between 0:1
	private float mHBlurLevel; //between 0:1
	
	/**
	 * Standard constructor
	 */
	public BlurDetection(){
		mVBlurLevel = 0;
		mHBlurLevel = 0;
		mGradXImage = new Mat();
		mGradYImage = new Mat();
	}
	
	/**
	 * Calculates horisontal respectively vertical blur levels
	 * The method uses sobel edge derivatives as a meassure of edge sharpness
	 * A sharper edge gives higher sobel derivative.
	 * 
	 * The local maximas of the edges are computed
	 * This is then flipped and normalised with respect to maximum edge intensity
	 * 
	 * The blur level is between [0:1] where 1 is maximum blur
	 * 
	 * @param imageObject image container to process
	 * @return Mean of blur in both directions
	 */
	public float runMethod(ImageObject imageObject){
		Mat greyImage = new Mat();
		Mat absGradX = new Mat();
		Mat absGradY = new Mat();
		int ddepth = CvType.CV_16S;
		
//		if(imageObject.getImage().empty())
//		   	return 0;
//		else if(imageObject.getImage().channels()>1)
		Imgproc.cvtColor(imageObject.getImage(), greyImage, Imgproc.COLOR_BGR2GRAY);
//		else grayImage = imageObject.getImage();
		
		/// X gradient
		 //Imgproc.Sobel( greyImage, mGradXImage, ddepth, 1, 0, 3, scale, delta, Imgproc.BORDER_DEFAULT);
		 Imgproc.Sobel(greyImage, mGradXImage, ddepth, 1, 0);
		 Core.convertScaleAbs( mGradXImage, absGradX );
		 
		/// X gradient
		 Imgproc.Sobel(greyImage, mGradYImage, ddepth, 0, 1);
		 Core.convertScaleAbs( mGradYImage, absGradY);
		 
		 //Get local maximas in X
		 Vector<Float> localXMaximas = localMaximas(absGradX,DEFAULT_SQUARE_SIZE,X_DIRECTION,EDGE_THRESHOLD);
		 float locXMaxMean = calculateMean(localXMaximas);
		 
		 mHBlurLevel = 1-locXMaxMean/MAX_EDGE_INTENSITY; //Get a normalised value 0:1 where 1 is high blur level
		 
		 //Get local maximas in Y
		Vector<Float> localYMaximas = localMaximas(absGradX,DEFAULT_SQUARE_SIZE,Y_DIRECTION,EDGE_THRESHOLD);
		float locYMaxMean = calculateMean(localYMaximas);
		 
		mVBlurLevel = 1-locYMaxMean/MAX_EDGE_INTENSITY; //Get a normalised value 0:1 where 1 is high blur level
		
		imageObject.setBlurLevels(mHBlurLevel, mVBlurLevel);
		 
		return (mVBlurLevel+mHBlurLevel)/2;
	}
	
	/**
	 * Find local maxima in x or y direction
	 * @param src image mat to find local maximas in
	 * @param squareSize The number of pixels to be processed in each step
	 * @param direction BlurDetection.X_DIRECTION or BlurDetection.Y_DIRECTION
	 * @return Vector<Float> local maximas of image
	 */
	private Vector<Float> localMaximas(Mat src,int squareSize,int direction, int threshold){
		Vector<Float> listOfLocMax = new Vector<Float>();
		if(squareSize % 2 == 0){
			return listOfLocMax;
		}
		
		int height = src.rows();
		int width = src.cols();
		int searchWidth = width/squareSize;
		int searchHeight = height/squareSize;
		int squareStart, squareEnd;
		switch(direction){
			case X_DIRECTION:{
				for(int row = 0; row < height-1 ; row++){
					for(int x=0; x < searchWidth; x++){
						squareStart = squareSize*x;// + squareSize/2+1;
						squareEnd = squareStart+squareSize;
						//System.out.println("squareStart " + squareStart + " - " + "squareEnd: " + squareEnd);
						//System.out.println("row " + row);
						
						Mat square = new Mat();
						square = src.submat(row, row+1,squareStart, squareEnd);
						//System.out.println(square.get(0, 0));
						MinMaxLocResult temp = Core.minMaxLoc(square);
						//System.out.println("local maxima: " + temp.maxVal);
						if(temp.maxVal > threshold){
							listOfLocMax.add((float) temp.maxVal);
							//System.out.println("Number of maximas: " + listOfLocMax.size());
						}
					}	
				}
			}
			case Y_DIRECTION:{
				for(int col = 0; col < width-1;col++){
					for(int y=0; y < searchHeight; ++y){
						squareStart = squareSize*y;// + squareSize/2+1;
						squareEnd = squareStart+squareSize;
						//System.out.println("squareStart " + squareStart + " - " + "squareEnd: " + squareEnd);
						//System.out.println("col " + col);
						
						Mat square = new Mat();
						square = src.submat(squareStart, squareEnd,col, col+1);
						//System.out.println(square.get(squareStart,col));
						MinMaxLocResult temp = Core.minMaxLoc(square);
						//System.out.println("local maxima: " + temp.maxVal);
						if(temp.maxVal > threshold){
							listOfLocMax.add((float) temp.maxVal);
							//System.out.println("Number of maximas: " + listOfLocMax.size());
						}
					}	
				}
			}
		}
		return listOfLocMax;
	}
	
	/**
	 * Calculate mean of vector containing Floats
	 * @param src
	 * @return
	 */
	private float calculateMean(Vector<Float> src){
		float temp = 0;
		if(src.size()==0)
			return 0;
		
		for(Float f:src){
			temp +=f;
		}
		return temp/src.size();
	}
	
	/**
	 * Get the image with horisontal edges
	 * @return
	 */
	public Mat getGradXImage(){
		return mGradXImage;
	}
	
	/**
	 * Get the image with vertical edges
	 * @return
	 */
	public Mat getGradYImage(){
		return mGradYImage;
	}
	
	/**
	 * 
	 * Container class for horisontal respectively vertical motion blur levels
	 *
	 */
	public class BlurLevels{
		public float h;
		public float v;
	}
}

