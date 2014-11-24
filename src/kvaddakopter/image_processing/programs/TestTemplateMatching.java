package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import kvaddakopter.image_processing.algorithms.TemplateMatch;
import kvaddakopter.image_processing.algorithms.Tracking;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.data_types.TargetObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Mat;

import com.xuggle.xuggler.demos.VideoImage;


public class TestTemplateMatching extends ProgramClass{

	public TestTemplateMatching(int threadid, MainBusIPInterface mainbus) {
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

		mCurrentMethod = new TemplateMatch();
//		((TemplateMatch)mCurrentMethod).addNewTemplateImage("kaffe.jpg");

	}

	public void update()  {

		Mat currentImage = getNextFrame();
		ImageObject imageObject = new ImageObject(currentImage);

		ArrayList<TargetObject> targetList = mCurrentMethod.runMethod(imageObject);


		if(mCurrentMethod.hasIntermediateResult()){
			Mat output = mCurrentMethod.getIntermediateResult();
			BufferedImage out = ImageConversion.mat2Img(output);
			output.release();
			updateJavaWindow(out);
		}
		
		imageObject.release();
	}
}
