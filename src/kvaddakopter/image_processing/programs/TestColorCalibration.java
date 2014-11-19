package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import com.xuggle.xuggler.demos.VideoImage;

public class TestColorCalibration extends ProgramClass{
	
	public ColorTemplate cTemplate;

	public TestColorCalibration(int threadid, MainBusIPInterface mainbus) {
		super(threadid, mainbus);
	}

	Tracking tracker;
	private static VideoImage trackingWindow = null;
	
	public void init() {
		//Create image queue, which is a list that is holding the most recent
		// images
		mImageQueue = new ArrayList<BufferedImage>();

		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();
		mDecoder.initialize("tcp://192.168.1.1:5555");
		//mDecoder.initialize("rtsp://130.236.214.20:8086");
		//mDecoder.initialize("mvi2.mp4");
		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window 
		openVideoWindow();
		
		mCurrentMethod = new ColorDetection();

	}
	
	public Mat thresholdImage(ImageObject imageObject, ColorTemplate template){
		Mat  thresh = new Mat();
		Mat  HSVImage= new Mat();
		
		// Convert RGB to HSV
		Imgproc.cvtColor(imageObject.getImage(), HSVImage, Imgproc.COLOR_BGR2HSV);
		//Threshold
		Core.inRange(HSVImage, template.getLower(), template.getUpper(), thresh);
		return thresh;
	}

	public void update()  {

		Mat currentImage = getNextFrame();
		Mat result= new Mat();

		ImageObject imageObject = new ImageObject(currentImage);
		
		cTemplate = mMainbus.getIPCalibTemplate();
		result = thresholdImage(imageObject, cTemplate);
		
		BufferedImage out = ImageConversion.mat2Img(result);
		updateJavaWindow(out);
		}
}
