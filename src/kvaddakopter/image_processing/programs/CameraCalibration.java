package kvaddakopter.image_processing.programs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
/**
 * @see<a href="http://docs.opencv.org/trunk/doc/py_tutorials/py_calib3d/py_calibration/py_calibration.html#calibration">Lé camerat calibracíon
 */
public class CameraCalibration extends ProgramClass{


	public CameraCalibration(int threadid, Mainbus mainbus) {
		super(threadid, mainbus);

	}
	Size mPatternSize; 

	Point3[] mObjectPoints;
	Mat 	mObjPoints;
	List<Mat> mImgPointsList;
	List<Mat> mObjPointsList;

	Mat calibImg;


	final static int DESIRED_FRAMES = 12;
	int mFrameNumber = 0;


	@Override
	public void init() {

		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();
		mDecoder.initialize("tcp://192.168.1.1:5555");

		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		setSleepTime(400);

		//Open window 
		openVideoWindow();

		mPatternSize = new Size(7,6);
		mImgPointsList = new ArrayList<Mat>();
		mObjPointsList = new ArrayList<Mat>();

		int numPoints = (int)(mPatternSize.height*mPatternSize.width);
		mObjectPoints = new Point3[numPoints];
		Mat mObjPoints = new Mat(mPatternSize,CvType.CV_32FC2);
		for (int i = 0; i < mObjectPoints.length; i++) {

			int x = i / (int)(mPatternSize.width);
			int y = i % (int)(mPatternSize.width);
			mObjPoints.put(x, y, new double[]{x,y}/*,0}*/);

			mObjectPoints[i] = new Point3();
			mObjectPoints[i].x  =i / (int)(mPatternSize.width);
			mObjectPoints[i].y = i %  (mPatternSize.width);
			mObjectPoints[i].z = 0;
		}
	}

	@Override
	protected void update() {



		if(mFrameNumber < DESIRED_FRAMES){
			//Convert image
			Mat image = getNextFrame(); 
			Mat gray = ImageConversion.toGrey(image);

			//Fins chessboard corners in current frame 
			MatOfPoint2f corners = new MatOfPoint2f();
			boolean patternFound = Calib3d.findChessboardCorners(gray, mPatternSize, corners);


			if(patternFound){

				System.out.println(
						"Frame number: " + mFrameNumber + "\n" +
								"Corners Size:\n" +
								"W: "+ corners.size().height + "\n" +
								"H: "+ corners.size().width
						);

				mFrameNumber++;
				TermCriteria criteria = new TermCriteria(TermCriteria.MAX_ITER + TermCriteria.EPS,30,0.001);
				Imgproc.cornerSubPix(gray, corners, new Size(5,5), new Size(-1,-1), criteria);

				mImgPointsList.add(corners);
				Mat apa = new MatOfPoint3f(mObjectPoints);
				mObjPointsList.add(apa);

				// Displaying detected pattern
				Calib3d.drawChessboardCorners(gray, mPatternSize, corners, patternFound);			
				updateJavaWindow(ImageConversion.mat2Img(gray));

				//Save last calibration image for validation of the camera matrix
				if(mFrameNumber == DESIRED_FRAMES -1)
					calibImg = image.clone();

				// Clean up
				gray.release();
				image.release();

			}else{
				updateJavaWindow(ImageConversion.mat2Img(gray));
			}
		}else if(mFrameNumber == DESIRED_FRAMES){

			Mat image = getNextFrame(); 
			Size imgSize = image.size();

			Mat cameraMatrix = new Mat();
			Mat distCoeffs	 = new Mat();

			List<Mat> rvecs = new ArrayList<Mat>();
			List<Mat> tvecs = new ArrayList<Mat>();

			Calib3d.calibrateCamera(mObjPointsList, mImgPointsList, imgSize, cameraMatrix, distCoeffs, rvecs, tvecs);
			Mat optimalCamMatrix = Calib3d.getOptimalNewCameraMatrix(cameraMatrix, distCoeffs, imgSize, 0.5);

			//Create an undistorted image and write it to file
			Mat undistortedImage = new Mat(calibImg.size(),calibImg.type());
			Imgproc.undistort(calibImg, undistortedImage, cameraMatrix, distCoeffs, optimalCamMatrix);
			Highgui.imwrite("cam_undistorted.png", undistortedImage);

			//Export camera paraters to file
			String output = new String();
			List<Mat> tempList = new ArrayList<Mat>();
			
			output += "Camera Matrix: \n";
			tempList.add(optimalCamMatrix.row(0));
			tempList.add(optimalCamMatrix.row(1));
			tempList.add(optimalCamMatrix.row(2));
			String camMatrixString = listOfMatToString(tempList);
			output+=camMatrixString;
			tempList.clear();
			
			output += "Distorion Coefficients: \n";
			tempList.add(distCoeffs);
			String distCoeffString = listOfMatToString(tempList);
			output+=distCoeffString;
			tempList.clear();
			
			output += "rvecs: \n";
			String rvecsString = listOfMatToString(rvecs);
			output+=rvecsString;
			
			output += "tvecs: \n";
			String tvecsString = listOfMatToString(tvecs);
			output+=tvecsString;
			
			PrintWriter writer;
			try {
				writer = new PrintWriter("camera_parameters.txt", "UTF-8");
				writer.println(output);
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}
	}
	private String listOfMatToString(List<Mat> src){
		String dst = new String();
		dst += " new double[][]{ \n";
		Size size;
		int xDim,yDim;
		for(Mat m: src){
			size = m.size();

			xDim = (int)size.width;
			yDim = (int)size.height;

			dst += "{";
			
			for (int i = 0; i < yDim; i++) {
				
				for (int j = 0; j < xDim; j++) {
					double[] elem = m.get(i, j);
					for (int k = 0; k < elem.length; k++) {
						dst += elem[k];
					}
					if(j < xDim-1)
						dst += ",";
				}
				if(i < yDim-1)
					dst += ",";
				
			}
			dst += "}";
			int index = src.indexOf(m);
			if(index < src.size()-1)
				dst += ",\n";
			else
				dst += "\n";
		}
		dst += "};\n";
		return dst;
		
	}
}



