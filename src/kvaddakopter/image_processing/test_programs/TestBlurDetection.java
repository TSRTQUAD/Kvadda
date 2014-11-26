package kvaddakopter.image_processing.test_programs;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import kvaddakopter.image_processing.algorithms.BlurDetection;
import kvaddakopter.image_processing.algorithms.ColorDetection;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.programs.ProgramClass;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Mat;

import com.xuggle.xuggler.demos.VideoImage;


public class TestBlurDetection extends ProgramClass{
	BlurDetection mBlurDetection;

	public TestBlurDetection(int threadid, MainBusIPInterface mainbus) {
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
		
		mBlurDetection = new BlurDetection();

	}

	public void update()  {

		Mat currentImage = getNextFrame();

		ImageObject imageObject = new ImageObject(currentImage);
		
		//((ColorDetection) mCurrentMethod).addTemplate("Yellow square", 20, 40, 100, 255, 100, 255, ColorTemplate.FORM_SQUARE);
		//ArrayList<TargetObject> targetList = mCurrentMethod.runMethod(imageObject);
		mBlurDetection.runMethod(imageObject);
		System.out.println(imageObject.getBlurLevels().h);
		int resWidth = 0;
		int resHeight = 0;
		
		BufferedImage out = ImageConversion.mat2Img(mBlurDetection.getGradXImage());
		resWidth = out.getWidth();
		resHeight = out.getHeight();
		updateJavaWindow(out);
		}
}
