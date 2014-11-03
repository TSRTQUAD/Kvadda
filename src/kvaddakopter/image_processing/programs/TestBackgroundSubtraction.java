package kvaddakopter.image_processing.programs;

import java.awt.image.BufferedImage;

import kvaddakopter.image_processing.algorithms.BackgroundSubtraction;
import kvaddakopter.image_processing.data_types.ImageObject;
import kvaddakopter.image_processing.decoder.FFMpegDecoder;
import kvaddakopter.image_processing.utils.ImageConversion;

import org.opencv.core.Mat;

public class TestBackgroundSubtraction extends ProgramClass{


	protected void init() {

		//Create and initialize decoder. And select source.
		mDecoder = new FFMpegDecoder();
		mDecoder.initialize("tcp://192.168.1.1:5555");

		// Listen to decoder events
		mDecoder.setDecoderListener(this);

		//Start stream on a separate thread
		mDecoder.startStream();

		//Open window 
		openVideoWindow();

		//Selecting method/algorithm
		mCurrentMethod = new BackgroundSubtraction();

	}
	
	@Override
	protected void update() {
		Mat currentImage = getNextFrame();

		ImageObject imageObject = new ImageObject(currentImage);

		mCurrentMethod.start(imageObject);

		if(mCurrentMethod.hasIntermediateResult()){
			Mat output = mCurrentMethod.getIntermediateResult();
			//Convert Mat to BufferedImage
			BufferedImage out = ImageConversion.mat2Img(output);
			output.release();
			updateJavaWindow(out);
		}
	}
	
}
