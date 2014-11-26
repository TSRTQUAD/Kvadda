package kvaddakopter.image_processing.test_programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.programs.ProgramClass;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Mat;

import com.xuggle.xuggler.demos.VideoImage;


public class TestColorDetection extends ProgramClass{


	public TestColorDetection(int threadid, MainBusIPInterface mainbus) {
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
		//mDecoder.initialize("tcp://192.168.1.1:5555");
		//mDecoder.initialize("rtsp://130.236.214.20:8086");
		mDecoder.initialize("mvi3.mp4");
		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window 
		openVideoWindow();

		trackingWindow = new VideoImage(); // put in Tracking?
		tracker = new Tracking();
		
		mCurrentMethod = new ColorDetection();

	}

	public void update()  {

		Mat currentImage = getNextFrame();

		ImageObject imageObject = new ImageObject(currentImage);
		
		//((ColorDetection) mCurrentMethod).addTemplate("Yellow square", 20, 40, 100, 255, 100, 255, ColorTemplate.FORM_SQUARE);
		ArrayList<TargetObject> targetList = mCurrentMethod.runMethod(imageObject);

		int resWidth = 0;
		int resHeight = 0;
		
		if(mCurrentMethod.hasIntermediateResult()){

			Mat output = mCurrentMethod.getIntermediateResult();
			//Convert Mat to BufferedImage
			BufferedImage out = ImageConversion.mat2Img(output);
			resWidth = out.getWidth();
			resHeight = out.getHeight();
			output.release();
			updateJavaWindow(out);
			
			tracker.update(targetList);
			Mat outputT = tracker.getImage(resHeight, resWidth, currentImage);
			BufferedImage outT = ImageConversion.mat2Img(outputT);
			trackingWindow.setImage(outT);

		}		
	}
}
